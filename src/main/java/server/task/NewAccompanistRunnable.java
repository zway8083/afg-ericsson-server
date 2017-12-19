package server.task;

import javax.mail.MessagingException;

import org.slf4j.Logger;

import server.utils.HTMLGenerator;

public class NewAccompanistRunnable extends EmailRunnable {
	private final Logger logger;
	private final String recipient;
	private final String rawPassword;
	private final String userName;
	private final String subjectName;

	public NewAccompanistRunnable(String emailId, String emailPassword, String emailHost, String recipient,
			String rawPassword, String userName, String subjectName, Logger logger) {
		super(emailId, emailPassword, emailHost, true);
		this.recipient = recipient;
		this.rawPassword = rawPassword;
		this.userName = userName;
		this.subjectName = subjectName;
		this.logger = logger;
	}

	@Override
	public void run() {
		email.addRecipient(recipient);
		email.setSubject("Projet AFG Autisme - Ericsson");

		final String line1 = (userName != null ? userName : "Un administrateur") + " vous a nommé accompagnateur de : <strong>" + subjectName + "</strong>.";
		final String line2 = "Rendez-vous sur <a href=\"http://35.187.36.227/\">http://35.187.36.227/</a>.";

		String htmlBody = HTMLGenerator.value(line1, 0) + HTMLGenerator.value(line2, 0);
		if (rawPassword != null) {
			htmlBody += HTMLGenerator.value("Identifiants :", 0)
					+ HTMLGenerator.strongAttributeValue("Utilisateur", recipient, 1)
					+ HTMLGenerator.strongAttributeValue("Mot de passe", rawPassword, 1);
		}
		email.concatBody(htmlBody);		
		try {
			email.send();
			logger.info("New user email sent to " + recipient + " added by " + (userName != null ? userName : "admin"));
		} catch (MessagingException e) {
			logger.error("Failed to send new user email to " + recipient + " added by " + (userName != null ? userName : "admin") + ": " + e);
		}
	}
}
