package cfs.view;

import cfs.view.employee.EmployeeView;
import cfs.view.employee.ProfileView;
import cfs.view.employee.DepartmentView;
import cfs.controller.EmployeeControllerInterface;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import cfs.data.constants.RoleEnum;
import cfs.data.entity.Employee;
import cfs.view.fleet.FleetView;
import cfs.view.fleet.MaintenanceView;
import cfs.view.reservation.ConfirmationView;
import cfs.view.reservation.ReservationView;
import cfs.view.reservation.TripView;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UIScope
@SpringView(name = MainView.PATH)
public class MainView extends VerticalLayout implements View {
  
  public static final String PATH = "main";
  
  private Employee user;
  private Label    title;
  private MenuBar  profileMenu;
  private MenuBar  mainMenu;
  private Panel    panel;
  private String   navRoot;

  @Autowired
  EmployeeControllerInterface ec;
  @Autowired
  ProfileView profileView;
  @Autowired
  ReservationView reservationView;
  @Autowired
  ConfirmationView confirmationView;
  @Autowired
  TripView tripView;
  @Autowired
  EmployeeView employeeView;
  @Autowired
  DepartmentView departmentView;
  @Autowired
  FleetView fleetView;
  @Autowired
  MaintenanceView maintenanceView;
  
  @PostConstruct
  protected void init() {
    setId(PATH);
    initComponents();
    setLayout();
//    panel.setContent(employeeView);
  }
  
  @Override
  public void enter(ViewChangeEvent event) {
    if(VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user") == null)
      event.getNavigator().navigateTo("login");
  }

  private void initComponents() {
    user = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");

    navRoot     = new String("COMPANY FLEET SYSTEM");
    title       = new Label(navRoot);
    panel       = new Panel();

    profileMenu = getProfileMenu();
    mainMenu    = getMainMenu();
  }
  
  public void setComponentValues() {
    user = (Employee) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user");
    profileMenu = getProfileMenu();
    setLayout();
  }

  private MenuBar getProfileMenu() {
    MenuBar menu = new MenuBar();
    
    MainView mv = this;
    
    MenuBar.Command command = new MenuBar.Command() {
      @Override
      public void menuSelected(MenuBar.MenuItem selectedItem) {
        switch(selectedItem.getText()) {
          case "Log out" :
            VaadinService.getCurrentRequest().getWrappedSession().invalidate();
            mv.getUI().getPage().setLocation("");
            break;
          case "Profile" :
            profileView.setComponentValues();
            getUI().addWindow(profileView);
            break;
          default : Notification.show("Undefined Profile Action!");
        }
      }
    };
    
    MenuBar.MenuItem subMenu = menu.addItem(user.getEmail());
    subMenu.addItem("Profile", command);
    subMenu.addItem("Log out", command);
    
    return menu;
  }

  private MenuBar getMainMenu() {
    MenuBar menu = new MenuBar();
    
    MainView mainView = this;
    
    Command command = new MenuBar.Command() {
      @Override
      public void menuSelected(MenuBar.MenuItem selectedItem) {
        title.setValue(getNavigation(selectedItem));
        switch(selectedItem.getText()) {
          case "Reservations" :
            reservationView.setComponentValues();
            panel.setContent(reservationView);
            break;
          case "Confirmations" :
            confirmationView.setComponentValues();
            panel.setContent(confirmationView);
            break;
          case "Trips" :
            tripView.setComponentValues();
            panel.setContent(tripView);
            break;
          case "Employees" :
            employeeView.setComponentValues(mainView);
            panel.setContent(employeeView);
            break;
          case "Departments" :
            departmentView.setComponentValues();
            panel.setContent(departmentView);
            break;
          case "Cars" :
            fleetView.setComponentValues();
            panel.setContent(fleetView);
            break;
          case "Maintenance" :
            maintenanceView.setComponentValues();
            panel.setContent(maintenanceView);
            break;
          default : Notification.show("Invalid Menu Option!", Notification.Type.ERROR_MESSAGE);
        }
      }

      private String getNavigation(MenuBar.MenuItem selectedItem) {
        String seperator = " > ";
        List<String> path = new ArrayList<>();
        String navigation = "";

        while (selectedItem != null) {
          path.add(selectedItem.getText().toUpperCase());
          path.add(seperator);
          selectedItem = selectedItem.getParent();
        }
        
        path.add(navRoot);
        Collections.reverse(path);

        for (String s : path)
          navigation += s;

        return navigation;
      }
    };
    
    addReservationManagement(menu, command);
    addEmployeeManagement(menu, command);
    addFleetManagement(menu, command);
    
    return menu;  }

  private void addReservationManagement(MenuBar menu, Command command) {
    MenuBar.MenuItem subMenu = menu.addItem("Reservation Management");
    
    subMenu.addItem("Reservations", command);
    
    if (ec.hasRole(user, RoleEnum.CEO) || ec.hasRole(user, RoleEnum.DM))
      subMenu.addItem("Confirmations", command);
    
    if (ec.hasRole(user, RoleEnum.CEO) || ec.hasRole(user, RoleEnum.FM))
      subMenu.addItem("Trips", command);
  }

  private void addEmployeeManagement(MenuBar menu, Command command) {
    if (!ec.hasRole(user, RoleEnum.CEO) && !ec.hasRole(user, RoleEnum.DM)) return;
    
    MenuBar.MenuItem subMenu = menu.addItem("Employee Management");
    
    subMenu.addItem("Employees", command);
    
    if (ec.hasRole(user, RoleEnum.CEO))
      subMenu.addItem("Departments", command);
  }

  private void addFleetManagement(MenuBar menu, Command command) {
    if (!ec.hasRole(user, RoleEnum.CEO) && !ec.hasRole(user, RoleEnum.FM)) return;
    
    MenuBar.MenuItem subMenu = menu.addItem("Fleet Management");
    
    subMenu.addItem("Cars", command);
    subMenu.addItem("Maintenance", command);
  }

  private void setLayout() {
    removeAllComponents();
    
    HorizontalLayout header = new HorizontalLayout();

    header.addComponents(title, profileMenu);
    header.setComponentAlignment(profileMenu, Alignment.TOP_RIGHT);
    
    setSizeFull();
    header.setWidth("100%");
    panel.setSizeFull();
    
    addComponents(header, mainMenu, panel);
    this.setExpandRatio(panel, 1);
  }
}
