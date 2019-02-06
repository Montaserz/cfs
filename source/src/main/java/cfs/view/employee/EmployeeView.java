package cfs.view.employee;

import cfs.data.constants.RoleEnum;
import cfs.controller.EmployeeControllerInterface;
import cfs.data.entity.Employee;
import cfs.view.MainView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalLayout;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class EmployeeView extends VerticalLayout {
  
  public static final String PATH = "employees";
  
  private Employee       user;
  private Button         addBtn;
  private Grid<Employee> grid;
  private MainView       master;
  
  @Autowired
  EmployeeControllerInterface ec;
  @Autowired
  EditEmployeeView editEmployeeView;

  
  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    user   = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    addBtn = new Button("Add New");
    grid   = new Grid<>();
    
    grid.addColumn(emp -> emp.getEmail()).setCaption("Email");
    grid.addColumn(emp -> emp.getFirstName()).setCaption("Name");
    grid.addColumn(emp -> emp.getLastName()).setCaption("Surname");
    grid.addColumn(emp -> emp.getDepartment() == null ? null : emp.getDepartment().getName()).setCaption("Department");
    grid.addColumn(emp -> emp.getManaging() == null ? null : emp.getManaging().getName()).setCaption("Managing");
    grid.addComponentColumn(emp -> getBooleanIcon(ec.hasRole(emp, RoleEnum.FM))).setCaption("FM").setMaximumWidth(85);
    grid.addComponentColumn(emp -> getEditButton(emp));
    grid.addComponentColumn(emp -> getDeleteButton(emp));
    
    grid.setSelectionMode(SelectionMode.NONE);

    addBtn.addClickListener(event -> editEmployee(null));
  }

  public void setComponentValues(MainView mainView) {
    if (mainView != null)
      this.master = mainView;
    
    Set<Employee> employees;
    
    if (ec.hasRole(user, RoleEnum.CEO))
      employees = ec.findEmployees();
    else
      employees = ec.findEmployees(user.getManaging());
    
    grid.setItems(employees);
    
    master.setComponentValues();
  }

  private void setLayout() {
    setSizeFull();
    addBtn.setWidth("100%");
    grid.setSizeFull();
    addComponents(addBtn, grid);
    setExpandRatio(grid, 1);
  }

  private Button getEditButton(Employee emp) {
    Button button = new Button("Edit");

    button.setSizeFull();
    
    button.addClickListener(event -> editEmployee(emp));
    
    return button;
  }

  private Button getDeleteButton(Employee emp) {
    Button button = new Button("Delete");
    
    button.setSizeFull();
    
    button.addClickListener(event -> ec.deleteEmployee(emp));
    
    return button;
  }

  private void editEmployee(Employee emp) {
    editEmployeeView.setData(emp, this);
    getUI().addWindow(editEmployeeView);
  }
  
  private Button getBooleanIcon(boolean bool) {
    Button button = new Button();
    button.setEnabled(false);
    
    if (bool) {
      button.setIcon(VaadinIcons.CHECK);
      return button;
    }
    
    return null;
  }
}
