package cfs.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Trip {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long      id;
  private LocalDate start_date;
  private LocalDate end_date;
  private Integer   start_milage;
  private Integer   end_milage;
  
  @OneToOne
  private Reservation reservation;
  
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
  
  public Trip() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getStart_date() {
    return start_date;
  }

  public void setStart_date(LocalDate start_date) {
    this.start_date = start_date;
  }

  public LocalDate getEnd_date() {
    return end_date;
  }

  public void setEnd_date(LocalDate end_date) {
    this.end_date = end_date;
  }

  public Integer getStart_milage() {
    return start_milage;
  }

  public void setStart_milage(Integer start_milage) {
    this.start_milage = start_milage;
  }

  public Integer getEnd_milage() {
    return end_milage;
  }

  public void setEnd_milage(Integer end_milage) {
    this.end_milage = end_milage;
  }

  public Reservation getReservation() {
    return reservation;
  }

  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
  }
  
  
}
