package cfs.data.entity;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Department {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique=true, nullable=false)
  private String name;

  @OneToMany
  @JoinColumn(name = "employee_id")
  private Set<Employee> employee;

  @OneToOne
  private Employee manager;
  
  @Override
  public int hashCode() {
    return id.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(getClass() == obj.getClass())
      return hashCode() == obj.hashCode();
    return false;
  }

  public Department() {
  }

  public Department(String name) {
    this.name = name;
    this.employee = new TreeSet<>();
  }

  public Department(String name, Employee manager) {
    this.name = name;
    this.employee = new TreeSet<>();
    this.manager = manager;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Employee> getEmployees() {
    return employee;
  }

  public void setEmployees(Set<Employee> employees) {
    this.employee = employees;
  }

  public Employee getManager() {
    return manager;
  }

  public void setManager(Employee manger) {
    this.manager = manger;
  }
  
}
