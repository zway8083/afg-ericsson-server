package server.database.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
	@Transient
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;
	private String firstName;
	private String lastName;
	private Date birth;
	@Email
	private String email;
	@Column(nullable = false)
	private boolean subject = false;
	private Time sleepStart;
	private Time sleepEnd;

	@Transient
	private String birthStr;

	public User() {
	}

	public User(String firstName, String lastName, Date birth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birth = birth;
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
				logger.error("Cannot parse '" + birthStr + "' to birth date, format must be: yyyy/MM/dd");
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

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", birth=" + birth + "]";
	}
}
