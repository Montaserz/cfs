package cfs.data.dao;

import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface DepartmentDao extends CrudRepository<Department, Long> {
  @Override
  public Set<Department> findAll();

  public Department findByEmployee(Employee employee);
  
}
