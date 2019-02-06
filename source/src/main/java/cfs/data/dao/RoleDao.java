package cfs.data.dao;

import cfs.data.entity.Employee;
import cfs.data.entity.Role;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface RoleDao extends CrudRepository<Role, Long> {
  @Override
  public Set<Role> findAll();
  
  public Set<Role> findAllByEmployee(Employee employee);

  public Role findByName(String name);
  
}
