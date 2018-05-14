
package server.model;

import java.util.Arrays;
import java.util.Date;

public class EventRequest2 {
	private long[] data;
	private int[] serial;
	private String type;
	

	public Date[] getData() {
		Date[] dates = new Date[data.length];
		for (int i = 0; i < data.length; i++) {
			dates[i]= new Date(data[i]*1000);
		}
		return dates;
	}

	public void setData(long[] data) {
		this.data = data;
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

	@Override
	public String toString() {
		return "EventRequest [serial=" + Arrays.toString(serial) + ", type=" + type + ", date=" + Arrays.toString(getData()) + "]";
	}
}
