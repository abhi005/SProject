package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Conversation {

    private int conversationID;
    private String address;
    private String lastMessage;
    private String latestDate;
    private int read;

    public Conversation() {}

    public Conversation( int conversationID, String address, String lastMessage, String latestDate, int read) {
        this.conversationID = conversationID;
        this.address = address;
        this.lastMessage = lastMessage;
        this.latestDate = latestDate;
        this.read = read;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }

    public int getConversationID() {
        return conversationID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(String latestDate) {
        this.latestDate = latestDate;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String toString() {
        return "thread: " + conversationID + " message: " + lastMessage + " seen: " + read + " number: " + address;
    }
}
