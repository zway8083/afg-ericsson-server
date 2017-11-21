package server.database.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import server.database.repository.RaspberryRepository;
import server.utils.RandomStringGenerator;

@Entity
public class Raspberry {
	private static final int idLength = 10;

	@Id
	@Column(length = idLength)
	private String id;

	@OneToMany
	@Column(nullable = false)
	private Set<User> users;

	private String input;
	@Column(nullable = false)
	private boolean connected;

	public Raspberry() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean addUser(User user) {
		if (users == null) {
			users = new HashSet<>();
		}
		return users.add(user);
	}

	public static String randomId(RaspberryRepository repository) {
		String id = RandomStringGenerator.randomString(idLength);
		if (repository.exists(id))
			return randomId(repository);
		return id;
	}
}
