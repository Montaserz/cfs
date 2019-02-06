package cfs.data.dao;

import cfs.data.entity.Maintenance;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface MaintenanceDao extends CrudRepository<Maintenance, Long> {
  @Override
  public Set<Maintenance> findAll();  
}
