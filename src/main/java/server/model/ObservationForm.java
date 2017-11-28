package server.model;

public class ObservationForm {
	private Long subjectId;
	private String subjectName;
	private String date;
	private Integer timeId;
	private String activity;
	private String behaviour;

	public ObservationForm() {
	}

	public ObservationForm(Long subjectId, String subjectName, String date) {
		this.subjectId = subjectId;
		this.subjectName = subjectName;
		this.date = date;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getTimeId() {
		return timeId;
	}

	public void setTimeId(Integer timeId) {
		this.timeId = timeId;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}

	@Override
	public String toString() {
		return "ObservationForm [" + (subjectId != null ? "subjectId=" + subjectId + ", " : "")
				+ (subjectName != null ? "subjectName=" + subjectName + ", " : "")
				+ (date != null ? "date=" + date + ", " : "") + (timeId != null ? "timeId=" + timeId + ", " : "")
				+ (activity != null ? "activity=" + activity + ", " : "")
				+ (behaviour != null ? "behaviour=" + behaviour : "") + "]";
	}
}
