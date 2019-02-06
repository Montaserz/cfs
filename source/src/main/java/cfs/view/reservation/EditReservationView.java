package cfs.view.reservation;

import cfs.controller.ReservationControllerInterface;
import cfs.data.constants.ReservationStateEnum;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import com.vaadin.data.HasValue;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class EditReservationView extends Window {
  
  private Employee user;
  private Reservation reservation;
  
  private TextField title;
  private TextArea description;
  private DateField startDate;
  private DateField endDate;
  
  private FormLayout form;
  private VerticalLayout layout;
  
  private Button saveBtn;
  private Button editBtn;
  private Button updateBtn;
  private Button cancelBtn;
  private Button submitBtn;
  
  private ReservationView master;
  
  @Autowired
  ReservationControllerInterface rc;
  
  @PostConstruct
  protected void init() {
    addCloseListener(event -> master.setComponentValues());

    user = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    
    setWidth("30%");
    setDraggable(false);
    setModal(true);

    initComponents();
  }

  private void initComponents() {
    saveBtn      = new Button("Save");
    editBtn      = new Button("Edit");
    updateBtn    = new Button("Update");
    cancelBtn    = new Button("Cancel");
    submitBtn    = new Button("Submit");
    
    saveBtn.setSizeFull();
    editBtn.setSizeFull();
    submitBtn.setSizeFull();
    updateBtn.setSizeFull();
    cancelBtn.setSizeFull();
    
    saveBtn.addClickListener(event -> saveReservation());
    
    editBtn.addClickListener(event -> setComponents(true));
    submitBtn.addClickListener(event -> submitReservation());
    
    updateBtn.addClickListener(event -> saveReservation());
    cancelBtn.addClickListener(event -> setData(reservation, master));
  }
    
  public void setData(Reservation reservation, ReservationView master) {
    this.reservation = reservation;
    this.master = master;

    title = new TextField("Title");
    description = new TextArea("Description");

    //resetError();
    
    if (reservation == null)
      setNewComponents();
    else
      setComponents(false);
  }

  private void setNewComponents() {
    setCaption("Add New Reservation");
    
    startDate = new DateField("Start Date");
    startDate.setRangeStart(LocalDate.now());
    startDate.addValueChangeListener(event -> updateEndDate(event));

    endDate = new DateField("End Date");
    endDate.setReadOnly(true);

    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setSizeFull();
    buttons.addComponents(saveBtn, submitBtn);
    
    setLayout(buttons);
  }

  private void setComponents(boolean b) {
    setCaption("Reservation Details");
    
    startDate = new DateField("Start Date");
    startDate.setValue(reservation.getStartDate());
    startDate.addValueChangeListener(event -> updateEndDate(event));

    endDate = new DateField("End Date");
    endDate.setValue(reservation.getEndDate());
    endDate.setRangeStart(startDate.getValue());

    title.setValue(reservation.getTitle());
    description.setValue(reservation.getDescription());

    title.setReadOnly(!b);
    description.setReadOnly(!b);
    startDate.setReadOnly(!b);
    endDate.setReadOnly(!b);


    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setSizeFull();
    
    if (!b && reservation.getState().equals(ReservationStateEnum.NEW.name()))
      buttons.addComponents(editBtn, submitBtn);
    else if (reservation.getState().equals(ReservationStateEnum.NEW.name()))
      buttons.addComponents(cancelBtn, updateBtn);
    
    setLayout(buttons);
  }

  private void setLayout(HorizontalLayout buttons) {
    title.setSizeFull();
    description.setSizeFull();
    startDate.setSizeFull();
    endDate.setSizeFull();
    
    form = new FormLayout();
    form.addComponents(title, description, startDate, endDate);  
  
    layout = new VerticalLayout(form, buttons);
    layout.setMargin(true);
    
    setContent(layout);
    center();
  }

  private void updateEndDate(HasValue.ValueChangeEvent<LocalDate> event) {
    DateField endDate2 = new DateField("End Date");
    endDate2.setRangeStart(event.getValue());
    endDate2.setSizeFull();

    form.replaceComponent(endDate, endDate2);
    endDate = endDate2;
  }

  private Reservation saveReservation() {
    if (!validateForm()) return null;
    
    Reservation res = getNewReservation();
    
    try {
      res = rc.saveReservation(res);

      if (reservation == null) {
        Notification.show("Reservation Saved");
        setData(null, master);
      }
      else {
        Notification.show("Reservation Updated");
        setData(res, master);
      }
      
    } catch (Exception e) {
      Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
    }
    
    return res;
  }

  private Reservation getNewReservation() {
    Reservation res = new Reservation();
    
    res.setEmployee(user);
    res.setTitle(title.getValue());
    res.setDescription(description.getValue());
    res.setStartDate(startDate.getValue());
    res.setEndDate(endDate.getValue());
    res.setState(ReservationStateEnum.NEW.name());
    
    if (reservation != null)
      res.setId(reservation.getId());
    
    return res;
  }

  private void submitReservation() {
    Reservation res = saveReservation();
    
    if (res != null)
      rc.submitReservation(res);
  }

  private boolean validateForm() {
    boolean bool = true;
    
    UserError empty = new UserError("Field Can Not Be Empty!");
    
    if (title.isEmpty()) {
      bool = false;
      title.setComponentError(empty);
    }

    if (description.isEmpty()) {
      bool = false;
      description.setComponentError(empty);
    }
    
    if (startDate.isEmpty()) {
      bool = false;
      startDate.setComponentError(empty);
    }

    if (endDate.isEmpty()) {
      bool = false;
      endDate.setComponentError(empty);
    }
    
    if (bool)
      resetError();

    return bool;
  }

  private void resetError () {
    title.setComponentError(null);
    description.setComponentError(null);
    startDate.setComponentError(null);
    endDate.setComponentError(null);
  }  
}
