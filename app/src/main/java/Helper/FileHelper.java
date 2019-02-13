package Helper;

import java.io.File;
import java.text.DecimalFormat;

public class FileHelper {

    public static String getReadableFileSize(long size) {
        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTb = sizeGb * sizeKb;

        if(size < sizeMb) {
            return df.format(size / sizeKb) + " KB";
        } else if(size < sizeGb) {
            return df.format(size / sizeMb) + " MB";
        } else if(size < sizeTb) {
            return  df.format(size / sizeGb) + " GB";
        }
        return  "";
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
}
