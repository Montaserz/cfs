package cfs.data.dao;

import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import cfs.data.entity.Role;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeDao extends CrudRepository<Employee, Long> {
  @Override
  public Set<Employee> findAll();
  
  public Employee findByEmailAndPassword(String email, String pass);

  public Set<Employee> findAllByDepartment(Department department);

  public Set<Employee> findAllByRole(Role role);
  
}
