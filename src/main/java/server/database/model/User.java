package server.database.model;

import java.sql.Time;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class User {
	@Transient
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String firstName;
	private String lastName;
	@Column(unique = true)
	private String email;
	private String password;
	@Column(nullable = false)
	private boolean subject = false;
	private Time sleepStart;
	private Time sleepEnd;

	@Column(nullable = false)
	private boolean enabled;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Collection<Role> roles;

	public User() {
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

	public boolean isSubject() {
		return subject;
	}

	public void setSubject(boolean subject) {
		this.subject = subject;
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

	public String getName() {
		return firstName + " " + lastName;
	}

	public Time getSleepStart() {
		return sleepStart;
	}

	public void setSleepStart(String timeStr) {
		if (timeStr == null || timeStr.isEmpty())
			sleepStart = null;
		else {
			sleepStart = new Time(0);
			setSleepHour(timeStr, sleepStart);
		}
	}

	public Time getSleepEnd() {
		return sleepEnd;
	}

	public void setSleepEnd(String timeStr) {
		if (timeStr == null || timeStr.isEmpty())
			sleepEnd = null;
		else {
			sleepEnd = new Time(0);
			setSleepHour(timeStr, sleepEnd);
		}
	}

	private void setSleepHour(String timeStr, Time sleep) {
		if (timeStr != null && !timeStr.isEmpty()) {
			String[] times = timeStr.split(":");
			// GMT conversion
			DateTime time = new DateTime(0).withHourOfDay(Integer.parseInt(times[0]))
					.withMinuteOfHour(Integer.parseInt(times[1]));
			sleep.setTime(time.getMillis());
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
