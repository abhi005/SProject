package Model;

public class VaultFile {

    private int id;
    private String name;
    private String originalPath;
    private String originalExt;
    private String date;
    private String size;

    public VaultFile() {
    }

    public VaultFile(String name, String originalPath, String originalExt, String date, String size) {
        this.name = name;
        this.originalPath = originalPath;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
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
