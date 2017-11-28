package server.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Time {
	@Id
	private Integer chrono;
	@Column(nullable = false, unique = true)
	private String name;

	public Time() {
	}

	public Time(Integer chrono, String name) {
		this.chrono = chrono;
		this.name = name;
	}

	public Integer getChrono() {
		return chrono;
	}

	public void setChrono(Integer chrono) {
		this.chrono = chrono;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
