package com.yourcompany.ems.models;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private Timestamp sentTime;
    private boolean isRead;
    private String direction; // NEW FIELD

    public Message(int id, String sender, String recipient, String subject, String body, Timestamp sentTime, boolean isRead, String direction) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sentTime = sentTime;
        this.isRead = isRead;
        this.direction = direction;
    }

    // Getters
    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Timestamp getSentTime() { return sentTime; }
    public boolean isRead() { return isRead; }
    public String getDirection() { return direction; } // NEW GETTER
}
