package com.navierre.healthsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description Format and Serialize Class for slots output
 * @author root
 *
 */
class SlotOutput {
	Long slotId;
	Integer duration;
	SlotOutput() {}
	public SlotOutput(Long slotId, Integer duration) {
		this.slotId = slotId;
		this.duration = duration;
    }
	public Long getSlotId() {
        return this.slotId;
    }
	public Integer getDuration() {
        return this.duration;
    }
}

/**
 * @description Manage Checking for appointment slot and book appointment
 * @author root
 *
 */
@RestController
public class AppointmentController {
	
	private final DoctorRepository doctorRepo;
	private final BookingRepository bookingRepo;
	HashMap<String, Integer> serviceTypes = new HashMap<String, Integer>();
	private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
	
	AppointmentController(DoctorRepository doctorRepo, BookingRepository bookingRepo) {
		this.doctorRepo = doctorRepo;
		this.bookingRepo = bookingRepo;
		this.serviceTypes.put("FIRST_VISIT", 45);
		this.serviceTypes.put("BODY_CHECK", 30);
		this.serviceTypes.put("COVID_TEST", 15);
	}
	
	/**
	 * @description List available slots given date and service type
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/slots")
	List<SlotOutput> all(HttpServletRequest request) throws ParseException {
		String serviceType = request.getParameter("serviceType");
		String date = request.getParameter("date");
		Integer duration = serviceTypes.get(serviceType);
		Date cdate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		//Find eligible doctors based on minimum booking time
		List<Doctor> doctors = doctorRepo.findByDurationLessThanEqual(duration);
		//Check if for each doctor has left available time on given date (between 9AM & 5PM)
		List<SlotOutput> slotList = new ArrayList<SlotOutput>();
		doctors.forEach((doctor) -> {
			//get maximum available start time on given date
			List<Booking> bookings = bookingRepo.findMaximumStartDateTime(cdate, doctor.getId());
			Integer numberOfSlots = 0;
			if (bookings.size() > 0) {
				Booking booking = bookings.get(0);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(cdate);
				LocalDateTime endTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 17, 00);
				//get the time difference before end of the day (5PM) for this doctor
				Integer diff = (int) ChronoUnit.MINUTES.between(booking.getAppointmentToDate(), endTime);
				if (diff > duration) {
					numberOfSlots = (int) Math.floor(diff/duration);
				}
			} else {
				numberOfSlots = (int) Math.floor(duration/doctor.getDuration());
			}
			//logger.info("{} {} {}", duration, doctor.getDuration(), duration%doctor.getDuration());
			for (int i = 0; i < numberOfSlots; i++) {
				slotList.add(new SlotOutput(doctor.getId(), doctor.getDuration()));
		    }
		});
		
		return slotList;
	}
	
	@PostMapping("/slot")
	Booking newBooking(HttpServletRequest request) throws ParseException {
		String slotId = request.getParameter("slotId");
		String serviceType = request.getParameter("serviceType");
		String date = request.getParameter("date");
		Integer duration = serviceTypes.get(serviceType);
		Date cdate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		
		List<Booking> bookings = bookingRepo.findMaximumStartDateTime(cdate, Long.valueOf(slotId));
		LocalDateTime startTime;
		if (bookings.size() > 0) {
			Booking booking = bookings.get(0);
			startTime = booking.getAppointmentToDate();
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(cdate);
			startTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 9, 00);
		}
		
		
		Booking booking = new Booking();
		booking.setDoctorId(Long.valueOf(slotId));
		booking.setAppointmentFromDate(startTime);
		booking.setAppointmentToDate(startTime.plusMinutes(duration));
		
		return bookingRepo.save(booking);
	}
	
	
	
}
