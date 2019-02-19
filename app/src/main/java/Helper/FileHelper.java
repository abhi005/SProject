package Helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v4.content.MimeTypeFilter;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.jarvis.sproject.R;

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

    public static String getFileExtension(String path) {
        String[] temp = path.split("/");
        String fileName = temp[temp.length - 1];
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static void openFile(Context context, String path) {
        Uri uriForFile;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", new File(path));
        } else {
            uriForFile = Uri.fromFile(new File(path));
        }
        MimeTypeMap fileMine = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = fileMine.getMimeTypeFromExtension(getFileExtension(path));
        newIntent.setDataAndType(uriForFile, mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "file cannot be opened", Toast.LENGTH_LONG).show();
        }
    }

    public static void encryptFile(String path) {

    }
}
