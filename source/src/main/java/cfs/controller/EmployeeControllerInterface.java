package cfs.controller;

import cfs.data.constants.RoleEnum;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import java.util.Set;

public interface EmployeeControllerInterface {
  public Department addDepartment(Department department);
  public Department updateDepartment(Department dep1, Department dep2);
  public void deleteDepartment(Department department);
  public Set<Department> findDepartments();
  public Set<Employee> findPossibleManagers(Department department);
  public Set<Employee> findEmployees(Department department);
  
  public Employee addEmployee(Employee employee, Boolean fleetManager);
  public Employee updateEmployee(Employee emp1, Employee emp2, Boolean fleetManager);
  public void updatePassword(Employee employee, String password);
  public void deleteEmployee(Employee employee);
  public Set<Employee> findEmployees();
  
  public Employee login(String email, String pass);
  public boolean hasRole(Employee employee, RoleEnum rEnum);

}
