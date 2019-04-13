package Model;

public class LocalCall {

    private int id;
    private int callId;
    private String number;
    private int type; //1 : outgoing, 2 : incoming, 3 : missed
    private String date;
    private String duration;

    public LocalCall() {
    }

    public LocalCall(int callId, String number, int type, String date, String duration) {
        this.callId = callId;
        this.number = number;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
