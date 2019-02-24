package Model;


public class ImageFile {
    private int id;
    private String originalPath;
    private String newPath;
    private String originalExt;
    private String date;
    private String size;
    private byte[] thumbnail;

    public ImageFile() {
    }

    public ImageFile(String originalPath, String newPath, String originalExt, String date, String size, byte[] thumbnail) {
        this.originalPath = originalPath;
        this.newPath = newPath;
        this.originalExt = originalExt;
        this.date = date;
        this.size = size;
        this.thumbnail = thumbnail;
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

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}
