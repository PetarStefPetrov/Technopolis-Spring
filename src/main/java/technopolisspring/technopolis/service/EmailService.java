package technopolisspring.technopolis.service;

import lombok.SneakyThrows;
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

    public void notifySubscribers(String subject, double discount) {
        List<User> subscribers = userDao.getAllSubscribers();
         new Thread(new Runnable() {
             @SneakyThrows
             @Override
             public void run() {
                 for (User subscriber : subscribers) {
                     sendEmail(subscriber.getEmail(), subject, discount, subscriber.getLastName());
                 }
             }
         }).start();
    }

    @SneakyThrows
    private void sendEmail(String userEmail, String subject, double discount, String lastName) {
        String ourEmail = "technopolisfinalproject@gmail.com";
        String ourEmailPassword = "technopolisfinalproject1!";
        String body = "Dear Mr./Mrs " + lastName +
                "\n\n We are happy to announce our newest promotion: " + subject +
                "\n With the amazing " + (discount * 100) + "% discount.\n\n" +
                "Sincerly yours,\nTechnopolis team";
        Properties props = new Properties();
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
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
