package cfs.view.reservation;

import cfs.controller.ReservationControllerInterface;
import cfs.data.entity.Reservation;
import cfs.data.entity.Trip;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class TripView extends VerticalLayout {
  
  public static final String PATH = "trips";
  
  private HorizontalLayout  viewMenu;
  private Button            activeBtn;
  private Button            passiveBtn;
  private Grid<Reservation> active;
  private Grid<Trip>        passive;
  
  @Autowired
  ReservationControllerInterface rc;
  @Autowired
  EditTripView editTripView;

  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    viewMenu   = new HorizontalLayout();
    activeBtn  = new Button("Active Trips");
    passiveBtn = new Button("Trips History");
    
    active = new Grid<>();
    active.addColumn(res -> res.getEmployee().getEmail()).setCaption("Employee");
    active.addColumn(res -> res.getEmployee().getEmail()).setCaption("Car");
    active.addColumn(res -> res.getEmployee().getEmail()).setCaption("Start Date");
    active.addColumn(res -> res.getEmployee().getEmail()).setCaption("End Date");
    active.addComponentColumn(res -> getActionButton(res));
    
    passive = new Grid<>();
    passive.addColumn(trip -> trip.getReservation().getEmployee().getEmail()).setCaption("Employee");
    passive.addColumn(trip -> trip.getReservation().getCar().getPlate()).setCaption("Car");
    passive.addColumn(trip -> trip.getStart_date()).setCaption("Start Date");
    passive.addColumn(trip -> trip.getEnd_date()).setCaption("End Date");
    passive.addColumn(trip -> trip.getStart_milage()).setCaption("Start Milage");
    passive.addColumn(trip -> trip.getEnd_milage()).setCaption("End Milage");
    
    activeBtn.addClickListener(event -> changeView(true));
    passiveBtn.addClickListener(event -> changeView(false));
    
    setComponentValues();
  }

  public void setComponentValues() {
    //List<Reservation> activeTrips = rc.findActiveTrips();
    //List<Trip> passiveTrips = rc.findPassiveTrips();
  }

  private void setLayout() {
    activeBtn.setWidth("100%");
    passiveBtn.setWidth("100%");
    
    viewMenu.setWidth("100%");
    viewMenu.addComponents(activeBtn, passiveBtn);
    
    active.setSizeFull();
    passive.setSizeFull();
    
    changeView(true);
    setSizeFull();
    setMargin(true);
  }

  private void changeView(boolean bool) {
    removeAllComponents();
    if (bool) {
      addComponents(viewMenu, active);
      setExpandRatio(active, 1);
    }
    else {
      addComponents(viewMenu, passive);
      setExpandRatio(passive, 1);
    }
  }

  private Button getActionButton(Reservation res) {
    Button button = new Button();
    
    if (res.getTrip() == null)
      button.setCaption("Start Trip");
    else
      button.setCaption("End Trip");
    
    button.addClickListener(event -> editTrip(res));
    return button;
  }

  private void editTrip(Reservation res) {
    editTripView.setData(res, this);
    getUI().addWindow(editTripView);
  }

}
