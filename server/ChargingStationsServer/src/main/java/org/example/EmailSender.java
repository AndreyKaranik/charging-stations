package org.example;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EmailSender {

    public static boolean sendEmail(String to, String token) throws IOException {
        String path = "/root/chargingstations/email.txt";
        String content = new String(Files.readAllBytes(Path.of(path)));
        String[] lines = content.split("\n");
        String username = lines[0].strip();
        String password = lines[1].strip();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Registration Confirmation");
            message.setText("Please confirm your registration by clicking the following link: " +
                    "http://194.67.88.154:8000/confirm?token=" + token);

            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

//String path = "C:\\Users\\Golum\\Desktop\\email.txt";