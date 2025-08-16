package com.yourcompany.ems.utils;

import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.models.Message;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import java.sql.*;
import java.util.*;

public class EmailReceiver {

    private static final String EMAIL = "zikkamalli007@gmail.com"; // your Gmail
    private static final String PASSWORD = "dxth hfhm qsup uyyj"; // app password

    public static List<Message> fetchUnreadEmails() {
        List<Message> messageList = new ArrayList<>();

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore();
            store.connect("imap.gmail.com", EMAIL, PASSWORD);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            javax.mail.Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
            try (Connection conn = DatabaseConnection.getConnection()) {

                for (javax.mail.Message msg : messages) {
                    Address[] froms = msg.getFrom();
                    String from = froms == null ? "Unknown" : ((InternetAddress) froms[0]).toString();
                    String subject = msg.getSubject();
                    java.util.Date sentDate = msg.getSentDate();
                    Timestamp sentTime = sentDate == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(sentDate.getTime());
                    String content = getTextFromMessage(msg);

                    // Prevent duplicates: check if sender + subject exist
                    String checkSql = "SELECT COUNT(*) FROM messages WHERE sender = ? AND subject = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setString(1, from);
                        checkStmt.setString(2, subject);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            rs.next();
                            int count = rs.getInt(1);

                            if (count == 0) {
                                String insertSQL = "INSERT INTO messages (sender, recipient, subject, body, sent_time, is_read, direction) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                                    stmt.setString(1, from);
                                    stmt.setString(2, EMAIL);
                                    stmt.setString(3, subject);
                                    stmt.setString(4, content);
                                    stmt.setTimestamp(5, sentTime);
                                    stmt.setBoolean(6, false);
                                    stmt.setString(7, "RECEIVED");
                                    stmt.executeUpdate();

                                    // Mark as read
                                    msg.setFlag(Flag.SEEN, true);
                                }
                            }
                        }
                    }

                    // Add to list for UI
                    Message m = new Message(0, from, EMAIL, subject, content, sentTime, false, "RECEIVED");
                    messageList.add(m);
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messageList;
    }

    private static String getTextFromMessage(javax.mail.Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    result.append(part.getContent());
                }
            }
            return result.toString();
        }
        return "";
    }
}






