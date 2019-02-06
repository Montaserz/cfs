package cfs.controller;

import cfs.data.dao.CarDao;
import cfs.data.dao.MaintenanceDao;
import cfs.data.entity.Car;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

@Controller
public class FleetController implements FleetControllerInterface {

  @Autowired
  CarDao carDao;
  @Autowired
  MaintenanceDao maintenanceDao;

  @Override
  public Car findCarByPlate(String plate) {
    return carDao.findByPlate(plate);
  }

  @Override
  public Set<Car> findCars() {
    return carDao.findAll();
  }

  @Override
  public Car addCar(Car car) {
    try {
      return carDao.save(car);
    } catch (DataIntegrityViolationException e) {
      throw new UnsupportedOperationException("Plate Number Collision!");
    }
  }

  @Override
  public Set<String> getModels() {
    Set<String> models = new TreeSet<>();
    Set<Car> cars = (Set<Car>) carDao.findAll();
    for (Car c : cars)
      models.add(c.getModel());
    return models;
  }
/*
  @Override
  public List<Maintenance> findActiveMaintenance() {
    return (List<Maintenance>) maintenanceDao.findAll();
  }

  @Override
  public List<Maintenance> findPassiveMaintenance() {
    return (List<Maintenance>) maintenanceDao.findAll();
  }
  */
}
