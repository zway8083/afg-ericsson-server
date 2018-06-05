package server.model;

public class DeleteAccompanistForm {
	private Long accompanistId;
	private Long subjectId;
	private String date;

	public DeleteAccompanistForm() {
	}

	public DeleteAccompanistForm(Long subjectId, String date) {
		this.subjectId = subjectId;
		this.date = date;
	}

	public Long getAccompanistId() {
		return accompanistId;
	}

	public void setAccompanistId(Long accompanistId) {
		this.accompanistId = accompanistId;
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
