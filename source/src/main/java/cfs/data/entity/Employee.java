package cfs.data.entity;

import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Employee {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="first_name", nullable=false)
  private String firstName;

  @Column(name="last_name", nullable=false)
  private String lastName;

  @Column(unique=true, nullable=false)
  private String email;
  
  @Column(nullable=false)
  private String password;
  
  @ManyToMany
  private Set<Role> role;
  
  @ManyToOne(optional=false)
  private Department department;
  
  @OneToOne
  private Department managing;
  
  @OneToMany(mappedBy="employee", cascade=CascadeType.ALL)
  private Set<Reservation> reservation;
  
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
  
  public Employee() {
  }

  public Employee(String firstName, String lastName, String email, Department department) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.department = department;
    
    this.password = "1234";
    this.role = new TreeSet<>();
    this.reservation = new TreeSet<>();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String first_name) {
    this.firstName = first_name;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String last_name) {
    this.lastName = last_name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return role;
  }

  public void setRoles(Set<Role> roles) {
    this.role = roles;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Department getManaging() {
    return managing;
  }

  public void setManaging(Department managing) {
    this.managing = managing;
  }

  public Set<Reservation> getReservations() {
    return reservation;
  }

  public void setReservations(Set<Reservation> reservations) {
    this.reservation = reservations;
  }
  
}
