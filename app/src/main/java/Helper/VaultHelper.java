package Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.text.DateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import Model.VaultFile;


public class VaultHelper {

    static void addVaultFile(Context context, String path) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File file = new File(path);
        String temp = Global.encryptionKey;
        String filename = FileHelper.getFileName(path);
        String fileExt = FileHelper.getFileExtension(file);
        Date lastModDate = new Date(file.lastModified());
        DateFormat formater = DateFormat.getDateInstance();
        String dateModify = formater.format(lastModDate);
        File newFile = new File(context.getFilesDir(), filename + ".serg");
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

            VaultFile f = new VaultFile(filename, file.getPath(), fileExt, dateModify, FileHelper.getReadableFileSize(file.length()));
            db.addVaultFile(f);
            db.increaseUserData(file.length());
            db.close();
            file.delete();
        } catch (Exception e) {
            Log.i("file_trace", e.getMessage());
        }
    }

    static void openEncryptedFile(Context context, String name, String originalPath) {
        File directory = context.getFilesDir();
        File file = new File(directory, name + ".serg");
        int lIndex = originalPath.lastIndexOf("/");
        String tempPath = originalPath.substring(0, lIndex + 1) + "temp_" + FileHelper.getFileName(originalPath) + "." + FileHelper.getFileExtension(originalPath);
        File tempFile = new File(tempPath);
        String temp = Global.encryptionKey;
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
            FileHelper.openFile(context, tempFile.getPath());
            Global.TEMP_FILES.add(tempFile.getPath());
        } catch (Exception e) {
            Log.i("open_encrypted_file", "error : " + e.getMessage());
        }
    }

    static void exportFile(Context context, VaultFile file) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);

        File directory = context.getFilesDir();
        File eFile = new File(directory, file.getName() + ".serg");
        String temp = Global.encryptionKey;
        File tempFile = new File(file.getOriginalPath());
        try {
            byte[] key = temp.getBytes();

            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RC4/ECB/NoPadding");
            Key sk = new SecretKeySpec(key, "RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            FileInputStream fis = new FileInputStream(eFile);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);
            byte[] decrypted = (byte[]) ois.readObject();
            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(decrypted);
            ois.close();
            cis.close();
            fos.close();
            db.decreaseUserData(eFile.length());
            context.deleteFile(file.getName());
            //eFile.delete();
            db.deleteVaultFile(file);
        } catch (Exception e) {
            Log.i("decrypt_file", "error : " + e.getMessage());
        } finally {
            db.close();
        }
    }

    static void deleteFile(Context context, String name) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);
        File directory = context.getFilesDir();
        File file = new File(directory, name);
        db.decreaseUserData(file.length());
        file.delete();
        db.close();
    }
}
