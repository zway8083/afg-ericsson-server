package server.database.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class InputHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Raspberry raspberry;
	@Column(nullable = false)
	private String input;
	@Column(nullable = false)
	private Date inputSent;
	private String output;
	private Date outputReceived;
	@Column(nullable = false)
	private String token;

	public InputHistory() {
	}
	
	public InputHistory(Raspberry raspberry, String input, Date inputSent, String token) {
		this.raspberry = raspberry;
		this.input = input;
		this.inputSent = inputSent;
		this.token = token;
	}

	public Raspberry getRaspberry() {
		return raspberry;
	}

	public void setRaspberry(Raspberry raspberry) {
		this.raspberry = raspberry;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Long getId() {
		return id;
	}

	public Date getInputSent() {
		return inputSent;
	}

	public void setInputSent(Date inputSent) {
		this.inputSent = inputSent;
	}

	public Date getOutputReceived() {
		return outputReceived;
	}

	public void setOutputReceived(Date outputReceived) {
		this.outputReceived = outputReceived;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
