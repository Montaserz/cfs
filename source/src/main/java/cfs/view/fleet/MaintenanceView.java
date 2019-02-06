package cfs.view.fleet;

import cfs.controller.FleetControllerInterface;
import cfs.data.entity.Maintenance;
import cfs.data.entity.Reservation;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class MaintenanceView extends VerticalLayout {
  
  public static final String PATH = "trips";
  
  private HorizontalLayout  viewMenu;
  private HorizontalLayout  passiveContent;
  private Button            activeBtn;
  private Button            passiveBtn;
  private Grid<Maintenance> active;
  private Grid<Maintenance> passive;
  private TextArea          note;
  
  @Autowired
  FleetControllerInterface fc;
  //@Autowired
  //EditMaintenanceView editMaintenanceView;

  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    viewMenu       = new HorizontalLayout();
    passiveContent = new HorizontalLayout();
    
    activeBtn  = new Button("Active Maintenance");
    passiveBtn = new Button("Maintenace History");
    
    active = new Grid<>();
    active.addColumn(mnt -> mnt.getCar().getPlate()).setCaption("Car");
    active.addColumn(mnt -> mnt.getStartDate()).setCaption("Start Date");
    active.addColumn(mnt -> mnt.getEndDate()).setCaption("End Date");
    active.addComponentColumn(mnt -> getEditButton(mnt));
    active.addComponentColumn(mnt -> getDeleteButton(mnt));
    
    passive = new Grid<>();
    passive.addColumn(mnt -> mnt.getCar().getPlate()).setCaption("Car");
    passive.addColumn(mnt -> mnt.getStartDate()).setCaption("Start Date");
    passive.addColumn(mnt -> mnt.getEndDate()).setCaption("End Date");    
    passive.asSingleSelect().addValueChangeListener(event -> {
      if (event.getValue() == null)
        note.clear();
      else
        note.setValue(event.getValue().getNote());
    });
    
    note = new TextArea("Maintenance Notes");

    activeBtn.addClickListener(event -> changeView(true));
    passiveBtn.addClickListener(event -> changeView(false));
    
    setComponentValues();
  }

  public void setComponentValues() {
    //List<Maintenance> activeMnt = fc.findActiveMaintenance();
    //List<Maintenance> passiveMnt = fc.findPassiveMaintenance();
  }

  private void setLayout() {
    activeBtn.setWidth("100%");
    passiveBtn.setWidth("100%");
    
    viewMenu.setWidth("100%");
    viewMenu.addComponents(activeBtn, passiveBtn);
    
    active.setSizeFull();
    passive.setSizeFull();
    note.setSizeFull();
    
    passiveContent.setSizeFull();
    passiveContent.addComponents(passive, note);
    passiveContent.setExpandRatio(passive, 4);
    passiveContent.setExpandRatio(note, 1);
    
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
      addComponents(viewMenu, passiveContent);
      setExpandRatio(passiveContent, 1);
    }
  }

  private Button getEditButton(Maintenance res) {
    Button button = new Button("Edit");
    
    //button.addClickListener(event -> editTrip(res));
    
    return button;
  }

  private Button getDeleteButton(Maintenance res) {
    Button button = new Button("Delete");
    
    //button.addClickListener(event -> editTrip(res));
    
    return button;
  }

  private void editMaintenance(Maintenance mnt) {
    //editTripView.setData(res, this);
    //getUI().addWindow(editTripView);
  }

}
