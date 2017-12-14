package server.database.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Description {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@ManyToOne
	private User observator;
	private Date save;
	@Column(length = 50)
	private String activity;
	@Column(length = 250)
	private String behaviour;
	private Integer grade;

	public Description() {
	}

	public Description(User observator, String activity, String behaviour, Integer grade) {
		this.observator = observator;
		this.activity = activity;
		this.behaviour = behaviour;
		this.save = new Date();
		this.grade = grade;
	}

	public User getObservator() {
		return observator;
	}

	public void setObservator(User observator) {
		this.observator = observator;
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

	public Long getId() {
		return Id;
	}

	public Date getSave() {
		return save;
	}

	public void setSave(Date save) {
		this.save = save;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

}
