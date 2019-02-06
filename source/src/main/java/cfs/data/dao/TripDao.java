package cfs.data.dao;

import cfs.data.entity.Trip;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface TripDao extends CrudRepository<Trip, Long> {
  @Override
  public Set<Trip> findAll();  
}
