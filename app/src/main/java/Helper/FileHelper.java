package Helper;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.jarvis.sproject.R;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import Model.AudioFile;
import Model.DocFile;
import Model.ImageFile;
import Model.VideoFile;
import Model.ZipFile;

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

    @SuppressLint("DefaultLocale")
    static String getReadableVideoDuration(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
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

    static String getFileExtension(String path) {
        String[] temp = path.split("/");
        String fileName = temp[temp.length - 1];
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    static void openFile(Context context, String path) {
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

    private static String getNewPath(String path, String fileName) {
        StringBuilder newPath = new StringBuilder();
        String[] tempArray = path.split("/");
        for (int i = 0; i < tempArray.length - 1; i++) {
            newPath.append(tempArray[i]).append("/");
        }
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            newPath.append(fileName, 0, fileName.lastIndexOf("."));

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
        } else if (Global.zipFileTypes.contains(ext.toLowerCase())) {
            return R.drawable.zip_file;
        } else {
            return R.drawable.default_file;
        }
    }

    static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    static void openEncryptedFile(Context context, String newPath, String originalPath) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);
        int lIndex = originalPath.lastIndexOf("/");
        String tempPath = originalPath.substring(0, lIndex + 1) + "temp_" + getFileName(originalPath) + "." + getFileExtension(originalPath);
        File file = new File(newPath);
        String temp = Global.encryptionKey;
        File tempFile = new File(tempPath);
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            openFile(context, tempFile.getPath());
            Global.TEMP_FILES.add(tempFile.getPath());
        } catch (Exception e) {
            Log.i("open_encrypted_file", "error : " + e.getMessage());
        }
    }

    public static void deleteTempFile() {
        if (Global.TEMP_FILES.size() > 0) {
            for (int i = 0; i < Global.TEMP_FILES.size(); i++) {
                try {
                    File f = new File(Global.TEMP_FILES.get(i));
                    f.delete();
                    Global.TEMP_FILES.remove(i);
                } catch (Exception e) {
                    Log.i("temp_file", e.getMessage());
                }
            }
        }
    }

    static void decryptAudioFile(Context context, AudioFile eFile) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(eFile.getNewPath());
        String temp = Global.encryptionKey;
        File tempFile = new File(eFile.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            file.delete();
            db.deleteAudio(eFile);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void decryptVideoFile(Context context, VideoFile eFile) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(eFile.getNewPath());
        String temp = Global.encryptionKey;
        File tempFile = new File(eFile.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            file.delete();
            db.deleteVideo(eFile);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void decryptDocFile(Context context, DocFile eFile) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(eFile.getNewPath());
        String temp = Global.encryptionKey;
        File tempFile = new File(eFile.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            file.delete();
            db.deleteDoc(eFile);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void decryptZipFile(Context context, ZipFile eFile) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(eFile.getNewPath());
        String temp = Global.encryptionKey;
        File tempFile = new File(eFile.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            file.delete();
            db.deleteZip(eFile);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void decryptImageFile(Context context, ImageFile eFile) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(eFile.getNewPath());
        String temp = Global.encryptionKey;
        File tempFile = new File(eFile.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(file);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            file.delete();
            db.deleteImage(eFile);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void encryptFile(Context context, String path) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(path);
        String temp = Global.encryptionKey;
        String newPath = getNewPath(path, file.getName());
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                byte[] thumbnail = baos.toByteArray();
                ImageFile f = new ImageFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()), thumbnail);
                db.addImage(f);
                baos.close();
            } else if (Global.docFileTypes.contains(ext.toLowerCase())) {
                DocFile f = new DocFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()));
                db.addDoc(f);
            } else if (Global.audioFileTypes.contains(ext.toLowerCase())) {
                AudioFile f = new AudioFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()));
                db.addAudio(f);
            } else if (Global.videoFileTypes.contains(ext.toLowerCase())) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                retriever.release();
                VideoFile f = new VideoFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()), time);
                db.addVideo(f);
            } else if (Global.zipFileTypes.contains((ext.toLowerCase()))) {
                ZipFile f = new ZipFile(path, newPath + ".serg", ext, dateModify, getReadableFileSize(file.length()));
                db.addZip(f);
            }
            db.close();
            file.delete();

        } catch (Exception e) {
            Log.i("file_trace", e.getMessage());
        }
    }
}
