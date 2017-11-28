package server.database.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

@Entity
public class Description {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@ManyToOne
	private User observator;
	private Date save;
	private String activity;
	private String behaviour;

	public Description() {
	}

	public Description(User observator, String activity, String behaviour) {
		this.observator=observator;
		this.activity = activity;
		this.behaviour = behaviour;
		this.save = new Date(new DateTime().getMillis());
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

}
