package server.database.model;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EventStat {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private Date date;
	@ManyToOne
	private Device device;
	private Integer mvts;
	private Time duration;
	private Integer grade;
	private java.util.Date startNight;
	private java.util.Date endNight;

	public EventStat() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(long millis) {
		this.date = new Date(millis);
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Long getId() {
		return id;
	}

	public Integer getMvts() {
		return mvts;
	}

	public void setMvts(Integer mvts) {
		this.mvts = mvts;
	}

	public Time getDuration() {
		return duration;
	}

	public void setDuration(Time duration) {
		this.duration = duration;
	}

	public void setDuration(long millis) {
		this.duration = new Time(millis);
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		if (grade < 0)
			this.grade = 0;
		else if (grade > 100)
			this.grade = 100;
		else
			this.grade = grade;
	}

	public java.util.Date getStartNight() {
		return startNight;
	}

	public void setStartNight(java.util.Date startNight) {
		this.startNight = startNight;
	}

	public java.util.Date getEndNight() {
		return endNight;
	}

	public void setEndNight(java.util.Date endNight) {
		this.endNight = endNight;
	}

}
