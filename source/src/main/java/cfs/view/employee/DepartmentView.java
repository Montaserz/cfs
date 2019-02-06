package cfs.view.employee;

import cfs.controller.EmployeeControllerInterface;
import cfs.data.entity.Department;
import cfs.data.entity.Employee;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class DepartmentView extends HorizontalLayout {
  
  public static final String PATH = "departments";
  
  private Grid<Department>   grid;
  private FormLayout         form;
  private TextField          name;
  private ComboBox<Employee> manager;
  private Button             saveBtn;
  private Button             deleteBtn;
  private Department         selected;
  
  @Autowired
  EmployeeControllerInterface ec;
  
  @PostConstruct
  protected void init() {
    initComponents();
    setLayout();
  }

  private void initComponents() {
    grid      = new Grid<>();
    form      = new FormLayout();
    name      = new TextField("Name");
    manager   = new ComboBox("Manager");
    saveBtn   = new Button("Add New");
    deleteBtn = new Button("Delete");
    selected  = null;
    
    form.setCaption("Add New Department");
    manager.setItemCaptionGenerator(emp -> emp.getEmail());
    
    deleteBtn.setEnabled(false);
    
    saveBtn.addClickListener(event -> {
      if (name.isEmpty()) {
        name.setComponentError(new UserError("Field Can Not Be Empty!"));
      }
      else {
        try {
          Department department = new Department(name.getValue(), manager.getValue());
          if (selected == null) ec.addDepartment(department);
          else ec.updateDepartment(selected, department);

          Notification.show("Department " + (selected == null ? "Saved" : "Updated"));

          setComponentValues();
        } catch (Exception e) {
          Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
      }
    });
    
    deleteBtn.addClickListener(event -> {
      try {
        ec.deleteDepartment((Department) selected);
        
        Notification.show("Department Deleted");
        
        setComponentValues();
      } catch (Exception e) {
        Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
      }
    });
    
    grid.asSingleSelect().addValueChangeListener(event -> {
      if(event.getValue() == null) {
        selected = null;
        name.clear();
        manager.clear();
        form.setCaption("Add New Department");
        saveBtn.setCaption("Add New");
        deleteBtn.setEnabled(false);
      }
      else {
        selected = (Department) event.getValue();
        name.setValue(selected.getName());
        manager.setValue(selected.getManager());
        form.setCaption("Edit " + selected.getName() + " Department");
        saveBtn.setCaption("Update");
        deleteBtn.setEnabled(true);
      }
      name.setComponentError(null);
      Set<Employee> managers = ec.findPossibleManagers(selected);
      manager.setItems(managers);
    });
    
    grid.addColumn(dep -> dep.getName()).setCaption("Department");
    grid.addColumn(dep -> dep.getManager() == null ? null : dep.getManager().getEmail()).setCaption("Manager");
    grid.addColumn(dep -> ec.findEmployees(dep).size()).setCaption("Employee Count");

    setComponentValues();
  }

  public void setComponentValues() {
    Set<Department> departments = ec.findDepartments();
    grid.setItems(departments);
    
    Set<Employee> managers = ec.findPossibleManagers(selected);
    manager.setItems(managers);
    
    name.setComponentError(null);
    name.clear();
  }

  private void setLayout() {
    grid.setSizeFull();
    form.setSizeFull();
    
    form.addComponents(name, manager, saveBtn, deleteBtn);
    
    name.setWidth("100%");
    manager.setWidth("100%");
    saveBtn.setWidth("100%");
    deleteBtn.setWidth("100%");
    
    addComponents(grid, form);

    setSizeFull();
    setMargin(true);
    setExpandRatio(grid, 3);
    setExpandRatio(form, 1);
  }
}
