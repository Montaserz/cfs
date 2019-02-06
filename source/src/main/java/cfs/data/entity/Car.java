package cfs.data.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Car {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique=true, nullable=false)
  private String plate;
  
  @Column(nullable=false)
  private String model;
  
  @Column(name="production_year", nullable=false)
  private LocalDate productionYear;
  
  @Column(name="purchase_date", nullable=false)
  private LocalDate purchaseDate;
  
  @Column(nullable=false)
  private Integer milage;
  
  @Column(nullable=false)
  private String state;
  
  @OneToMany
  @JoinColumn(name = "reservation_id")
  private List<Reservation> reservations;
  @OneToMany
  @JoinColumn(name = "maintenance_id")
  private List<Maintenance> maintenances;
  
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
  
  public Car() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPlate() {
    return plate;
  }

  public void setPlate(String plate) {
    this.plate = plate;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public LocalDate getProductionYear() {
    return productionYear;
  }

  public void setProductionYear(LocalDate productionYear) {
    this.productionYear = productionYear;
  }

  public LocalDate getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(LocalDate purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  public Integer getMilage() {
    return milage;
  }

  public void setMilage(Integer milage) {
    this.milage = milage;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public List<Reservation> getReservations() {
    return reservations;
  }

  public void setReservations(List<Reservation> reservations) {
    this.reservations = reservations;
  }

  public List<Maintenance> getMaintenances() {
    return maintenances;
  }

  public void setMaintenances(List<Maintenance> maintenances) {
    this.maintenances = maintenances;
  }
  
  

}
