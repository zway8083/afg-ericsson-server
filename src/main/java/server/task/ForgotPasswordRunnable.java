package server.task;

import javax.mail.MessagingException;

import server.utils.HTMLGenerator;

public class ForgotPasswordRunnable extends EmailRunnable {
	private final String recipient;
	private final String rawPassword;

	public ForgotPasswordRunnable(String id, String password, String host, String rawPassword, String recipient) {
		super(id, password, host, true);
		this.recipient = recipient;
		this.rawPassword = rawPassword;
	}

	@Override
	public void run() {
		email.addRecipient(recipient);
		email.setSubject("Projet AFG Autisme - Ericsson : Verification Email");
		
		String htmlBody = "";
		if (rawPassword != null) {
			htmlBody += HTMLGenerator.value("Identifiants :", 0)
					+ HTMLGenerator.strongAttributeValue2("Utilisateur", recipient, 1)
					+ HTMLGenerator.strongAttributeValue2("Code", rawPassword, 1);
		}
		email.concatBody(htmlBody);
		
		try {
			email.send();
		} catch (MessagingException e) {
			System.err.println("Forgot password thread error: " + e.getMessage());
		}
	}

}
