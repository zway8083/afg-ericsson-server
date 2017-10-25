package server.model;

import java.util.Arrays;

public class Serial {
	private int[] serial;

	public Serial() {
	}

	public Serial(String serialStr) throws Exception {
		String serialStrTab[] = serialStr.replace("[", "").replace("]", "").replace(" ", "").split(",");
		if (serialStrTab.length != 8)
			throw new Exception("Invalid serial number length for: " + serialStr);
		int serial[] = new int[8];
		for (int i = 0; i < 8; i++)
			try {
				serial[i] = Integer.parseInt(serialStrTab[i]);
			} catch (NumberFormatException e) {
				throw new Exception("Failed to parse serial string: '" + serialStr + "' to int[]");
			}
		this.serial = serial;
	}

	public Serial(int[] serial) {
		this.serial = serial;
	}

	public int[] getSerial() {
		return serial;
	}

	public void setSerial(int[] serial) {
		this.serial = serial;
	}

	@Override
	public String toString() {
		return Arrays.toString(serial);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(serial);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Serial))
			return false;
		Serial other = (Serial) obj;
		if (!Arrays.equals(serial, other.serial))
			return false;
		return true;
	}
}
