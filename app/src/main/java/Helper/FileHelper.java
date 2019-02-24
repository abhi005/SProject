package Helper;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.MimeTypeFilter;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.jarvis.sproject.R;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import Model.DocFile;
import Model.ImageFile;

import static Helper.Global.thumbSize;

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

    public static String getFileName(String path) {
        String[] temp = path.split("/");
        String fileName = temp[temp.length - 1];
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(0, fileName.lastIndexOf("."));
        else return "";
    }

    public static String getDirectoryName(String path) {
        String[] temp = path.split("/");
        return temp[temp.length - 1];
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

    // method to get new path for encrypted files
    private static String getNewPath(String path, String fileName, String sdCardPath) {
        StringBuilder newPath = new StringBuilder();
        if(path.contains(sdCardPath)) {
            newPath = new StringBuilder(Environment.getExternalStorageDirectory().getPath() + "/encrypted_data/");
            File directory = new File(newPath.toString());
            if(!directory.exists()) {
                directory.mkdir();
            }
            if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                newPath.append(fileName.substring(0, fileName.lastIndexOf(".")));
        } else {
            String[] tempArray = path.split("/");
            for (int i = 0; i < tempArray.length - 1; i++) {
                newPath.append(tempArray[i]).append("/");
            }
            if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                newPath.append(fileName.substring(0, fileName.lastIndexOf(".")));
        }

        return newPath.toString();
    }

    public static int getFileIcon(String ext) {
        if (Global.docFileTypes.contains(ext.toLowerCase())) {
            return R.drawable.doc;
        } else if(Global.imageFileTypes.contains(ext.toLowerCase())) {
            return R.drawable.image;
        } else if(Global.audioFileTypes.contains(ext.toLowerCase())) {
            return R.drawable.audio;
        } else if(Global.videoFileTypes.contains(ext.toLowerCase())) {
            return R.drawable.video;
        } else if (ext.equals("serg")) {
            return R.drawable.encrypted_file;
        } else {
            return R.drawable.default_file;
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if(file.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public static void encryptFile(Context context, String path, String sdCardPath) {

        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(path);
        String temp = db.getUserKey();
        String newPath = getNewPath(path, file.getName(), sdCardPath);
        File newFile = new File(newPath + ".serg");
        try {
            byte[] bytes;
            byte[] key = temp.getBytes();
            bytes = FileUtils.readFileToByteArray(file);

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            newFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(newFile, false);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            ObjectOutputStream oos = new ObjectOutputStream(cos);
            oos.writeObject(bytes);
            oos.close();

            String ext = getFileExtension(file);
            Date lastModDate = new Date(file.lastModified());
            DateFormat formater = DateFormat.getDateInstance();
            String dateModify = formater.format(lastModDate);
            if(Global.imageFileTypes.contains(ext.toLowerCase())) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] thumbnail = baos.toByteArray();
                ImageFile f = new ImageFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()), thumbnail);
                db.addImage(f);
                baos.close();
            } else if(Global.docFileTypes.contains(ext.toLowerCase())) {
                DocFile f = new DocFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()));
                db.addDoc(f);
            }
            db.close();
            //file.delete();

        } catch (Exception e) {
            Log.i("file_trace", e.getMessage());
        }
    }
}
