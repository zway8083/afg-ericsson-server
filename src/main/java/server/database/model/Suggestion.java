package server.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Suggestion {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private User user;
	@Column(nullable = false, length = 500)
	private String suggestion;

	public Suggestion() {
	}

	public Suggestion(User user) {
		this.user = user;
	}

	public Suggestion(User user, String suggestion) {
		this.user = user;
		this.suggestion = suggestion;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public Long getId() {
		return id;
	}

}
