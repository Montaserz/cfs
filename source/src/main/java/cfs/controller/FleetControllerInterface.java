package cfs.controller;

import cfs.data.entity.Car;
import cfs.data.entity.Maintenance;
import java.util.List;
import java.util.Set;

public interface FleetControllerInterface {

  public Set<Car> findCars();
  public Car findCarByPlate(String plate);
  public Car addCar(Car car);
  
  public Set<String> getModels();
  /*
  public List<Maintenance> findActiveMaintenance();
  public List<Maintenance> findPassiveMaintenance();

*/
}
