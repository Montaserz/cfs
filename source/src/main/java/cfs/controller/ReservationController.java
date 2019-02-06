package cfs.controller;

import cfs.data.constants.CarStateEnum;
import cfs.data.constants.ReservationStateEnum;
import cfs.data.constants.RoleEnum;
import cfs.data.dao.CarDao;
import cfs.data.dao.ReservationDao;
import cfs.data.entity.Car;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ReservationController implements ReservationControllerInterface {

  @Autowired
  ReservationDao reservationDao;
  @Autowired
  CarDao carDao;
  @Autowired
  EmployeeControllerInterface ec;

  @Override
  public Set<Reservation> findReservations(Employee user) {
    Set<Reservation> reservations = reservationDao.findAll();
    if (ec.hasRole(user, RoleEnum.CEO)) {
      reservations.removeIf(r -> !r.getEmployee().equals(user) && r.getState().equals(ReservationStateEnum.NEW.name()));
    }
    else if (ec.hasRole(user, RoleEnum.DM)) {
      reservations.removeIf(r -> !r.getEmployee().equals(user) && !r.getEmployee().getDepartment().equals(user.getManaging()));
      reservations.removeIf(r -> !r.getEmployee().equals(user) && r.getState().equals(ReservationStateEnum.NEW.name()));
    }
    else {
      reservations = reservationDao.findAllByEmployee(user);
    }
    return reservations;
  }

  @Override
  public Reservation saveReservation(Reservation reservation) {
    return reservationDao.save(reservation);
  }

  @Override
  public List<Reservation> findAllConfirmations(Employee user) {
    Set<Employee> employees = null;
    List<Reservation> reservations = new ArrayList<>();
    
    if (ec.hasRole(user, RoleEnum.CEO)) {
      employees = ec.findEmployees();
    }
    else {
      employees = ec.findEmployees(user.getManaging());
    }
    
    for (Employee e : employees) {
      List<Reservation> empRes = reservationDao.findAllByEmployeeAndEndDateAfter(e, LocalDate.now().minusDays(1));
      reservations.addAll(empRes);
    }
    
    reservations.removeIf(r -> !r.getState().equals(ReservationStateEnum.SUBMITTED.name()));
    
    return reservations;
  }
/*
  @Override
  public List<Reservation> findActiveTrips() {
    return null;
  }

  @Override
  public List<Trip> findPassiveTrips() {
    return null;
  }
  */

  @Override
  public void submitReservation(Reservation reservation) {
    reservation.setState(ReservationStateEnum.SUBMITTED.name());
    reservationDao.save(reservation);
  }

  @Override
  public Set<LocalDate> getInvalidDates() {
    Set<LocalDate> dates = new HashSet<>();
    Map<LocalDate, Integer> dateMap = new HashMap<>();
    Set<Car> cars = carDao.findAll();
    Integer carCnt = 0;
    
    for (Car c : cars){
      for (Reservation r : reservationDao.findAllByCarAndEndDateAfter(c, LocalDate.now())) {
        if (activeReservation(r)) {
          LocalDate start = r.getStartDate();
          LocalDate end = r.getEndDate();

          while (!start.isAfter(end)) {
            if (dateMap.containsKey(start)) dateMap.replace(start, dateMap.get(start)+1);
            else dateMap.put(start, 1);
            start = start.plusDays(1);
          }
        }
      }
      if (!c.getState().equals(CarStateEnum.BROKEN.name())) carCnt++;
    }
    
    for (Map.Entry<LocalDate, Integer> e : dateMap.entrySet()) {
      if (e.getValue() >= carCnt) dates.add(e.getKey());
    }
    
    return dates;
  }

  protected boolean activeReservation(Reservation r) {
    return (r.getState().equals(ReservationStateEnum.READY.name()) || r.getState().equals(ReservationStateEnum.REALIZED.name()));
  }

  @Override
  public LocalDate getMaxEnd(LocalDate value) {
    Set<Car> cars = carDao.findAll();
    LocalDate endDate = null;
    
    for (Car c : cars) {
      List<Reservation> carRes = reservationDao.findAllByCarAndEndDateAfter(c,value);
      if (!c.getState().equals(CarStateEnum.BROKEN.name()) && carRes.isEmpty())
        return null;
      for (Reservation r : carRes){
        if (activeReservation(r)) {
          if (endDate == null || r.getStartDate().isAfter(endDate))
            endDate = r.getStartDate();
        }
      }
    }
    
    return endDate;
  }

  @Override
  public void setConfirmation(Reservation res, boolean b) {
    res.setState(b ? ReservationStateEnum.ACCEPTED.name() : ReservationStateEnum.REJECTED.name());
    
    reservationDao.save(res);
    
    if (!b) return;
    
    assignCar(res);
  }

  protected void assignCar(Reservation res) {
    Set<Car> cars = carDao.findAll();
    cars.removeIf(c -> c.getState().equals(CarStateEnum.BROKEN.name()));
    
    for (Car c : cars) {
      List<Reservation> carRes = reservationDao.findAllByCarAndStartDateBeforeAndEndDateAfter(c, res.getEndDate().plusDays(1), res.getStartDate().minusDays(1));
      carRes.removeIf(r -> !r.getState().equals(ReservationStateEnum.READY.name()));
      if (carRes.isEmpty()) {
        res.setCar(c);
        res.setState(ReservationStateEnum.READY.name());
        reservationDao.save(res);
        return;
      }
    }
    
    res.setState(ReservationStateEnum.FAIL.name());
    reservationDao.save(res);
  }
  
}
