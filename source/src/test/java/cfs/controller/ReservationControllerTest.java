package cfs.controller;

import cfs.data.constants.CarStateEnum;
import cfs.data.constants.ReservationStateEnum;
import cfs.data.constants.RoleEnum;
import cfs.data.dao.CarDao;
import cfs.data.dao.ReservationDao;
import cfs.data.entity.Car;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.hamcrest.CoreMatchers.hasItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ReservationControllerTest {

  private CarDao         carDao;
  private ReservationDao reservationDao;
  
  private EmployeeController ec;
  private ReservationController rc;
  
  public ReservationControllerTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
    rc = spy(ReservationController.class);
    
    ec = mock(EmployeeController.class);
    carDao = mock(CarDao.class);
    reservationDao = mock(ReservationDao.class);
    
    rc.carDao = carDao;
    rc.reservationDao = reservationDao;
    rc.ec = ec;
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of findAllReservations method, of class ReservationController.
   */
  @Test
  public void testFindReservations_Employee() {
    Employee user = mock(Employee.class);
    Set<Reservation> reservations = mock(Set.class);
    
    doReturn(reservations).when(reservationDao).findAllByEmployee(user);
    doReturn(false).when(ec).hasRole(user, RoleEnum.CEO);
    doReturn(false).when(ec).hasRole(user, RoleEnum.DM);
    
    Set<Reservation> result = rc.findReservations(user);
    assertEquals(reservations, result);
  }

  /**
   * Test of findAllReservations method, of class ReservationController.
   */
  @Test
  public void testFindReservations_DM() {
    Employee user = mock(Employee.class);
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    Employee emp3 = mock(Employee.class);
    
    Reservation res1 = mock(Reservation.class);
    Reservation res2 = mock(Reservation.class);
    Reservation res3 = mock(Reservation.class);
    Reservation res4 = mock(Reservation.class);
    
    Department dep1 = mock(Department.class);
    Department dep2 = mock(Department.class);
    
    Set<Reservation> reservations = new HashSet<>();
    reservations.add(res1);
    reservations.add(res2);
    reservations.add(res3);
    reservations.add(res4);
    
    doReturn(emp1).when(res1).getEmployee();
    doReturn(emp2).when(res2).getEmployee();
    doReturn(emp3).when(res3).getEmployee();
    doReturn(user).when(res4).getEmployee();
    
    doReturn(dep1).when(emp1).getDepartment();
    doReturn(dep2).when(emp2).getDepartment();
    doReturn(dep1).when(emp3).getDepartment();
    doReturn(dep1).when(user).getManaging();
    
    doReturn(ReservationStateEnum.NEW.name()).when(res1).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res3).getState();
    doReturn(ReservationStateEnum.NEW.name()).when(res4).getState();
    
    doReturn(reservations).when(reservationDao).findAll();
    
    doReturn(false).when(ec).hasRole(user, RoleEnum.CEO);
    doReturn(true).when(ec).hasRole(user, RoleEnum.DM);
    
    Set<Reservation> result = rc.findReservations(user);
    
    assertThat(result, hasItem(res3));
    assertThat(result, hasItem(res4));
    assertEquals(2, result.size());
  }

  /**
   * Test of findAllReservations method, of class ReservationController.
   */
  @Test
  public void testFindReservations_CEO() {
    Employee user = mock(Employee.class);
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    Employee emp3 = mock(Employee.class);
    
    Reservation res1 = mock(Reservation.class);
    Reservation res2 = mock(Reservation.class);
    Reservation res3 = mock(Reservation.class);
    Reservation res4 = mock(Reservation.class);
    
    Department dep1 = mock(Department.class);
    Department dep2 = mock(Department.class);
    
    Set<Reservation> reservations = new HashSet<>();
    reservations.add(res1);
    reservations.add(res2);
    reservations.add(res3);
    reservations.add(res4);
    
    doReturn(emp1).when(res1).getEmployee();
    doReturn(emp2).when(res2).getEmployee();
    doReturn(emp3).when(res3).getEmployee();
    doReturn(user).when(res4).getEmployee();
    
    doReturn(dep1).when(emp1).getDepartment();
    doReturn(dep2).when(emp2).getDepartment();
    doReturn(dep1).when(emp3).getDepartment();
    doReturn(dep1).when(user).getManaging();
    
    doReturn(ReservationStateEnum.NEW.name()).when(res1).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res2).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res3).getState();
    doReturn(ReservationStateEnum.NEW.name()).when(res4).getState();
    
    doReturn(reservations).when(reservationDao).findAll();
    
    doReturn(true).when(ec).hasRole(user, RoleEnum.CEO);
    
    Set<Reservation> result = rc.findReservations(user);
    
    assertThat(result, hasItem(res2));
    assertThat(result, hasItem(res3));
    assertThat(result, hasItem(res4));
    assertEquals(3, result.size());
  }

  /**
   * Test of saveReservation method, of class ReservationController.
   */
  @Test
  public void testSaveReservation() {
    Reservation res = mock(Reservation.class);
    
    doReturn(res).when(reservationDao).save(res);
    
    Reservation result = rc.saveReservation(res);
    
    assertEquals(res, result);
  }

  /**
   * Test of findAllConfirmations method, of class ReservationController.
   */
  @Test
  public void testFindAllConfirmations_CEO() {
    Employee user = mock(Employee.class);
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    
    Reservation res1 = mock(Reservation.class);
    Reservation res2 = mock(Reservation.class);
    Reservation res3 = mock(Reservation.class);
    Reservation res4 = mock(Reservation.class);

    Set<Employee> employees = new HashSet<>();
    employees.add(emp1);
    employees.add(emp2);
    
    doReturn(ReservationStateEnum.NEW.name()).when(res1).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res2).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res3).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res4).getState();
    
    List<Reservation> list1 = new ArrayList<>();
    list1.add(res1);
    list1.add(res2);
    List<Reservation> list2 = new ArrayList<>();
    list1.add(res3);
    list1.add(res4);
    
    doReturn(list1).when(reservationDao).findAllByEmployeeAndEndDateAfter(emp1, LocalDate.now().minusDays(1));
    doReturn(list2).when(reservationDao).findAllByEmployeeAndEndDateAfter(emp2, LocalDate.now().minusDays(1));
    
    doReturn(true).when(ec).hasRole(user, RoleEnum.CEO);
    doReturn(employees).when(ec).findEmployees();
    
    List<Reservation> result = rc.findAllConfirmations(user);
    
    assertThat(result, hasItem(res2));
    assertThat(result, hasItem(res3));
    assertThat(result, hasItem(res4));
    assertEquals(3, result.size());
  }

  /**
   * Test of findAllConfirmations method, of class ReservationController.
   */
  @Test
  public void testFindAllConfirmations_DM() {
    Department dep = mock(Department.class);
    
    Employee user = mock(Employee.class);
    Employee emp1 = mock(Employee.class);
    Employee emp2 = mock(Employee.class);
    
    Reservation res1 = mock(Reservation.class);
    Reservation res2 = mock(Reservation.class);
    Reservation res3 = mock(Reservation.class);
    Reservation res4 = mock(Reservation.class);

    Set<Employee> employees = new HashSet<>();
    employees.add(emp1);
    employees.add(emp2);
    
    doReturn(ReservationStateEnum.NEW.name()).when(res1).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res2).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res3).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res4).getState();
    
    List<Reservation> list1 = new ArrayList<>();
    list1.add(res1);
    list1.add(res2);
    List<Reservation> list2 = new ArrayList<>();
    list1.add(res3);
    list1.add(res4);
    
    doReturn(dep).when(user).getManaging();
    
    doReturn(list1).when(reservationDao).findAllByEmployeeAndEndDateAfter(emp1, LocalDate.now().minusDays(1));
    doReturn(list2).when(reservationDao).findAllByEmployeeAndEndDateAfter(emp2, LocalDate.now().minusDays(1));
    
    doReturn(false).when(ec).hasRole(user, RoleEnum.CEO);
    doReturn(true).when(ec).hasRole(user, RoleEnum.DM);
    doReturn(employees).when(ec).findEmployees(dep);
    
    List<Reservation> result = rc.findAllConfirmations(user);
    
    assertThat(result, hasItem(res2));
    assertThat(result, hasItem(res3));
    assertThat(result, hasItem(res4));
    assertEquals(3, result.size());
  }

  /**
   * Test of submitReservation method, of class ReservationController.
   */
  @Test
  public void testSubmitReservation() {
    Reservation res = spy(Reservation.class);
    
    rc.submitReservation(res);
    
    verify(reservationDao).save(res);
    assertEquals(ReservationStateEnum.SUBMITTED.name(), res.getState());
  }

  /**
   * Test of getInvalidDates method, of class ReservationController.
   */
  @Test
  public void testGetInvalidDates() {
    Car car = mock(Car.class);
    Set<Car> cars = new HashSet<>();
    cars.add(car);
    
    Reservation res = mock(Reservation.class);
    List<Reservation> reservations = new ArrayList<>();
    reservations.add(res);
    
    doReturn(cars).when(carDao).findAll();
    
    doReturn(reservations).when(reservationDao).findAllByCarAndEndDateAfter(car, LocalDate.now());

    doReturn(ReservationStateEnum.READY.name()).when(res).getState();
    doReturn(CarStateEnum.OKAY.name()).when(car).getState();

    doReturn(LocalDate.now()).when(res).getStartDate();
    doReturn(LocalDate.now()).when(res).getEndDate();

    doReturn(true).when(rc).activeReservation(res);
    
    Set<LocalDate> result = rc.getInvalidDates();
    
    assertThat(result, hasItem(LocalDate.now()));
  }

  /**
   * Test of activeReservation method, of class ReservationController.
   */
  @Test
  public void testActiveReservation() {
    Reservation res1 = mock(Reservation.class);
    Reservation res2 = mock(Reservation.class);
    Reservation res3 = mock(Reservation.class);
    Reservation res4 = mock(Reservation.class);
    
    doReturn(ReservationStateEnum.READY.name()).when(res1).getState();
    doReturn(ReservationStateEnum.REALIZED.name()).when(res2).getState();
    doReturn(ReservationStateEnum.ACCEPTED.name()).when(res3).getState();
    doReturn(ReservationStateEnum.SUBMITTED.name()).when(res4).getState();
    
    boolean result1 = rc.activeReservation(res1);
    boolean result2 = rc.activeReservation(res2);
    boolean result3 = rc.activeReservation(res3);
    boolean result4 = rc.activeReservation(res4);
    
    assertEquals(true, result1);
    assertEquals(true, result2);
    assertEquals(false, result3);
    assertEquals(false, result4);
  }

  /**
   * Test of getMaxEnd method, of class ReservationController.
   */
  @Test
  public void testGetMaxEnd() {
    Car car = mock(Car.class);
    Reservation res = mock(Reservation.class);
    LocalDate date = LocalDate.now();
    LocalDate startDate = LocalDate.now().plusDays(1);
    
    Set<Car> cars = new HashSet<>();
    cars.add(car);
    
    List<Reservation> reservations = new ArrayList<>();
    reservations.add(res);
    
    doReturn(cars).when(carDao).findAll();
    doReturn(CarStateEnum.OKAY.name()).when(car).getState();
    doReturn(reservations).when(reservationDao).findAllByCarAndEndDateAfter(car, date);
    doReturn(true).when(rc).activeReservation(res);
    doReturn(startDate).when(res).getStartDate();
    
    LocalDate result = rc.getMaxEnd(date);
    
    assertEquals(startDate, result);
  }

  /**
   * Test of setConfirmation method, of class ReservationController.
   */
  @Test
  public void testSetConfirmation() {
    Reservation res1 = spy(Reservation.class);
    Reservation res2 = spy(Reservation.class);
    
    doNothing().when(rc).assignCar(res1);
    
    rc.setConfirmation(res1, true);
    rc.setConfirmation(res2, false);
    
    assertEquals(ReservationStateEnum.ACCEPTED.name(), res1.getState());
    assertEquals(ReservationStateEnum.REJECTED.name(), res2.getState());
    
    verify(reservationDao).save(res1);
    verify(reservationDao).save(res2);
    verify(rc).assignCar(res1);
  }

  /**
   * Test of assignCar method, of class ReservationController.
   */
  @Test
  public void testAssignCar_SUCCESS() {
    Car car = mock(Car.class);
    Reservation res1 = spy(Reservation.class);
    Reservation res2 = spy(Reservation.class);
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    
    Set<Car> cars = new HashSet<>();
    cars.add(car);
    
    List<Reservation> reservations = new ArrayList<>();
    reservations.add(res2);
    
    doReturn(CarStateEnum.OKAY.name()).when(car).getState();
    doReturn(cars).when(carDao).findAll();
    doReturn(reservations).when(reservationDao).findAllByCarAndStartDateBeforeAndEndDateAfter(car, endDate.plusDays(1), startDate.minusDays(1));
    doReturn(ReservationStateEnum.NEW.name()).when(res2).getState();
    doReturn(endDate).when(res1).getEndDate();
    doReturn(startDate).when(res1).getStartDate();
    
    rc.assignCar(res1);
    
    verify(reservationDao).save(res1);
    assertEquals(car, res1.getCar());
    assertEquals(ReservationStateEnum.READY.name(), res1.getState());
  }

  /**
   * Test of assignCar method, of class ReservationController.
   */
  @Test
  public void testAssignCar_FAIL() {
    Car car = mock(Car.class);
    Reservation res1 = spy(Reservation.class);
    Reservation res2 = spy(Reservation.class);
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    
    Set<Car> cars = new HashSet<>();
    cars.add(car);
    
    List<Reservation> carRes = new ArrayList<>();
    carRes.add(res2);
    res2.setState(ReservationStateEnum.READY.name());
    
    doReturn(CarStateEnum.OKAY.name()).when(car).getState();
    doReturn(cars).when(carDao).findAll();
    doReturn(carRes).when(reservationDao).findAllByCarAndStartDateBeforeAndEndDateAfter(car, endDate.plusDays(1), startDate.minusDays(1));
    doReturn(endDate).when(res1).getEndDate();
    doReturn(startDate).when(res1).getStartDate();
    
    rc.assignCar(res1);
    
    verify(reservationDao).save(res1);
    assertEquals(null, res1.getCar());
    assertEquals(ReservationStateEnum.FAIL.name(), res1.getState());
  }
  
}
