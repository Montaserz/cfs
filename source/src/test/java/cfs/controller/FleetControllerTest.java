package cfs.controller;

import cfs.data.dao.CarDao;
import cfs.data.dao.MaintenanceDao;
import cfs.data.entity.Car;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.hasItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import org.springframework.dao.DataIntegrityViolationException;

public class FleetControllerTest {

  private CarDao         carDao;
  private MaintenanceDao maintenanceDao;
  
  private FleetController fc;
  
  public FleetControllerTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
    fc = spy(FleetController.class);
    
    carDao = mock(CarDao.class);
    maintenanceDao = mock(MaintenanceDao.class);
    
    fc.carDao = carDao;
    fc.maintenanceDao = maintenanceDao;
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of findCarByPlate method, of class FleetController.
   */
  @Test
  public void testFindCarByPlate() {
    Car car = mock(Car.class);
    String plate = "plate";
    
    doReturn(car).when(carDao).findByPlate(plate);
    
    Car result = fc.findCarByPlate(plate);
    
    assertEquals(car, result);
  }

  /**
   * Test of findCars method, of class FleetController.
   */
  @Test
  public void testFindCars() {
    Set<Car> cars = new HashSet<>();
    
    doReturn(cars).when(carDao).findAll();
    
    Set<Car> result = fc.findCars();
    
    assertEquals(cars, result);
  }

  /**
   * Test of addCar method, of class FleetController.
   */
  @Test
  public void testAddCar() {
    Car car = mock(Car.class);
    
    doReturn(car).when(carDao).save(car);
    
    Car result = fc.addCar(car);
    
    assertEquals(car, result);
  }

  /**
   * Test of addCar method, of class FleetController.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testAddCar_Exception() {
    Car car = mock(Car.class);
    
    doThrow(DataIntegrityViolationException.class).when(carDao).save(car);
    
    fc.addCar(car);
  }

  /**
   * Test of getModels method, of class FleetController.
   */
  @Test
  public void testGetModels() {
    String m1 = "m1";
    String m2 = "m2";
    String m3 = "m3";
    
    Set<Car> cars = new HashSet<>();
    Car c1 = mock(Car.class);
    Car c2 = mock(Car.class);
    Car c3 = mock(Car.class);
    cars.add(c1);
    cars.add(c2);
    cars.add(c3);
    
    doReturn(m1).when(c1).getModel();
    doReturn(m2).when(c2).getModel();
    doReturn(m3).when(c3).getModel();
    
    doReturn(cars).when(carDao).findAll();
    
    Set<String> result = fc.getModels();
    
    assertThat(result, hasItem(m1));
    assertThat(result, hasItem(m2));
    assertThat(result, hasItem(m3));
  }
  
}
