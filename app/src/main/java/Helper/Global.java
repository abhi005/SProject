package Helper;

import android.content.Context;
import android.os.Vibrator;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Global {
    static final ArrayList<String> imageFileTypes = new ArrayList<String>(Arrays.asList("jpg", "jpeg", "png", "svg", "gif", "bmp"));
    static final ArrayList<String> docFileTypes = new ArrayList<String>(Arrays.asList("txt", "doc", "docx", "pdf", "epub", "text", "htm", "html", "xhtml"));
    static final ArrayList<String> audioFileTypes = new ArrayList<String>(Arrays.asList("mp3", "wma", "wav", "mp2", "aac", "ac3", "au", "ogg", "flac", "imy", "m4a", "mid", "midi", "mka", "ota"));
    static final ArrayList<String> videoFileTypes = new ArrayList<String>(Arrays.asList("3gp", "mp4", "ts", "webm"));
    static final ArrayList<String> zipFileTypes = new ArrayList<>(Arrays.asList("zip", "7z", "bz2", "gz", "jar", "tar", "tar.bz2", "tar.gz", "tgz", "z", "rar"));

    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 100;

    static final List<String> TEMP_FILES = new ArrayList<String>();

    public static final int PICK_CONTACT = 101;

    public static final int SPLASH_TIME_OUT = 700;

    public static String pin = "";
    public static String encryptionKey = "";


    public static void vibrate(Context context, long time) {
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert vibe != null;
        vibe.vibrate(time);
    }

    public static String computeEncryptionKey(String pin, String str) {
        final String charSet = "US-ASCII";
        BitSet aBitSet = null;
        try {
            aBitSet = BitSet.valueOf(pin.getBytes(charSet));
            aBitSet.or(BitSet.valueOf(str.getBytes(charSet)));
        } catch (UnsupportedEncodingException e) {
            computeEncryptionKey(pin, str);
        }
        assert aBitSet != null;
        return new String(aBitSet.toByteArray(), Charset.forName(charSet));
    }
}
