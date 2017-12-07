package server.model;

public class InitObservationForm {
	private Long subjectId;
	private String date;
	private String newDate;

	public InitObservationForm() {
	}
	
	public InitObservationForm(Long subjectId, String date) {
		super();
		this.subjectId = subjectId;
		this.date = date;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNewDate() {
		return newDate;
	}

	public void setNewDate(String newDate) {
		this.newDate = newDate;
	}
}
