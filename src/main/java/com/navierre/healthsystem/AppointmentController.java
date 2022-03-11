package com.navierre.healthsystem;

import java.util.List;
import java.util.Map;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description Format and Serialize Class for slots output
 * @author root
 *
 */
class SlotOutput {
	String slotId;
	String name;
	LocalDateTime startTime;
	LocalDateTime endTime;
	SlotOutput() {}
	public SlotOutput(String slotId, String name, LocalDateTime startTime, LocalDateTime endTime) {
		this.slotId = slotId;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
    }
	public String getSlotId() {
        return this.slotId;
    }
	public String getName() {
		return this.name;
	}
	public LocalDateTime getStartTime() {
        return this.startTime;
    }
	public LocalDateTime getEndTime() {
		return this.endTime;
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
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
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
	List<SlotOutput> all(@RequestBody Map<String, String> payload) throws ParseException {
		String serviceType = payload.get("serviceType");
		String date = payload.get("date");
		Integer duration = serviceTypes.get(serviceType);
		Date cdate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cdate);
		LocalDateTime dayStartTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 9, 00);
		LocalDateTime dayEndTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 17, 00);
		//Find eligible doctors based on minimum booking time
		List<Doctor> doctors = doctorRepo.findByDurationLessThanEqual(duration);
		//Check if for each doctor has left available time on given date (between 9AM & 5PM)
		List<SlotOutput> slotList = new ArrayList<SlotOutput>();
		doctors.forEach((doctor) -> {
			//get maximum available start time on given date
			List<Booking> bookings = new ArrayList<Booking>();
			LocalDateTime ldate = dayStartTime;
			while (ldate.isBefore(dayEndTime)) {
				bookings = bookingRepo.findMaximumStartDateTime(ldate.plusMinutes(1), doctor.getId());
				if (bookings.size() > 0) {
					// Booking booking = bookings.get(0);
					ldate = ldate.plusMinutes(doctor.getDuration());
					continue;
				}
				slotList.add(new SlotOutput(ldate.format(formatter)+doctor.getId(), doctor.getName(), ldate, ldate.plusMinutes(duration)));
				ldate = ldate.plusMinutes(duration);
			}
		});
		
		return slotList;
	}
	
	@PostMapping("/slot")
	Booking newBooking(@RequestBody Map<String, String> payload) throws ParseException {
		String slotId = payload.get("slotId");
		String serviceType = payload.get("serviceType");
		Integer duration = serviceTypes.get(serviceType);
		
		String date = slotId.substring(0, 12);
		
		LocalDateTime startTime = LocalDateTime.parse(date, formatter);		
		
		Booking booking = new Booking();
		booking.setDoctorId(Long.valueOf(slotId.substring(12)));
		booking.setAppointmentFromDate(startTime);
		booking.setAppointmentToDate(startTime.plusMinutes(duration));
		
		return bookingRepo.save(booking);
		

	}
	
	
	
}
