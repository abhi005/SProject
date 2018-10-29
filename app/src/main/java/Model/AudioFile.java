package Model;

public class AudioFile {

    private int id;
    private String name;
    private double size;
    private String lastModifiedDate;
    private String lastModifiedTime;
    private int image;

    public AudioFile(int id, String name, double size, String lastModifiedDate, String lastModifiedTime, int image) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedTime = lastModifiedTime;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
