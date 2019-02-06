package cfs.data.dao;

import cfs.data.entity.Car;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface ReservationDao extends CrudRepository<Reservation, Long> {
  @Override
  public Set<Reservation> findAll();
  
  public Set<Reservation> findAllByEmployee(Employee user);

  public Set<Reservation> findAllByCar(Car c);

  public List<Reservation> findAllByCarAndEndDateAfter(Car c, LocalDate value);

  public List<Reservation> findAllByEmployeeAndEndDateAfter(Employee e, LocalDate now);

  public List<Reservation> findAllByStartDateBeforeAndEndDateAfter(LocalDate plusDays, LocalDate minusDays);

  public List<Reservation> findAllByCarAndStartDateBeforeAndEndDateAfter(Car c, LocalDate plusDays, LocalDate minusDays);
  
}
