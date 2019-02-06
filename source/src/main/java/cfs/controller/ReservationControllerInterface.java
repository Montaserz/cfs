package cfs.controller;

import cfs.data.entity.Employee;
import cfs.data.entity.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ReservationControllerInterface {

  public Set<Reservation> findReservations(Employee user);

  public Reservation saveReservation(Reservation reservation);

  public List<Reservation> findAllConfirmations(Employee user);

  public void submitReservation(Reservation reservation);

  public Set<LocalDate> getInvalidDates();

  public LocalDate getMaxEnd(LocalDate value);

  public void setConfirmation(Reservation res, boolean b);
}
