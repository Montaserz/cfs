package cfs.view.employee;

import cfs.controller.EmployeeControllerInterface;
import cfs.data.constants.RoleEnum;
import cfs.data.entity.Employee;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class ProfileView extends Window {

  public static final String PATH = "profile";

  private Employee      user       ;
  private TextField     email      ;
  private TextField     name       ;
  private TextField     surname    ;
  private TextField     department ;
  private TextField     managing   ;
  private PasswordField pass1      ;
  private PasswordField pass2      ;
  private Button        updateBtn  ;
  
  @Autowired
  EmployeeControllerInterface ec;

  @PostConstruct
  protected void init() {
    setCaption("My Profile");
    
    initComponents();
    setLayout();
  }

  private void initComponents() {
    email      = new TextField("Email");
    name       = new TextField("Name");
    surname    = new TextField("Surname");
    department = new TextField("Department");
    managing   = new TextField("Managing");
    pass1      = new PasswordField("New Password");
    pass2      = new PasswordField("New Password");
    updateBtn  = new Button("Change Password");
    
    updateBtn.addClickListener(event -> updatePassword(user, pass1, pass2));
    
    email.setReadOnly(true);
    name.setReadOnly(true);
    surname.setReadOnly(true);
    department.setReadOnly(true);
    managing.setReadOnly(true);
    
    pass1.setPlaceholder("Enter Password");
    pass2.setPlaceholder("Re-Enter Password");

    setComponentValues();
  }

  public void setComponentValues() {
    user = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    
    email.setValue(user.getEmail());
    name.setValue(user.getFirstName());
    surname.setValue(user.getLastName());

    department.setValue(user.getDepartment().getName());

    if (ec.hasRole(user, RoleEnum.DM))
      managing.setValue(user.getManaging().getName());
    else
      managing.clear();
    
    pass1.clear();
    pass2.clear();
  }
  
  public void setLayout() {
    email.setSizeFull();
    name.setSizeFull();
    surname.setSizeFull();
    department.setSizeFull();
    managing.setSizeFull();
    pass1.setSizeFull();
    pass2.setSizeFull();
    updateBtn.setSizeFull();
    
    FormLayout form = new FormLayout();
    form.addComponents(name, surname, email, department, managing, pass1, pass2, updateBtn);
    
    VerticalLayout layout = new VerticalLayout(form);
    layout.setMargin(true);
    
    setContent(layout);
    setWidth("30%");
    setDraggable(false);
    setModal(true);
    center();
  }

  private void updatePassword(Employee user, PasswordField pass1, PasswordField pass2) {
    if (pass1.isEmpty())
      Notification.show("Password can not be empty!", Notification.Type.ERROR_MESSAGE);
    else if (!pass1.getValue().equals(pass2.getValue()))
      Notification.show("Passwords do not match!", Notification.Type.ERROR_MESSAGE);
    else {
      try {
      ec.updatePassword(user, pass1.getValue());
      Notification.show("Password updated!");
      } catch (Exception e) {
        Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
      } finally {
        pass1.clear();
        pass2.clear();
      }
    }
  }
}
