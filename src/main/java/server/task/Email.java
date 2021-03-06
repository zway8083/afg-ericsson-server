package server.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.joda.time.DateTime;

public class Email {
	private final String id;
	private final String password;
	private final String host;
	private String subject;
	private boolean bodyHTML;
	private String body;
	private Multipart multipart;
	private List<String> recipients;

	public Email(String id, String password, String host) {
		this(id, password, host, false);
	}

	public Email(String id, String password, String host, boolean bodyHTML) {
		this.id = id;
		this.password = password;
		this.host = host;
		this.multipart = new MimeMultipart();
		this.subject = null;
		this.body = "";
		this.recipients = new ArrayList<>();
		this.bodyHTML = bodyHTML;
	}

	public void addAttachments(ArrayList<String> files) throws MessagingException {
		for (String file : files)
			addAttachement(file);
	}

	public void addAttachement(String file) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(new File(file).getName());
		multipart.addBodyPart(messageBodyPart);
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void concatBody(String body) {
		this.body += body + "\r\n";
	}

	public void addRecipient(String emailAdress) {
		recipients.add(emailAdress);
	}

	public void send() throws AddressException, MessagingException {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getDefaultInstance(properties, null);

		MimeMessage message = new MimeMessage(session);

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(id));
		for (String recipient : recipients)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

		if (subject == null)
			message.setSubject("Report " + new DateTime().toString("dd/MM/yyyy"));
		else
			message.setSubject(subject);

		BodyPart bodyPart = new MimeBodyPart();
		if (bodyHTML) {
			bodyPart.setContent(body, "text/html; charset=utf-8");
		} else {
			bodyPart.setText(body);
		}
		multipart.addBodyPart(bodyPart);

		message.setContent(multipart);

		Transport transport = session.getTransport("smtp");
		transport.connect(host, id, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}
