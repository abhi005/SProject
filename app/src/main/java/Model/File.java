package Model;

public class File {
    private String name;
    private String type;
    private String ext = null;
    private String location;
    private String lastModifiedDate;
    private String creationDate;
    private double size;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public double getSize() {
        return size;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public File(String name, String type, String lastModifiedDate, double size) {
        this.name = name;
        this.type = type;
        this.lastModifiedDate = lastModifiedDate;
        this.size = size;
    }
}
