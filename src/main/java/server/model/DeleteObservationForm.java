package server.model;

public class DeleteObservationForm {
	private Long descriptionId;
	private Long subjectId;
	private String date;

	public DeleteObservationForm() {
	}

	public DeleteObservationForm(Long subjectId, String date) {
		this.subjectId = subjectId;
		this.date = date;
	}

	public Long getDescriptionId() {
		return descriptionId;
	}

	public void setDescriptionId(Long descriptionId) {
		this.descriptionId = descriptionId;
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

}
