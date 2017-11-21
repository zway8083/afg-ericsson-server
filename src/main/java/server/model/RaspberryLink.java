package server.model;

public class RaspberryLink {
	private String raspberryId;
	private Long userId;
	private boolean create;

	public String getRaspberryId() {
		return raspberryId;
	}

	public void setRaspberryId(String raspberryId) {
		this.raspberryId = raspberryId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean getCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

}
