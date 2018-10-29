package Model;

public class Call {
    private String name;
    private String contact;
    private String location;
    private String date;
    private String time;
    private String type;
    private String duration;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Call(String name, String contact, String date, String time, String type, String duration, String location) {

        this.name = name;
        this.contact = contact;
        this.date = date;
        this.time = time;
        this.type = type;
        this.duration = duration;
        this.location = location;
    }
}
