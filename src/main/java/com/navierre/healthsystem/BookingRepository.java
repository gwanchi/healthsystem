package com.navierre.healthsystem;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//import javax.persistence.Query;

/**
 * @description Data Access and Manipulation Layer for Booking Modal
 * @author root
 *
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	@Query(value="select id, doctor_id,appointment_from_date, appointment_to_date from bookings where :appointdate between appointment_from_date and appointment_to_date and doctor_id = :doctorid order by id desc limit 1", nativeQuery = true)
	public List<Booking> findMaximumStartDateTime(@Param("appointdate") LocalDateTime date, @Param("doctorid") Long doctorId);
	
	public List<Booking> findByAppointmentFromDate(LocalDateTime startTime);

}
