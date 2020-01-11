package technopolisspring.technopolis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import technopolisspring.technopolis.model.daos.UserDao;
import technopolisspring.technopolis.model.pojos.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    UserDao userDao;

    public void notifySubscribers(String subject, double discount) throws MessagingException {
        List<User> subscribers = userDao.getAllSubscribers();
        for (User subscriber : subscribers) {
            sendEmail(subscriber.getEmail(), subject, discount, subscriber.getLastName());
        }
    }

    private void sendEmail(String userEmail, String subject, double discount, String lastName) throws MessagingException {
        String ourEmail = "";
        String ourEmailPassword = "";
        String body = "Dear Mr./Mrs " + lastName +
                "\n\n We are happy to announce our newest promotion: " + subject +
                "\n With the amazing " + (discount * 100) + "% discount.\n\n" +
                "Sincerly yours,\n + Technopolis team";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.starttls", "true"); // try deleting this if it doesn't work before 587
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465"); //587 if it doesn't work
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ourEmail, ourEmailPassword);
                    }
                });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ourEmail));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(userEmail));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }

}
