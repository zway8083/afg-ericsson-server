package server.database.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "subject_id", "role_id" }) })
public class UserLink {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private User user;
	@ManyToOne
	private User subject;
	@ManyToOne
	private Role role;

	@Transient
	private long subjectId;
	@Transient
	private long userId;
	@Transient
	private String roleStr;
	@Transient
	private List<String> checkedRoles;

	public UserLink() {
	}
	
	public UserLink(User user, User subject, Role role) {
		this.user = user;
		this.subject = subject;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public User getSubject() {
		return subject;
	}

	public void setSubject(User subject) {
		this.subject = subject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getRoleStr() {
		return roleStr;
	}

	public void setRoleStr(String roleStr) {
		this.roleStr = roleStr;
	}

	public List<String> getCheckedRoles() {
		return checkedRoles;
	}

	public void setCheckedRoles(List<String> checkedRoles) {
		this.checkedRoles = checkedRoles;
	}

}
