package cfs.view.fleet;

import cfs.controller.FleetControllerInterface;
import cfs.data.entity.Car;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class FleetView extends VerticalLayout {
  
  public static final String PATH = "employees";
  
  private Button    addBtn;
  private Grid<Car> grid;
  
  @Autowired
  FleetControllerInterface fc;
  @Autowired
  EditFleetView editFleetView;
  
  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    addBtn = new Button("Add New");
    grid   = new Grid<>();
    
    grid.addColumn(car -> car.getPlate()).setCaption("Plate Number");
    grid.addColumn(car -> car.getModel()).setCaption("Model");
    grid.addColumn(car -> car.getProductionYear().getYear()).setCaption("Production Year");
    grid.addColumn(car -> car.getMilage()).setCaption("Milage");
    grid.addColumn(car -> car.getState()).setCaption("State");
    grid.addComponentColumn(car -> getScheduleButton(car)).setCaption("Maintenance");
    grid.addComponentColumn(car -> getEditButton(car));
    grid.addComponentColumn(car -> getDeleteButton(car));
    
    grid.setSelectionMode(Grid.SelectionMode.NONE);

    addBtn.addClickListener(event -> editFleet(null));
    
    setComponentValues();
  }

  public void setComponentValues() {
    Set<Car> cars;
    cars = fc.findCars();
    grid.setItems(cars);
  }

  private void setLayout() {
    setSizeFull();
    addBtn.setWidth("100%");
    grid.setSizeFull();
    addComponents(addBtn, grid);
    setExpandRatio(grid, 1);
  }

  private Button getScheduleButton(Car car) {
    Button button = new Button("Schedule");
    button.setSizeFull();
    
    return button;
  }

  private Button getEditButton(Car car) {
    Button button = new Button("Details");
    button.setSizeFull();
    
    button.addClickListener(event -> editFleet(car));
    
    return button;
  }

  private Button getDeleteButton(Car car) {
    Button button = new Button("Delete");
    button.setSizeFull();
    
    return button;
  }

  private void editFleet(Car car) {
    editFleetView.setData(car, this);
    getUI().addWindow(editFleetView);
  }
}
