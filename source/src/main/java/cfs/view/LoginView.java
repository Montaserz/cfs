package cfs.view;

import cfs.controller.EmployeeControllerInterface;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import cfs.data.entity.Employee;
import cfs.view.MainView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@UIScope
@SpringView(name = LoginView.PATH)
public class LoginView extends VerticalLayout implements View {
  
  public static final String PATH = "login";
  
  private Label         title;
  private TextField     email;
  private PasswordField pass;
  private Button        loginBtn;
  
  @Autowired
  private EmployeeControllerInterface ec;
  
  @PostConstruct
  protected void init() {
    setId(PATH);
    initComponents();
    setLayout();
  }
  
  @Override
  public void enter(ViewChangeListener.ViewChangeEvent event) {
    if(VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user") != null)
      event.getNavigator().navigateTo("main");
  }
  
  private void initComponents() {
    title = new Label("COMPANY FLEEY SYSTEM > LOGIN");
    
    email = new TextField("Email");
    email.setIcon(VaadinIcons.USER);
    
    pass = new PasswordField("Password");
    pass.setIcon(VaadinIcons.LOCK);
    
    loginBtn = new Button("Login");
    loginBtn.addClickListener(event -> login(email, pass));
  }

  private void login(TextField email, PasswordField pass) {
    Employee user = ec.login(email.getValue(), pass.getValue());
    
    if(user == null)
      Notification.show("Login Failed!", Notification.Type.ERROR_MESSAGE);
    else {
      VaadinService.getCurrentRequest().getWrappedSession().setAttribute("user", user);
      getUI().getNavigator().navigateTo(MainView.PATH);
    }
  }

  private void setLayout() {
    email.setWidth("200");
    pass.setWidth("200");
    loginBtn.setWidth("200");
    
    FormLayout form = new FormLayout();
    form.addComponents(email, pass, loginBtn);
    
    addComponents(title, form);
  }

}
