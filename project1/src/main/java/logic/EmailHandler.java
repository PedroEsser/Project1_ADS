package logic;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import java.util.Properties;

public class EmailHandler {

	public static void sendGmail(String destination, String title, String text) {
		final String username = "adsdummyuser22@gmail.com";
		final String password = "qgYAv^)AqPrMXw4B";
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); // TLS

		Session session = Session.getInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
			message.setSubject(title);
			message.setText("Dear Curator,\n\n" + text);
			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
