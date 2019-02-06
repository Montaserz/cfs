package cfs.view.reservation;

import cfs.data.constants.CarStateEnum;
import cfs.controller.ReservationControllerInterface;
import cfs.data.entity.Reservation;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class EditTripView extends Window {
  
  private static final String PATH = "edittrip";
  
  private TextField        milage;
  private ComboBox<String> state;
  private Button           actionBtn;
  
  private TripView    master;
  private Reservation reservation;
  
  @Autowired
  ReservationControllerInterface rc;
  
  @PostConstruct
  protected void init() {
    addCloseListener(event -> master.setComponentValues());
    
    initComponents();
    setLayout();
  }
  
  public void setData(Reservation reservation, TripView master) {
    this.reservation = reservation;
    this.master      = master;
    
    //resetError();
    setComponentValues();
    //setPermissions();
  }
  
  private void initComponents() {
    milage    = new TextField("Milage");
    state     = new ComboBox<>();
    actionBtn = new Button();

    actionBtn.addClickListener(event -> saveTrip());

    List<String> states = new ArrayList<>();
    for (CarStateEnum cs : CarStateEnum.values())
      states.add(cs.name());
    
    state.setItems(states);
    state.setEmptySelectionAllowed(false);
  }

  public void setComponentValues() {
    milage.setValue(reservation.getCar().getMilage().toString());
    state.setValue(reservation.getCar().getState());
    
    if (reservation.getTrip() == null)
      actionBtn.setCaption("Start Trip");
    else
      actionBtn.setCaption("End Trip");
    
    if (reservation.getTrip() == null)
      state.setReadOnly(true);
  }

  private void setLayout() {
    milage.setSizeFull();
    state.setSizeFull();
    actionBtn.setSizeFull();
    
    FormLayout form = new FormLayout();
    form.addComponents(milage, state, actionBtn);
    
    VerticalLayout layout = new VerticalLayout(form);
    layout.setMargin(true);
    
    setContent(layout);
    setWidth("30%");
    setDraggable(false);
    setModal(true);
    center();
  }

  private void saveTrip() {
    System.out.println("Save Trip");
  }
}
