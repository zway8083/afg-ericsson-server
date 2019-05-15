package server.model;

public class UserForm {
	private String roleStr;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String sleepStart;
	private String sleepEnd;
	private Long subjectId;
	private String emailON;

	public UserForm() {
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getRoleStr() {
		return roleStr;
	}

	public void setRoleStr(String roleStr) {
		this.roleStr = roleStr;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSleepStart() {
		return sleepStart;
	}

	public void setSleepStart(String sleepStart) {
		this.sleepStart = sleepStart;
	}

	public String getSleepEnd() {return sleepEnd;}

	public void setSleepEnd(String sleepEnd) {this.sleepEnd = sleepEnd;}

	public String getEmailStatus() { return emailON; }

	public void setEmailStatus(String emailON ) { this.emailON = emailON;}
}
