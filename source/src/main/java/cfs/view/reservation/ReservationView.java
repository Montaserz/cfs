package cfs.view.reservation;

import cfs.controller.EmployeeControllerInterface;
import cfs.controller.ReservationControllerInterface;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import com.vaadin.server.VaadinService;
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
public class ReservationView extends VerticalLayout {
  
  public static final String PATH = "reservations";

  private Employee          user;
  private Button            addBtn;
  private Grid<Reservation> grid;
  
  @Autowired
  EmployeeControllerInterface ec;
  @Autowired
  ReservationControllerInterface rc;
  @Autowired
  EditReservationView editReservationView;
  
  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    user   = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    addBtn = new Button("Add New");
    grid   = new Grid<>();
    
    grid.addColumn(res -> res.getEmployee().getEmail()).setCaption("Employee");
    grid.addColumn(res -> res.getTitle()).setCaption("Title");
    grid.addColumn(res -> res.getStartDate()).setCaption("Start Date");
    grid.addColumn(res -> res.getEndDate()).setCaption("End Date");
    grid.addColumn(res -> res.getState()).setCaption("State");
    grid.addColumn(res -> res.getCar() == null ? "" : res.getCar().getPlate()).setCaption("Car");
    grid.addComponentColumn(res -> getEditButton(res));
    grid.addComponentColumn(res -> getDeleteButton(res));
    
    grid.setSelectionMode(Grid.SelectionMode.NONE);

    addBtn.addClickListener(event -> editReservation(null));
    
    setComponentValues();
  }

  public void setComponentValues() {
    Set<Reservation> reservations = rc.findReservations(user);
    
    grid.setItems(reservations);
  }

  private void setLayout() {
    setSizeFull();
    addBtn.setWidth("100%");
    grid.setSizeFull();
    addComponents(addBtn, grid);
    setExpandRatio(grid, 1);
  }

  private Button getEditButton(Reservation res) {
    Button button = new Button("Details");

    button.setSizeFull();
    
    button.addClickListener(event -> editReservation(res));
    
    return button;
  }

  private Button getDeleteButton(Reservation res) {
    Button button = new Button("Delete");

    button.setSizeFull();
    
    button.addClickListener(event -> editReservation(res));
    
    return button;
  }

  private void editReservation(Reservation res) {
    editReservationView.setData(res, this);
    getUI().addWindow(editReservationView);
  }
  
  
}
