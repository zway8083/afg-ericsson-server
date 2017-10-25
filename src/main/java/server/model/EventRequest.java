
package server.model;

import java.util.Arrays;
import java.util.Date;

public class EventRequest {
	private int[] serial;
	private String type;
	private Date date;
	private Double dValue;
	private Boolean binValue;

	public EventRequest() {
	}

	public int[] getSerial() {
		return serial;
	}

	public void setSerial(int[] serial) {
		this.serial = serial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = new Date(date * 1000);
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

	@Override
	public String toString() {
		return "EventRequest [serial=" + Arrays.toString(serial) + ", type=" + type + ", date=" + date + ", value="
				+ (dValue != null ? dValue : binValue) + "]";
	}
}
