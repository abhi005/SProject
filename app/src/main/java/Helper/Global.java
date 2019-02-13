package Helper;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class Global {
    public static final ArrayList<String> imageFileTypes = new ArrayList<String>(Arrays.asList("jpg", "jpeg", "png", "svg", "gif", "bmp"));
    public static final ArrayList<String> docFileTypes = new ArrayList<String>(Arrays.asList("txt", "doc", "docx"));
    public static final ArrayList<String> audioFileTypes = new ArrayList<String>(Arrays.asList("mp3", "wma", "wav", "mp2", "aac", "ac3", "au", "ogg","flac"));
    public static final ArrayList<String> videoFileTypes = new ArrayList<String>(Arrays.asList("3gp", "mp4", "ts", "webm"));

    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 100;
}
