package server.database.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "persistent_logins")
public class RememberMe {
	@Column(columnDefinition = "varchar(64) not null")
	private String username;
	@Id
	@Column(columnDefinition = "varchar(64)")
	private String series;
	@Column(columnDefinition = "varchar(64) not null")
	private String token;
	@Column(columnDefinition = "timestamp not null")
	private Timestamp last_used;
}
