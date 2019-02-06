package cfs.controller;

import cfs.data.constants.RoleEnum;
import cfs.data.dao.DepartmentDao;
import cfs.data.dao.EmployeeDao;
import cfs.data.dao.RoleDao;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import cfs.data.entity.Role;
import com.vaadin.server.WrappedSession;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataIntegrityViolationException;

public class EmployeeControllerTest {
  
  private WrappedSession session;
  private RoleDao        roleDao;
  private DepartmentDao  departmentDao;
  private EmployeeDao    employeeDao;
  
  private EmployeeController ec;
  
  
  public EmployeeControllerTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
    ec = spy(EmployeeController.class);
    
    session = mock(WrappedSession.class);
    roleDao = mock(RoleDao.class);
    departmentDao = mock(DepartmentDao.class);
    employeeDao = mock(EmployeeDao.class);
    
    ec.roleDao = roleDao;
    ec.departmentDao = departmentDao;
    ec.employeeDao = employeeDao;
    ec.session = session;
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of addDepartment method, of class EmployeeController.
   */
  @Test
  public void testAddDepartment() {
    Department department = mock(Department.class);
 
    when(departmentDao.save(department)).thenReturn(department);
    
    Department result = ec.addDepartment(department);
    
    assertEquals(department, result);
  }

  /**
   * Test of addDepartment method, of class EmployeeController.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddDepartment_Exception() {
    Department department = mock(Department.class);
    
    when(departmentDao.save(any())).thenThrow(DataIntegrityViolationException.class);
    
    ec.addDepartment(department);
  }

  /**
   * Test of updateDepartmentName method, of class EmployeeController.
   */
  @Test
  public void testUpdateDepartment() {
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    
    Department dep1 = spy(Department.class);
    dep1.setName("first");
    dep1.setManager(emp1);
 
    Department dep2 = spy(Department.class);
    dep2.setName("second");
    dep2.setManager(emp2);
 
    when(departmentDao.save(dep2)).thenReturn(dep2);
    
    doNothing().when(ec).denyManager(dep1);
    doNothing().when(ec).assignManager(dep2);
    
    Department result = ec.updateDepartment(dep1, dep2);
    
    verify(ec).denyManager(dep1);
    verify(ec).assignManager(dep2);
    
    assertEquals(dep2, result);
    assertEquals(dep2.getName(), result.getName());
  }

  /**
   * Test of updateDepartmentName method, of class EmployeeController.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDepartment_Exception() {
    Department dep1 = mock(Department.class);
    Department dep2 = mock(Department.class);
    
    when(departmentDao.save(any())).thenThrow(DataIntegrityViolationException.class);
    
    ec.updateDepartment(dep1, dep2);
  }

  /**
   * Test of findDepartments method, of class EmployeeController.
   */
  @Test
  public void testFindDepartments() {
    Set<Department> departments = mock(Set.class);
    
    when(departmentDao.findAll()).thenReturn(departments);
    
    Set<Department> result = ec.findDepartments();
    
    assertEquals(departments, result);
  }

  /**
   * Test of findManagableDepartments method, of class EmployeeController.
   */
  @Test
  public void testFindPossibleManagers_NoManager() {
    Employee e1 = mock(Employee.class);
    Employee e2 = mock(Employee.class);
    Employee e3 = mock(Employee.class);
    
    Department d1 = mock(Department.class);
    Department d2 = mock(Department.class);
    Department d3 = mock(Department.class);
    
    when(e1.getManaging()).thenReturn(null);
    when(e2.getManaging()).thenReturn(d2);
    when(e3.getManaging()).thenReturn(d3);
    
    Set<Employee> managers = new HashSet<>();
    managers.add(e1);
    managers.add(e2);
    managers.add(e3);
    
    Set<Employee> expected = new HashSet<>();
    expected.add(e1);
    
    when(ec.findEmployees()).thenReturn(managers);
    
    Set<Employee> result = ec.findPossibleManagers(d1);
    
    assertEquals(expected, result);
  }

  /**
   * Test of findManagableDepartments method, of class EmployeeController.
   */
  @Test
  public void testFindManagableDepartments_WithManager() {
    Employee e1 = mock(Employee.class);
    Employee e2 = mock(Employee.class);
    Employee e3 = mock(Employee.class);
    
    Department d1 = mock(Department.class);
    Department d2 = mock(Department.class);
    Department d3 = mock(Department.class);
    
    when(e1.getManaging()).thenReturn(null);
    when(e2.getManaging()).thenReturn(d2);
    when(e3.getManaging()).thenReturn(d3);
    
    Set<Employee> managers = new HashSet<>();
    managers.add(e1);
    managers.add(e2);
    managers.add(e3);
    
    Set<Employee> expected = new HashSet<>();
    expected.add(e1);
    expected.add(e2);
    
    when(ec.findEmployees()).thenReturn(managers);
    
    Set<Employee> result = ec.findPossibleManagers(d2);
    
    assertEquals(expected, result);
  }

  /**
   * Test of deleteDepartment method, of class EmployeeController.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testDeleteDepartment_WithEmployees() {
    Department department = mock(Department.class);
    
    Employee employee = mock(Employee.class);
    
    Set<Employee> employees = new HashSet<>();
    employees.add(employee);
    
    when(ec.findEmployees(department)).thenReturn(employees);
    
    ec.deleteDepartment(department);
  }

  /**
   * Test of deleteDepartment method, of class EmployeeController.
   */
  @Test
  public void testDeleteDepartment_WithNoEmployees_WithNoManager() {
    Department department = mock(Department.class);
    when(department.getManager()).thenReturn(null);
    
    Set<Employee> employees = new HashSet<>();
    
    when(ec.findEmployees(department)).thenReturn(employees);
    doNothing().when(departmentDao).delete(department);
    
    ec.deleteDepartment(department);
    
    verify(departmentDao).delete(department);
  }

  /**
   * Test of deleteDepartment method, of class EmployeeController.
   */
  @Test
  public void testDeleteDepartment_WithNoEmployees_WithManager() {
    Employee employee = mock(Employee.class);

    Department department = mock(Department.class);
    
    employee.setManaging(department);

    when(employee.getManaging()).thenCallRealMethod();
    when(department.getManager()).thenReturn(employee);
    
    Set<Employee> employees = new HashSet<>();
    
    when(ec.findEmployees(department)).thenReturn(employees);
    doNothing().when(departmentDao).delete(department);
    
    ec.deleteDepartment(department);
    
    verify(departmentDao).delete(department);
    assertEquals(null, employee.getManaging());
  }

  /**
   * Test of findEmployees method, of class EmployeeController.
   */
  @Test
  public void testFindEmployees_Department() {
    Set employees = mock(Set.class);
    
    Department department = mock(Department.class);
    
    when(employeeDao.findAllByDepartment(department)).thenReturn(employees);
    
    Set result = ec.findEmployees(department);
    
    verify(employeeDao).findAllByDepartment(department);
    assertEquals(employees, result);
  }

  /**
   * Test of addEmployee method, of class EmployeeController.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddEmployee_WithEmailCollision() {
    Employee employee = mock(Employee.class);
    
    when(employeeDao.save(employee)).thenThrow(DataIntegrityViolationException.class);
    
    ec.addEmployee(employee, Boolean.TRUE);
    
    verify(employeeDao).save(employee);
  }

  /**
   * Test of addEmployee method, of class EmployeeController.
   */
  @Test
  public void testAddEmployee_WithoutRole() {
    Employee employee = mock(Employee.class);
    
    Department department = mock(Department.class);
    
    Set<Employee> depEmployees = new HashSet<>();
    
    employee.setDepartment(department);
    
    when(employeeDao.save(employee)).thenReturn(employee);
    when(department.getEmployees()).thenReturn(depEmployees);
    
    doAnswer(new Answer() {
      public Object answer(InvocationOnMock invocation) {
        depEmployees.add(employee);
        return null;
      }
    }).when(ec).assignDepartment(employee);
    
    Employee result = ec.addEmployee(employee, Boolean.FALSE);
    
    verify(employeeDao).save(employee);

    assertEquals(employee, result);
    assertThat(department.getEmployees(), hasItem(employee));
  }

  /**
   * Test of addEmployee method, of class EmployeeController.
   */
  @Test
  public void testAddEmployee_WithRole() {
    Employee employee = mock(Employee.class);
    Role fleetManager = mock(Role.class);
    Department department = mock(Department.class);
    
    Set<Employee> depEmployees = new HashSet<>();
    Set<Role> roles = new HashSet<>();
    
    employee.setDepartment(department);
    
    when(employeeDao.save(employee)).thenReturn(employee);
    when(department.getEmployees()).thenReturn(depEmployees);
    
    doAnswer(new Answer() {
      public Object answer(InvocationOnMock invocation) {
        depEmployees.add(employee);
        return null;
      }
    }).when(ec).assignDepartment(employee);
    
    doAnswer(new Answer() {
      public Object answer(InvocationOnMock invocation) {
        roles.add(fleetManager);
        return null;
      }
    }).when(ec).assignRole(employee, RoleEnum.FM);
    
    Employee result = ec.addEmployee(employee, Boolean.TRUE);
    
    verify(employeeDao).save(employee);

    assertEquals(employee, result);
    assertThat(department.getEmployees(), hasItem(employee));
    assertThat(roles, hasItems(fleetManager));
  }

  /**
   * Test of updateEmployee method, of class EmployeeController.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testUpdateEmployee_EmailCollision() {
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    
    doNothing().when(ec).assignRole(any(), any());
    doNothing().when(ec).setSession();

    doThrow(DataIntegrityViolationException.class).when(employeeDao).save(any());

    ec.updateEmployee(emp1, emp2, Boolean.FALSE);
  }

  /**
   * Test of updateEmployee method, of class EmployeeController.
   */
  @Test
  public void testUpdateEmployee_CeoUpdateAll() {
    Employee emp1 = spy(Employee.class);
    Employee emp2 = spy(Employee.class);
    
    Department dep1 = spy(Department.class);
    Department dep2 = spy(Department.class);
    
    emp1.setId(1l);
    emp1.setPassword("pass");

    emp1.setDepartment(dep1);
    emp2.setDepartment(dep2);
    emp1.setManaging(dep1);
    
    doReturn(true).when(ec).hasRole(emp1, RoleEnum.CEO);
    doNothing().when(ec).setSession();
    doNothing().when(ec).assignRole(emp2, RoleEnum.CEO);
    doNothing().when(ec).denyDepartment(emp1);
    doNothing().when(ec).assignDepartment(emp2);
    doNothing().when(ec).denyRole(emp2, RoleEnum.FM);
    doNothing().when(ec).assignRole(emp2, RoleEnum.DM);
    
    doReturn((Object)emp2).when(session).getAttribute("user");
    doNothing().when(session).setAttribute("user", emp2);
    
    doReturn(emp2).when(employeeDao).save(emp2);
    
    Employee result = ec.updateEmployee(emp1, emp2, Boolean.FALSE);
    
    verify(ec).assignRole(emp2, RoleEnum.CEO);
    verify(ec).denyDepartment(emp1);
    verify(ec).assignDepartment(emp2);
    verify(ec).denyRole(emp2, RoleEnum.FM);
    verify(session).setAttribute("user", emp2);
    
    assertEquals(emp2, result);
    assertEquals(emp1.getId(), emp2.getId());
    assertEquals(dep1, emp2.getManaging());
    assertEquals(emp1.getPassword(), emp2.getPassword());
  }

  /**
   * Test of updatePassword method, of class EmployeeController.
   */
  @Test
  public void testUpdatePassword() {
    Employee employee = spy(Employee.class);
    String password = "pass";
    
    when(employeeDao.save(employee)).thenReturn(employee);
    
    ec.updatePassword(employee, password);
    
    assertEquals(password, employee.getPassword());
    verify(employeeDao).save(employee);
  }

  /**
   * Test of deleteEmployee method, of class EmployeeController.
   */
  @Test
  public void testDeleteEmployee() {
    // TODO
  }

  /**
   * Test of findEmployees method, of class EmployeeController.
   */
  @Test
  public void testFindEmployees_0args() {
    Set<Employee> employees = new HashSet<>();
    
    when(employeeDao.findAll()).thenReturn(employees);
    
    Set<Employee> result = ec.findEmployees();
    
    verify(employeeDao).findAll();
    assertEquals(employees, result);
  }

  /**
   * Test of login method, of class EmployeeController.
   */
  @Test
  public void testLogin() {
    Employee employee = mock(Employee.class);
    String email = "email";
    String pass = "pass";
    
    when(employeeDao.findByEmailAndPassword(email, pass)).thenReturn(employee);
    
    Employee result = ec.login(email, pass);
    
    verify(employeeDao).findByEmailAndPassword(email, pass);
    assertEquals(employee, result);
  }

  /**
   * Test of hasRole method, of class EmployeeController.
   */
  @Test
  public void testHasRole_True() {
    Employee employee = mock(Employee.class);
    
    Role r1 = mock(Role.class);
    Role r2 = mock(Role.class);
    
    Set<Role> roles = new HashSet<>();
    roles.add(r1);
    roles.add(r2);
    
    RoleEnum rEnum = RoleEnum.CEO;
    
    when(roleDao.findAllByEmployee(employee)).thenReturn(roles);
    when(roleDao.findByName(rEnum.name())).thenReturn(r1);
    
    boolean result = ec.hasRole(employee, rEnum);
    
    assertEquals(true, result);
  }

  /**
   * Test of hasRole method, of class EmployeeController.
   */
  @Test
  public void testHasRole_False() {
    Employee employee = mock(Employee.class);
    
    Role r1 = mock(Role.class);
    Role r2 = mock(Role.class);
    
    Set<Role> roles = new HashSet<>();
    roles.add(r1);
    
    RoleEnum rEnum = RoleEnum.CEO;
    
    when(roleDao.findAllByEmployee(employee)).thenReturn(roles);
    when(roleDao.findByName(rEnum.name())).thenReturn(r2);
    
    boolean result = ec.hasRole(employee, rEnum);
    
    assertEquals(false, result);
  }

  /**
   * Test of assignDepartment method, of class EmployeeController.
   */
  @Test
  public void testAssignDepartment() {
    Employee employee = spy(Employee.class);
    Department department = mock(Department.class);
    
    employee.setDepartment(department);
    
    Set<Employee> employees = new HashSet<>();
    
    doReturn(employees).when(ec).findEmployees(department);
    
    doNothing().when(department).setEmployees(employees);
    doReturn(department).when(departmentDao).save(department);
    
    ec.assignDepartment(employee);
    
    verify(department).setEmployees(employees);
    verify(departmentDao).save(department);
    
    assertThat(employees, hasItem(employee));
  }

  /**
   * Test of assignManager method, of class EmployeeController.
   */
  @Test
  public void testAssignManager() {
    Employee employee = spy(Employee.class);
    Department department = spy(Department.class);
    
    department.setManager(employee);

    doReturn(employee).when(employeeDao).save(employee);
    doNothing().when(ec).assignRole(employee, RoleEnum.DM);
    
    ec.assignManager(department);
    
    verify(employeeDao).save(employee);
    verify(ec).assignRole(employee, RoleEnum.DM);
    
    assertEquals(department, employee.getManaging());
  }

  /**
   * Test of assignRole method, of class EmployeeController.
   */
  @Test
  public void testAssignRole() {
    Role role = spy(Role.class);
    Employee employee = spy(Employee.class);
    
    Set<Role> roles = new HashSet<>();
    Set<Employee> employees = new HashSet<>();
    
    doReturn(role).when(roleDao).findByName(RoleEnum.CEO.name());
    doReturn(roles).when(roleDao).findAllByEmployee(employee);
    doReturn(employee).when(employeeDao).save(employee);
    
    doReturn(employees).when(employeeDao).findAllByRole(role);
    doReturn(role).when(roleDao).save(role);
    
    ec.assignRole(employee, RoleEnum.CEO);
    
    verify(employeeDao).save(employee);
    verify(roleDao).save(role);
    
    assertThat(roles, hasItem(role));
    assertThat(employees, hasItem(employee));
  }

  /**
   * Test of denyDepartment method, of class EmployeeController.
   */
  @Test
  public void testDenyDepartment() {
    Employee employee = spy(Employee.class);
    Department department = mock(Department.class);
    
    employee.setDepartment(department);
    
    Set<Employee> employees = new HashSet<>();
    employees.add(employee);
    
    doReturn(employees).when(ec).findEmployees(department);
    
    doNothing().when(department).setEmployees(employees);
    doReturn(department).when(departmentDao).save(department);
    
    ec.denyDepartment(employee);
    
    verify(department).setEmployees(employees);
    verify(departmentDao).save(department);
    
    assertThat(employees, not(hasItem(employee)));
  }

  /**
   * Test of denyManager method, of class EmployeeController.
   */
  @Test
  public void testDenyManager() {
    Employee employee = spy(Employee.class);
    Department department = spy(Department.class);
    
    employee.setManaging(department);
    department.setManager(employee);

    doReturn(department).when(departmentDao).save(department);
    doNothing().when(ec).denyRole(employee, RoleEnum.DM);
    
    ec.denyManager(department);
    
    verify(employeeDao).save(employee);
    verify(ec).denyRole(employee, RoleEnum.DM);
    
    assertEquals(null, employee.getManaging());
  }

  /**
   * Test of denyRole method, of class EmployeeController.
   */
  @Test
  public void testDenyRole_WithRole() {
    Role role = spy(Role.class);
    Employee employee = spy(Employee.class);
    
    Set<Role> roles = new HashSet<>();
    Set<Employee> employees = new HashSet<>();
    
    roles.add(role);
    employees.add(employee);
    
    doReturn(role).when(roleDao).findByName(RoleEnum.CEO.name());
    doReturn(roles).when(roleDao).findAllByEmployee(employee);
    doReturn(employee).when(employeeDao).save(employee);
    
    doReturn(employees).when(employeeDao).findAllByRole(role);
    doReturn(role).when(roleDao).save(role);
    
    ec.denyRole(employee, RoleEnum.CEO);
    
    verify(employeeDao).save(employee);
    verify(roleDao).save(role);
    
    assertThat(roles, not(hasItem(role)));
    assertThat(employees, not(hasItem(employee)));
  }
  
}
