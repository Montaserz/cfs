package cfs.view.reservation;

import cfs.controller.ReservationControllerInterface;
import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class ConfirmationView extends HorizontalLayout {

  public static final String PATH = "confirmations";
  
  private Employee          user;
  private Grid<Reservation> grid;
  private TextArea          description;
  
  @Autowired
  ReservationControllerInterface rc;

  @PostConstruct
  protected void init() {
    initComponents();
    setComponentValues();
    setLayout();
  }

  private void initComponents() {
    user        = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    grid        = new Grid<>();
    description = new TextArea("Description");
    
    grid.addColumn(res -> res.getEmployee().getEmail()).setCaption("Employee");
    grid.addColumn(res -> res.getTitle()).setCaption("Title");
    grid.addColumn(res -> res.getStartDate()).setCaption("Start Date");
    grid.addColumn(res -> res.getEndDate()).setCaption("End Date");
    grid.addComponentColumn(res -> getActionButton(res, true));
    grid.addComponentColumn(res -> getActionButton(res, false));
    
    grid.asSingleSelect().addValueChangeListener(event -> {
      if (event.getValue() == null)
        description.clear();
      else
        description.setValue(event.getValue().getDescription());
    });
  }

  public void setComponentValues() {
    List<Reservation> reservations = rc.findAllConfirmations(user);
    grid.setItems(reservations);
  }

  private void setLayout() {
    grid.setSizeFull();
    description.setSizeFull();
    
    addComponents(grid, description);
    setSizeFull();
    setMargin(true);
    setExpandRatio(grid, 4);
    setExpandRatio(description, 1);
  }

  private Button getActionButton(Reservation res, boolean b) {
    Button button = new Button();
    button.setSizeFull();
    
    button.setCaption(b ? "Accept" : "Reject");
    
    ConfirmationView cv = this;
    
    button.addClickListener(event -> {
      rc.setConfirmation(res, b);
      cv.setComponentValues();
            });
    
    return button;
  }
  
  
}
