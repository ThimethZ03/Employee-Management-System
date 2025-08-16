package com.yourcompany.ems.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

public class EmailSender {
    private static final String EMAIL = "zikkamalli007@gmail.com";
    private static final String APP_PASSWORD = "dxth hfhm qsup uyyj";

    public static void sendEmail(Connection conn, String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            // Log to database
            saveToDatabase(conn, EMAIL, to, subject, body);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static void saveToDatabase(Connection conn, String sender, String recipient, String subject, String body) {
        String sql = "INSERT INTO messages (direction, sender, recipient, subject, body, status, is_read, sent_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "SENT");
            stmt.setString(2, sender);
            stmt.setString(3, recipient);
            stmt.setString(4, subject);
            stmt.setString(5, body);
            stmt.setString(6, "SENT");
            stmt.setBoolean(7, true); // Sent messages are already "read"
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

