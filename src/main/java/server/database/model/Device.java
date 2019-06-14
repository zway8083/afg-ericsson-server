package server.database.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Device {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true)
	private int[] serial;
	@ManyToOne
	private User user;

	@Transient
	private String serialStr;
	@Transient
	private Long userId;
	@Transient
	private String gateway;


	public Device() {
	}

	public Device(int[] serial, User user) {
		this.serial = serial;
		this.user = user;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int[] getSerial() {
		return serial;
	}

	public void setSerial(int[] serial) {
		this.serial = serial;
	}

	public void setSerial(String serialStr) {
		String[] serialTab = serialStr.contains(",") ? serialStr.split(",") : serialStr.split("-");
		if (serialTab.length != 8)
			return;
		this.serial = new int[8];
		for (int i = 0; i < serialTab.length; i++) {
			this.serial[i] = Integer.parseInt(serialTab[i]);
		}
	}

	public String getSerialStr() {
		return serialStr;
	}

	public void setSerialStr(String serialStr) {
		this.serialStr = serialStr;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	@Override
	public String toString() {return "Capteur: NS =" + Arrays.toString(serial).replace(", ","-");
	}
	
	
}
