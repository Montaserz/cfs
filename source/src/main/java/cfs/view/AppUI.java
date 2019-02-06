package cfs.view;

import cfs.controller.EmployeeControllerInterface;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@SpringViewDisplay
@Theme("mytheme")
public class AppUI extends UI implements ViewDisplay {
  
  private Panel root;
  
  @Override
  protected void init(VaadinRequest request) {
    root = new Panel();
    root.setSizeFull();
    setContent(root);
    
    if(VaadinService.getCurrentRequest().getWrappedSession().getAttribute("user") == null)
      this.getNavigator().navigateTo(LoginView.PATH);
    else
      this.getNavigator().navigateTo(MainView.PATH);
  }

  @Override
  public void showView(View view) {
    root.setContent((Component) view);
  }
}
