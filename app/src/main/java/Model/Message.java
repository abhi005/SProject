package Model;

import java.io.Serializable;

public class Message implements Serializable {
    private int conversationID;
    private String messageText;
    private String time;
    private String date;
    private String sender;
    private String receiver;

    public Message(int conversationID, String messageText, String time, String date, String sender, String receiver) {
        this.conversationID = conversationID;
        this.messageText = messageText;
        this.time = time;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
