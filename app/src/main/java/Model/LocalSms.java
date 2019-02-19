package Model;

public class LocalSms {

    private int threadId;
    private int type;
    private int read;
    private String date;
    private String sentDate;
    private String body;
    private String address;

    public LocalSms() {

    }

    public LocalSms(int threadId, int type, int read, String date, String sentDate, String body, String address) {
        this.threadId = threadId;
        this.type = type;
        this.read = read;
        this.date = date;
        this.sentDate = sentDate;
        this.body = body;
        this.address = address;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
