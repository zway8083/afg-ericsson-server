package server.database.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.joda.time.DateTime;

@IdClass(ObservationPK.class)
@Entity
public class Observation {

	@Id
	@Column(name = "subject_id")
	private Long subjectId;
	@Id
	private Date date;
	@Id
	@Column(name = "time_chrono")
	private Integer timeChrono;

	@ManyToOne
	@JoinColumn(name = "subject_id", insertable = false, updatable = false)
	private User subject;
	@ManyToOne(optional = false)
	@JoinColumn(name = "time_chrono", insertable = false, updatable = false)
	private Time time;

	@OneToMany
	private List<Description> descriptions;	

	public Observation() {
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public User getSubject() {
		return subject;
	}

	public void setSubject(User subject) {
		this.subject = subject;
		this.subjectId = subject.getId();
	}

	public List<Description> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(Long date) {
		DateTime dateTime = new DateTime(date);
		this.date = new Date(dateTime.withMillisOfDay(0).getMillis());
	}

	public Integer getTimeChrono() {
		return timeChrono;
	}

	public void setTimeChrono(Integer timeChrono) {
		this.timeChrono = timeChrono;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
		timeChrono = time.getChrono();
	}
	
	public void addDescription(Description description) {
		if (description == null)
			return;
		if (descriptions == null)
			descriptions = new ArrayList<>();
		descriptions.add(description);
	}
}
