package server.database.model;

import java.io.Serializable;
import java.sql.Date;

public class ObservationPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -29904039211792915L;
	private Long subjectId;
	private Date date;
	private Integer timeChrono;

	public ObservationPK() {
	}

	public ObservationPK(User subject, Date date, Time time) {
		this.subjectId = subject.getId();
		this.date = date;
		this.timeChrono = time.getChrono();
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getTimeChrono() {
		return timeChrono;
	}

	public void setTimeChrono(Integer timeChrono) {
		this.timeChrono = timeChrono;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((subjectId == null) ? 0 : subjectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ObservationPK))
			return false;
		ObservationPK other = (ObservationPK) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (subjectId == null) {
			if (other.subjectId != null)
				return false;
		} else if (!subjectId.equals(other.subjectId))
			return false;
		return true;
	}
}
