package cfs.data.dao;

import cfs.data.entity.Car;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface CarDao extends CrudRepository<Car, Long> {
  @Override
  public Set<Car> findAll();
  
  public Car findByPlate(String plate);
  
}
