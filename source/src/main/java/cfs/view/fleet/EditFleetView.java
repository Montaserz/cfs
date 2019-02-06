package cfs.view.fleet;

import cfs.data.constants.CarStateEnum;
import cfs.controller.FleetControllerInterface;
import cfs.data.entity.Car;
import cfs.data.entity.Maintenance;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class EditFleetView extends Window {
  
  public static final String PATH = "editfleet";
  
  private Car               car;
  private TextField         plate;
  private ComboBox<String>  model;
  private ComboBox<Integer> productionYear;
  private DateField         purchaseDate;
  private TextField         milage;
  private ComboBox<String>  state;
  private Button            addBtn;
  private Button            updateBtn;
  private Button            editBtn;
  private Button            cancelBtn;
  private Grid<Maintenance> maintenance;
  
  private FleetView master;
  
  @Autowired
  FleetControllerInterface fc;

  @PostConstruct
  protected void init() {
    addCloseListener(event -> master.setComponentValues());
    initComponents();

    setDraggable(false);
    setModal(true);
  }
  
  public void setData(Car car, FleetView master) {
    this.car = car;
    this.master = master;
    
    resetError();
    setComponentValues();
    setLayout();
  }

  private void initComponents() {
    plate = new TextField("Plate Number");
    model = new ComboBox<>("Model");
    productionYear = new ComboBox<>("Production Year");
    purchaseDate = new DateField("Purchase Date");
    milage = new TextField("Milage");
    state = new ComboBox<>("State");
    addBtn = new Button("Add New");
    updateBtn = new Button("Update");
    editBtn = new Button("Edit");
    cancelBtn = new Button("Cancel");
    maintenance = new Grid();
    
    addBtn.addClickListener(event -> saveCar());
    updateBtn.addClickListener(event -> saveCar());
    editBtn.addClickListener(event -> setViewLayout(true));
    cancelBtn.addClickListener(event -> setViewLayout(false));
    
    addBtn.setSizeFull();
    editBtn.setSizeFull();
    cancelBtn.setSizeFull();
    updateBtn.setSizeFull();
    
    model.setPlaceholder("Select a Model");

    productionYear.setEmptySelectionAllowed(false);
    state.setEmptySelectionAllowed(false);
    
    setComponentValues();
  }

  public void setComponentValues() {
    Integer thisYear = LocalDate.now().getYear();
    
    List<Integer> years = new ArrayList<>();
    for (int i = 0; i < 50; ++i)
      years.add(thisYear-i);
    
    productionYear.setItems(years);
    purchaseDate.setRangeEnd(LocalDate.now());
    
    List<String> states = new ArrayList<>();
    
    CarStateEnum[] carStates = CarStateEnum.values();
    for (CarStateEnum cs : carStates)
      states.add(cs.name());
    
    state.setItems(states);

    Set<String> allModels = fc.getModels();
    SortedSet<String> models = new TreeSet<>();
    
    allModels.forEach((m) -> {
      models.add(m);
    });
    
    model.setItems(models);
    
    model.setNewItemProvider(string -> {
      models.add(string);
      model.setItems(models);
      return Optional.of(string);
    });

    if (car == null) {
      setCaption("Add New Car");
      
      plate.clear();
      model.clear();
      productionYear.setValue(thisYear);
      purchaseDate.setValue(LocalDate.now());
      milage.clear();
      state.setValue(CarStateEnum.OKAY.name());
      
      return;
    }
    
    setCaption("Edit Car");
    
    plate.setValue(car.getPlate());
    model.setValue(car.getModel());
    productionYear.setValue(car.getProductionYear().getYear());
    purchaseDate.setValue(car.getPurchaseDate());
    milage.setValue(car.getMilage().toString());
    state.setValue(car.getState());

  }

  private void setLayout() {
    if (car == null)
      setAddLayout();
    else
      setViewLayout(false);

  }
  
  private Layout getForm() {
    FormLayout form = new FormLayout();
    
    form.addComponents(plate, model, productionYear, purchaseDate, milage, state);
    
    plate.setSizeFull();
    model.setSizeFull();
    productionYear.setSizeFull();
    purchaseDate.setSizeFull();
    milage.setSizeFull();
    state.setSizeFull();
    
    return form;
  }
  
  private void setAddLayout() {
    Layout form = getForm();
    form.addComponent(addBtn);
    
    VerticalLayout layout = new VerticalLayout();
    layout.addComponent(form);
  
    setWidth("30%");
    
    setAccess(true);
    
    setContent(layout);
    center();
  }
  
  private void setViewLayout(boolean mode) {
    HorizontalLayout layout = new HorizontalLayout();
    VerticalLayout mLayout = new VerticalLayout();
    VerticalLayout formLayout = new VerticalLayout();
    
    Layout form = getForm();
    form.setSizeFull();
    
    formLayout.addComponent(form);
    formLayout.setSizeFull();
    
    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setSizeFull();
    
    if (!mode)
      buttons.addComponents(editBtn);
    else
      buttons.addComponents(cancelBtn, updateBtn);

    form.addComponents(buttons);
    mLayout.addComponents(maintenance);
    layout.addComponents(formLayout, mLayout);

    setWidth("80%");
    
    setAccess(mode);
    
    setContent(layout);
    center();
  }

  private void saveCar() {
    if (!validateForm()) return;
    
    Car tmp = car;
    setCar();
    
    try {
      car = fc.addCar(car);

      Notification.show("Car Saved!");
      
      if (tmp == null)
        setData(null, master);
      else
        setData(car, master);
    } catch (Exception e) {
      Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
    }
  }
  
  private void setCar() {
    if (car == null)
      car = new Car();
    
    car.setPlate(plate.getValue());
    car.setModel(model.getValue());
    car.setProductionYear(LocalDate.ofYearDay(productionYear.getValue(), 1));
    car.setPurchaseDate(purchaseDate.getValue());
    car.setMilage(Integer.parseInt(milage.getValue()));
    car.setState(state.getValue());
  }
  
  private void setAccess(boolean bool) {
    if (bool)
      setCaption("Edit Car");
    else
      setCaption("View Car");
    
    bool = !bool;
    
    plate.setReadOnly(bool);
    model.setReadOnly(bool);
    productionYear.setReadOnly(bool);
    purchaseDate.setReadOnly(bool);
    milage.setReadOnly(bool);
    state.setReadOnly(bool);
  }
  
  private boolean validateForm() {
    boolean bool = true;
    resetError();
    
    UserError empty = new UserError("Can not be empty!");
    UserError integer = new UserError("Value should be a positive integer!");
    
    if (plate.isEmpty()) {
      bool = false;
      plate.setComponentError(empty);
    }

    if (model.isEmpty()) {
      bool = false;
      model.setComponentError(empty);
    }

    if (milage.isEmpty()) {
      bool = false;
      milage.setComponentError(empty);
    }

    try {
      int milageVal = Integer.parseInt(milage.getValue());
      if (milageVal < 0) {
        bool = false;
        milage.setComponentError(integer);
      }
    } catch (Exception e) {
      bool = false;
      milage.setComponentError(integer);
    }
    
    return bool;
  }

  private void resetError() {
    plate.setComponentError(null);
    model.setComponentError(null);
    milage.setComponentError(null);
  }
}
