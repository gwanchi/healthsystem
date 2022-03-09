package com.navierre.healthsystem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description Data Access and Manipulation Layer for Doctor Modal
 * @author root
 *
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	public List<Doctor> findByDurationLessThanEqual(Integer duration);

}
