package server.database.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;
	private String firstName;
	private String lastName;
	@ManyToMany
	private Set<Role> roles;
	@Email
	private String email;
	@Transient
	private String roleIdStr;
	private Date birth;
	@Transient
	private String birthStr;
	@Transient
	private List<String> checkedRoles;

	public User() {
	}

	public User(String firstName, String lastName, Date birth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birth = birth;
	}

	public void addRole(Role role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		roles.add(role);
	}

	public Long getId() {
		return id;
	}

	public String getIdStr() {
		return id.toString();
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoleIdStr() {
		return roleIdStr;
	}

	public void setRoleIdStr(String roleIdStr) {
		this.roleIdStr = roleIdStr;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public void setBirth(String birthStr) {
		if (birthStr == null || birthStr.isEmpty())
			this.birth = null;
		else {
			try {
				this.birth = new SimpleDateFormat("yyyy/MM/dd").parse(birthStr.replace('-', '/'));
			} catch (ParseException e) {
				System.err.println(e.getMessage());
				this.birth = null;
			}
		}
	}

	public String getBirthStr() {
		return birthStr;
	}

	public void setBirthStr(String birthStr) {
		this.birthStr = birthStr;
	}

	public List<String> getCheckedRoles() {
		return checkedRoles;
	}

	public void setCheckedRoles(List<String> checkedRoles) {
		this.checkedRoles = checkedRoles;
	}

	private String rolesToString() {
		Role[] rolesTab = roles.toArray(new Role[0]);
		String rolesStr = "[";
		for (int i = 0; i < rolesTab.length; i++) {
			if (i > 0)
				rolesStr += ", ";
			rolesStr += rolesTab[i].getName();
		}
		rolesStr += "]";
		return rolesStr;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", roles=" + rolesToString()
				+ ", birth=" + birth + "]";
	}
}
