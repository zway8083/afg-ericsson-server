package server.task;


import javax.mail.MessagingException;

import org.slf4j.Logger;

import server.utils.HTMLGenerator;

public class NewBoxRegisteredRunnable extends EmailRunnable{
    private final Logger logger;
    private final String recipient;
    private final String userName;
    private final String subjectName;

    public NewBoxRegisteredRunnable(String emailId, String emailPassword, String emailHost, String recipient, String userName, String subjectName, Logger logger) {
        super(emailId, emailPassword, emailHost, true);
        this.recipient = recipient;
        this.userName = userName;
        this.subjectName = subjectName;
        this.logger = logger;
    }

    @Override
    public void run() {
        email.addRecipient(recipient);
        email.setSubject("Projet AFG Autisme - Ericsson");

        final String line1 =  "Vous venez de renseigner les informations d'un nouveau point d'un accès wifi pour le capteur associé au sujet: <strong>" + subjectName + "</strong>.";
        final String line2 = "Les données renseignées sont correctes et une connexion a été effectué.";

        String htmlBody = HTMLGenerator.value(line1, 0) + HTMLGenerator.value(line2, 0);
        email.concatBody(htmlBody);

        try {
            email.send();
            logger.info("New user email sent to " + recipient + " for  subject sensor" + subjectName);
        } catch (MessagingException e) {
            logger.error("Failed to send new user email to " + recipient + " for  subject sensor" + subjectName + ": " + e);
        }
    }
}
