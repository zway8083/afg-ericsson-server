package server.model;

public class NightHoursForm {
	private Long subjectId;
	private String name;
	private String sleepStart;
	private String sleepEnd;
	private String emailON;

	public NightHoursForm() {
	}

	public NightHoursForm(Long subjectId, String name, String sleepStart, String sleepEnd, String emailON) {
		this.subjectId = subjectId;
		this.name = name;
		this.sleepStart = sleepStart;
		this.sleepEnd = sleepEnd;
		this.emailON = emailON;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getSleepStart() {
		return sleepStart;
	}

	public void setSleepStart(String sleepStart) {
		this.sleepStart = sleepStart;
	}

	public String getSleepEnd() {
		return sleepEnd;
	}

	public void setSleepEnd(String sleepEnd) {
		this.sleepEnd = sleepEnd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailStatus() { return emailON; }

	public void setEmailStatus(String emailON ) { this.emailON = emailON;}

	@Override
	public String toString() {
		return "NightHoursForm [" + (subjectId != null ? "subjectId=" + subjectId + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (sleepStart != null ? "sleepStart=" + sleepStart + ", " : "")
				+ (sleepEnd != null ? "sleepEnd=" + sleepEnd : "") + "]";
	}
}
