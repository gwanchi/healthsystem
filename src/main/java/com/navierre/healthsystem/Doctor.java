package com.navierre.healthsystem;

import java.util.Objects;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * @description Doctor Modal
 * @author root
 *
 */
@Entity
public class Doctor {
	
	private @Id @GeneratedValue Long id;
	private String name;
	private Integer duration;
	
	Doctor() {}
	
	Doctor(String name, Integer duration) {
		this.name = name;
		this.duration = duration;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer getDuration() {
		return this.duration;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	@Override
	  public String toString() {
	    return "Doctor{" + "id=" + this.id + ", name='" + this.name + '\'' + ", duration='" + this.duration + '\'' + '}';
	  }

}
