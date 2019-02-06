package cfs.view.employee;

import cfs.controller.EmployeeControllerInterface;
import cfs.data.constants.RoleEnum;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class EditEmployeeView extends Window {

  public static final String PATH = "editemployee";

  private Employee             user        ;
  private Employee             employee    ;
  private TextField            email       ;
  private TextField            name        ;
  private TextField            surname     ;
  private ComboBox<Department> department  ;
  private CheckBox             fleetManager;
  private Button               saveBtn     ;
  
  private EmployeeView         master;
  private boolean              newForm;
  
  @Autowired
  EmployeeControllerInterface ec;

  @PostConstruct
  protected void init() {
    addCloseListener(event -> master.setComponentValues(null));

    newForm = true;
    
    initComponents();
    setLayout();
    setPermissions();
  }
  
  public void setData(Employee employee, EmployeeView master) {
    this.employee = employee;
    this.master   = master;
    
    newForm = employee == null;
    
    resetError();
    setComponentValues();
  }

  private void initComponents() {
    email        = new TextField("Email");
    name         = new TextField("Name");
    surname      = new TextField("Surname");
    department   = new ComboBox<>("Department");
    fleetManager = new CheckBox("Fleet Manager");
    saveBtn      = new Button();

    saveBtn.addClickListener(event -> saveEmployee());
    
    department.setItemCaptionGenerator(dep -> dep.getName());
    
    department.setPlaceholder("Select a Department");

    setComponentValues();
  }

  public void setComponentValues() {
    user = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");

    Set<Department> allDepartments = ec.findDepartments();
    department.setItems(allDepartments);

    if (newForm) {
      setCaption("Add New Employee");
      
      email.clear();
      name.clear();
      surname.clear();
      department.setValue(user.getDepartment());
      fleetManager.setValue(Boolean.FALSE);

      saveBtn.setCaption("Add Employee");
      
      return;
    }
    
    setCaption("Edit Employee");
    
    email.setValue(employee.getEmail());
    name.setValue(employee.getFirstName());
    surname.setValue(employee.getLastName());
    department.setValue(employee.getDepartment());

    fleetManager.setValue(ec.hasRole(employee, RoleEnum.FM));

    saveBtn.setCaption("Update Employee");
  }
  
  public void setLayout() {
    email.setSizeFull();
    name.setSizeFull();
    surname.setSizeFull();
    department.setSizeFull();
    fleetManager.setSizeFull();
    saveBtn.setSizeFull();
    
    FormLayout form = new FormLayout();
    form.addComponents(name, surname, email, department, fleetManager, saveBtn);
    if (!ec.hasRole(user, RoleEnum.CEO)) {
      form.removeComponent(fleetManager);
    }
    
    VerticalLayout layout = new VerticalLayout(form);
    layout.setMargin(true);
    
    setContent(layout);
    setWidth("30%");
    setDraggable(false);
    setModal(true);
    center();
  }

  private void saveEmployee() {
    if (!validateForm()) return;

    try {
      if (employee == null) {
        Employee newEmployee = new Employee(name.getValue(), surname.getValue(), email.getValue(), department.getValue());
        ec.addEmployee(newEmployee, fleetManager.getValue());
      }
      else {
        Employee newEmployee = new Employee(name.getValue(), surname.getValue(), email.getValue(), department.getValue());
        employee = ec.updateEmployee(employee, newEmployee, fleetManager.getValue());
      }

      Notification.show("Employee " + (employee == null ? "Saved" : "Updated"));

      if (employee == null) setData(null, master);
      else setData(employee, master);
    
    } catch (Exception e) {
      Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
    }
  }
  
  private boolean validateForm() {
    boolean bool = true;
    
    UserError empty = new UserError("Field Can Not Be Empty!");
    
    if (name.isEmpty()) {
      bool = false;
      name.setComponentError(empty);
    }

    if (surname.isEmpty()) {
      bool = false;
      surname.setComponentError(empty);
    }
    
    if (email.isEmpty()) {
      bool = false;
      email.setComponentError(empty);
    }

    if (department.isEmpty()) {
      bool = false;
      department.setComponentError(empty);
    }
    
    if (bool)
      resetError();

    return bool;
  }
  
  private void resetError () {
    email.setComponentError(null);
    name.setComponentError(null);
    surname.setComponentError(null);
    department.setComponentError(null);
  }

  private void setPermissions() {
    if (ec.hasRole(user, RoleEnum.CEO)) return;
    
    department.setReadOnly(true);
    fleetManager.setReadOnly(true);
  }
}
