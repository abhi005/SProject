package Model;

import android.support.annotation.NonNull;

public class FileManagerItem implements Comparable<FileManagerItem>{

    private String name;
    private String path;
    private String data;
    private String date;
    private String type; //values "dir", "file"
    private String ext = null;

    public FileManagerItem(String name, String path, String data, String date, String type, String ext) {
        this.name = name;
        this.path = path;
        this.data = data;
        this.date = date;
        this.type = type;
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public int compareTo(@NonNull FileManagerItem fileManagerItem) {
        if (this.name != null) {
            return this.name.toLowerCase().compareTo(fileManagerItem.getName().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
