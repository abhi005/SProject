package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Conversation implements Serializable {
    private final int conversationID;
    private String personName;
    private String personContact;
    private String lastMessage;
    private String latestDate;
    private String latestTime;
    private ArrayList<Message> messages;
    private int messageCount;

    public Conversation( int conversationID, String personName, String personContact, ArrayList<Message> messages) {
        this.conversationID = conversationID;
        this.personName = personName;
        this.personContact = personContact;
        this.messages = messages;
        this.lastMessage = messages.get(messages.size() - 1).getMessageText();
        this.latestDate = messages.get(messages.size() - 1).getDate();
        this.latestTime = messages.get(messages.size() - 1).getTime();
        this.messageCount = messages.size();
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonContact() {
        return personContact;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getConversationID() {
        return conversationID;
    }

    public String getLatestTime() {
        return latestTime;
    }
}
