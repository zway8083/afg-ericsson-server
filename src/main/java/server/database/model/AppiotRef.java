package server.database.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import server.model.Sensor;

@Entity
public class AppiotRef {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;
	@OneToOne
	@JsonIgnore
	private Device device;
	private String gateway;
	private String motion;
	private String temperature;
	private String luminescence;
	private String battery;
	private String tamper;

	public AppiotRef() {
	}

	public AppiotRef(Device device, String gateway) {
		this.device = device;
		this.gateway = gateway.replace(" ", "");
	}

	public AppiotRef(String motion, String temperature, String luminescence, String battery, String tamper) {
		this.motion = motion;
		this.temperature = temperature;
		this.luminescence = luminescence;
		this.battery = battery;
		this.tamper = tamper;
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

	public String getMotion() {
		return motion;
	}

	public void setMotion(String motion) {
		this.motion = motion;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getLuminescence() {
		return luminescence;
	}

	public void setLuminescence(String luminescence) {
		this.luminescence = luminescence;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public String getTamper() {
		return tamper;
	}

	public void setTamper(String tamper) {
		this.tamper = tamper;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public void setKey(Sensor[] sensors) {
		for (Sensor sensor : sensors) {
			switch (sensor.getName()) {
			case "Battery":
				this.setBattery(sensor.getId());
				break;
			case "Tamper":
				this.setTamper(sensor.getId());
				break;
			case "Motion sensor":
				this.setMotion(sensor.getId());
				break;
			case "Temperature":
				this.setTemperature(sensor.getId());
				break;
			case "Light":
				this.setLuminescence(sensor.getId());
				break;
			default:
				break;
			}
		}
	}
}
