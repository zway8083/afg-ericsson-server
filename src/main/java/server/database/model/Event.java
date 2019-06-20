package server.database.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@NotNull
	private Device device;
	@ManyToOne

	private User user;
	@NotNull
	private Date date;
	@ManyToOne
	@NotNull
	private SensorType type;
	private Double dValue;
	private Boolean binValue;
	private Double Blevel;

	public Event() {
	}

	public Long getId() {
		return id;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SensorType getType() {
		return type;
	}

	public void setType(SensorType type) {
		this.type = type;
	}

	public Double getdValue() {
		return dValue;
	}

	public void setdValue(Double dValue) {
		this.dValue = dValue;
	}

	public Boolean getBinValue() {
		return binValue;
	}

	public void setBinValue(Boolean binValue) {
		this.binValue = binValue;
	}


	public User getUser() { return user; }

	public void setUser(User user) { this.user = user; }


	//public Double getBlevel() { return Blevel;}

	//public void setBlevel(Double blevel) { Blevel = blevel;}

	@Override
	public String toString() {
		return "Event [id=" + id + ", device=" + device + ", date=" + date.toString() + ", type=" + type.getName() + ", dValue=" + dValue
				+ ", binValue=" + binValue + "]";
	}
	
	
}
