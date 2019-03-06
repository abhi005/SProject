package Model;

public class ZipFile {

    private int id;
    private String originalPath;
    private String newPath;
    private String originalExt;
    private String date;
    private String size;

    public ZipFile() {}

    public ZipFile(String originalPath, String newPath, String originalExt, String date, String size) {
        this.originalPath = originalPath;
        this.newPath = newPath;
        this.originalExt = originalExt;
        this.date = date;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOriginalExt() {
        return originalExt;
    }

    public void setOriginalExt(String originalExt) {
        this.originalExt = originalExt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
