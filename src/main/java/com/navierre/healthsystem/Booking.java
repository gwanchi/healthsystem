package com.navierre.healthsystem;

import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * @description Booking Modal
 * @author root
 *
 */
@Entity
@Table(name = "bookings")
public class Booking {
	
	@Column(name = "id")
	private @Id @GeneratedValue Long id;
	@Column(name = "doctor_id")
	private Long doctorId;
	@Column(name = "appointment_from_date")
	private LocalDateTime appointmentFromDate;
	@Column(name = "appointment_to_date")
	private LocalDateTime appointmentToDate;
	
	Booking() {}
	
	Booking(Long doctorId, LocalDateTime appointmentFromDate, LocalDateTime appointmentToDate) {
		this.doctorId = doctorId;
		this.appointmentFromDate = appointmentFromDate;
		this.appointmentToDate = appointmentToDate;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public Long getDoctorId() {
		return this.doctorId;
	}
	
	public LocalDateTime getAppointmentFromDate() {
		return this.appointmentFromDate;
	}
	
	public LocalDateTime getAppointmentToDate() {
		return this.appointmentToDate;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	
	public void setAppointmentFromDate(LocalDateTime appointmentFromDate) {
		this.appointmentFromDate = appointmentFromDate;
	}
	
	public void setAppointmentToDate(LocalDateTime appointmentToDate) {
		this.appointmentToDate = appointmentToDate;
	}

}
