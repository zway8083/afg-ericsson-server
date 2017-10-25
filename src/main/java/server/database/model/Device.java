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
	private String userIdStr;
	@Transient
	private String gateway;

	public String getUserIdStr() {
		return userIdStr;
	}

	public void setUserIdStr(String userIdStr) {
		this.userIdStr = userIdStr;
	}

	public Device() {
	}

	public Device(int[] serial, User user) {
		this.serial = serial;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public User getUserId() {
		return user;
	}

	public void setUserId(User userId) {
		this.user = userId;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	public String toString() {
		return "Device [id=" + id + ", serial=" + Arrays.toString(serial) + "]";
	}
}
