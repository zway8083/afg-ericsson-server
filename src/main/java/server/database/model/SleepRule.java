package server.database.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SleepRule {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double value;
	private Date date;

	public SleepRule() {}
	
	public SleepRule(Double value, Date date) {
		this.value = value;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}
}
