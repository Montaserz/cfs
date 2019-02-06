package cfs.controller;

import cfs.data.constants.RoleEnum;
import cfs.data.dao.DepartmentDao;
import cfs.data.dao.EmployeeDao;
import cfs.data.dao.RoleDao;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import cfs.data.entity.Role;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

@Controller
public class EmployeeController implements EmployeeControllerInterface {
  
  WrappedSession session;
  
  @Autowired
  RoleDao roleDao;
  
  @Autowired
  DepartmentDao departmentDao;

  @Autowired
  EmployeeDao employeeDao;

  @Override
  public Department addDepartment(Department department) {
    Department dep;
    
    try {
      dep = departmentDao.save(department);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Department Name Collision!");
    }
    
    if (department.getManager() != null)
      assignManager(department);
    
    return dep;
  }
  
  @Override
  public Department updateDepartment(Department dep1, Department dep2) {
    try {
      dep2.setId(dep1.getId());
      dep2 = departmentDao.save(dep2);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Department Name Collision!");
    }
    
    if (dep1.getManager() != dep2.getManager()) {
      denyManager(dep1);
      assignManager(dep2);
    }
    
    return dep2;
  }
  
  @Override
  public Set<Department> findDepartments() {
    return departmentDao.findAll();
  }

  @Override
  public Set<Employee> findPossibleManagers(Department department) {
    Set<Employee> managers = findEmployees();
    managers.removeIf(e -> e.getManaging()!= null && !e.getManaging().equals(department));
    return managers;
  }

  @Override
  public void deleteDepartment(Department department) {
    if (!findEmployees(department).isEmpty())
      throw new UnsupportedOperationException("Department Has Employees!");
    
    if (department.getManager() != null)
      department.getManager().setManaging(null);
    
    departmentDao.delete(department);
  }
  
  @Override
  public Set<Employee> findEmployees(Department department) {
    return employeeDao.findAllByDepartment(department);
  }

  @Override
  public Employee addEmployee(Employee employee, Boolean fleetManager) {
    try {
      employee = employeeDao.save(employee);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Email Collision!");
    }

    assignDepartment(employee);

    if (fleetManager) assignRole(employee, RoleEnum.FM);

    return employee;
  }
  
  @Override
  public Employee updateEmployee(Employee emp1, Employee emp2, Boolean fleetManager) {
    setSession();
    
    emp2.setId(emp1.getId());
    emp2.setPassword(emp1.getPassword());
    emp2.setManaging(emp1.getManaging());
    
    if (hasRole(emp1, RoleEnum.CEO))
      assignRole(emp2, RoleEnum.CEO);
    
    try {
      emp2 = employeeDao.save(emp2);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Email Collision!");
    }
    
    if (emp1.getDepartment() != emp2.getDepartment()) {
      denyDepartment(emp1);
      assignDepartment(emp2);
    }
    
    if (emp2.getManaging() == null)
      denyRole(emp2, RoleEnum.DM);
    else
      assignRole(emp2, RoleEnum.DM);
    
    if (fleetManager)
      assignRole(emp2, RoleEnum.FM);
    else
      denyRole(emp2, RoleEnum.FM);

    if (((Employee) session.getAttribute("user")).equals(emp2))
      session.setAttribute("user", emp2);
    
    return emp2;
  }
  
  @Override
  public void updatePassword(Employee employee, String password) {
    employee.setPassword(password);
    employeeDao.save(employee);
  }

  @Override
  public void deleteEmployee(Employee employee) {
    //TODO
  }
  
  @Override
  public Set<Employee> findEmployees() {
    return employeeDao.findAll();
  }
          
  @Override
  public Employee login(String email, String pass) {
    return employeeDao.findByEmailAndPassword(email, pass);
  }

  @Override
  public boolean hasRole(Employee employee, RoleEnum rEnum) {
    Set<Role> roles = roleDao.findAllByEmployee(employee);
    Role role = roleDao.findByName(rEnum.name());
    
    return roles.contains(role);
  }

  protected void assignDepartment(Employee employee) {
    Department department = employee.getDepartment();
    Set<Employee> employees = findEmployees(department);
    employees.add(employee);
    department.setEmployees(employees);
    departmentDao.save(department);
  }

  protected void assignManager(Department department) {
    if (department.getManager() == null) return;
    Employee employee = department.getManager();
    employee.setManaging(department);
    employeeDao.save(employee);
    assignRole(employee, RoleEnum.DM);
  }
  
  protected void assignRole(Employee employee, RoleEnum rEnum) {
    Role role = roleDao.findByName(rEnum.name());

    Set<Role> roles = roleDao.findAllByEmployee(employee);
    roles.add(role);
    employee.setRoles(roles);
    employeeDao.save(employee);

    Set<Employee> employees = employeeDao.findAllByRole(role);
    employees.add(employee);
    role.setEmployees(employees);
    roleDao.save(role);
  }
  
  protected void denyDepartment(Employee employee) {
    Department department = employee.getDepartment();
    Set<Employee> employees = findEmployees(department);
    employees.remove(employee);
    department.setEmployees(employees);
    departmentDao.save(department);
  }
  
  protected void denyManager(Department department) {
    if (department.getManager() == null) return;
    Employee employee = department.getManager();
    employee.setManaging(null);
    employeeDao.save(employee);
    denyRole(employee, RoleEnum.DM);
  }

  protected void denyRole(Employee employee, RoleEnum rEnum) {
    Role role = roleDao.findByName(rEnum.name());

    Set<Role> roles = roleDao.findAllByEmployee(employee);
    roles.remove(role);
    employee.setRoles(roles);
    employeeDao.save(employee);

    Set<Employee> employees = employeeDao.findAllByRole(role);
    employees.remove(employee);
    role.setEmployees(employees);
    roleDao.save(role);
  }
  
  protected void setSession() {
    session = VaadinService.getCurrentRequest().getWrappedSession();
  }
}
