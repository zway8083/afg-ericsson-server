package server.task;

import javax.mail.MessagingException;

import org.slf4j.Logger;

import server.utils.HTMLGenerator;

public class ForgotPasswordRunnable extends EmailRunnable {
	private final Logger logger;
	private final String recipient;
	private final String rawPassword;

	public ForgotPasswordRunnable(String id, String password, String host, String rawPassword, String recipient, Logger logger) {
		super(id, password, host, true);
		this.recipient = recipient;
		this.rawPassword = rawPassword;
		this.logger = logger;
	}

	@Override
	public void run() {
		email.addRecipient(recipient);
		email.setSubject("Projet AFG Autisme - Ericsson : nouveau mot de passe");
		
		String htmlBody = "";
		if (rawPassword != null) {
			htmlBody += HTMLGenerator.value("Identifiants :", 0)
					+ HTMLGenerator.strongAttributeValue("Utilisateur", recipient, 1)
					+ HTMLGenerator.strongAttributeValue("Mot de passe", rawPassword, 1);
		}
		email.concatBody(htmlBody);
		
		try {
			email.send();
			logger.info("New password sent for " + recipient);
		} catch (MessagingException e) {
			logger.error("Failed to send new password email to " + recipient + ": " + e);
		}
	}

}
