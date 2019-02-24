package Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import Model.Conversation;
import Model.DocFile;
import Model.ImageFile;
import Model.LocalSms;

public class SqliteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "seguro";

    // sms table attributes
    private static final String TABLE_SMS = "smses";
    private static final String SMS_KEY_THREAD_ID = "threadId";
    private static final String SMS_KEY_TYPE = "type";
    private static final String SMS_KEY_READ = "read";
    private static final String SMS_KEY_DATE = "date";
    private static final String SMS_KEY_SENT_DATE = "sentDate";
    private static final String SMS_KEY_BODY = "body";
    private static final String SMS_KEY_ADDRESS = "address";
    private static final String[] SMS_COLUMNS = {SMS_KEY_THREAD_ID, SMS_KEY_TYPE, SMS_KEY_READ, SMS_KEY_DATE, SMS_KEY_SENT_DATE, SMS_KEY_BODY, SMS_KEY_ADDRESS};

    // secret key table attributes
    private static final String TABLE_SECRET = "secret";
    private static final String SECRET_KEY_ID = "id";
    private static final String SECRET_KEY_USERKEY = "userkey";
    private static final String[] SECRET_COLUMNS = {SECRET_KEY_ID, SECRET_KEY_USERKEY};


    //encrypted file tabels
    private static final String TABLE_IMAGE = "image";
    private static final String TABLE_AUDIO = "audio";
    private static final String TABLE_VIDEO = "video";
    private static final String TABLE_DOCS = "docs";
    private static final String TABLE_DIRECTORY = "directory";
    private static final String TABLE_ZIP = "zip";
    private static final String FILE_KEY_ID = "id";
    private static final String FILE_KEY_ORIGINAL_PATH = "originalPath";
    private static final String FILE_KEY_NEW_PATH = "newPath";
    private static final String FILE_KEY_ORIGINAL_EXT = "originalExt";
    private static final String FILE_KEY_DATE = "date";
    private static final String FILE_KEY_SIZE = "size";
    private static final String FILE_KEY_THUMBNAIL = "thumbnail";
    private static final String FILE_KEY_DURATION = "duration";


    public SqliteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATION_TABLE_SMS = "CREATE TABLE smses ( "
                + "threadId INTEGER, " + "type INTEGER, " + "read INTEGER, "
                + "date TEXT, " + "sentDate TEXT, " + "body TEXT, " + "address TEXT, PRIMARY KEY (body, date))";

        String CREATION_TABLE_SECRET = "CREATE TABLE " + TABLE_SECRET + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + SECRET_KEY_USERKEY + " TEXT )";

        String CREATION_TABLE_IMAGE = "CREATE TABLE " + TABLE_IMAGE + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT, " + FILE_KEY_THUMBNAIL + " BLOB )";

        String CREATION_TABLE_VIDEO = "CREATE TABLE " + TABLE_VIDEO + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT, " + FILE_KEY_DURATION + " TEXT, " + FILE_KEY_THUMBNAIL + " BLOB )";

        String CREATION_TABLE_AUDIO = "CREATE TABLE " + TABLE_AUDIO + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        String CREATION_TABLE_DOCS = "CREATE TABLE " + TABLE_DOCS + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        String CREATION_TABEL_ZIP = "CREATE TABLE " + TABLE_ZIP + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        sqLiteDatabase.execSQL(CREATION_TABLE_SMS);
        sqLiteDatabase.execSQL(CREATION_TABLE_SECRET);
        sqLiteDatabase.execSQL(CREATION_TABLE_IMAGE);
        sqLiteDatabase.execSQL(CREATION_TABLE_VIDEO);
        sqLiteDatabase.execSQL(CREATION_TABLE_AUDIO);
        sqLiteDatabase.execSQL(CREATION_TABLE_DOCS);
        sqLiteDatabase.execSQL(CREATION_TABEL_ZIP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SECRET);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ZIP);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        this.onCreate(sqLiteDatabase);
    }

    // sms encryption methods
    public void deleteOneThread(LocalSms sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SMS, "threadId = ?", new String[] { String.valueOf(sms.getThreadId()) });
        db.close();
    }

    public List<LocalSms> allSmses() {

        List<LocalSms> smses = new LinkedList<LocalSms>();
        String query = "SELECT  * FROM " + TABLE_SMS;
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        LocalSms sms = null;

        if (cursor.moveToFirst()) {
            do {
                sms = new LocalSms();
                sms.setThreadId(Integer.parseInt(cursor.getString(0)));
                sms.setType(Integer.parseInt(cursor.getString(1)));
                sms.setRead(Integer.parseInt(cursor.getString(2)));
                sms.setDate(cursor.getString(3));
                sms.setSentDate(cursor.getString(4));
                sms.setBody(cursor.getString(5));
                sms.setAddress(cursor.getString(6));
                smses.add(sms);
            } while (cursor.moveToNext());
        }
        db.close();
        return smses;
    }

    public List<LocalSms> getSmsByAddress(String address) {
        List<LocalSms> smses = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SMS + " WHERE " + SMS_KEY_ADDRESS + " = \"" + address
                + "\"" + " ORDER BY " + SMS_KEY_DATE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        LocalSms sms = null;

        if (cursor.moveToFirst()) {
            do {
                sms = new LocalSms();
                sms.setThreadId(Integer.parseInt(cursor.getString(0)));
                sms.setType(Integer.parseInt(cursor.getString(1)));
                sms.setRead(Integer.parseInt(cursor.getString(2)));
                sms.setDate(cursor.getString(3));
                sms.setSentDate(cursor.getString(4));
                sms.setBody(cursor.getString(5));
                sms.setAddress(cursor.getString(6));
                smses.add(sms);
            } while (cursor.moveToNext());
        }
        db.close();
        return smses;

    }

    public void addSms(LocalSms sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SMS_KEY_THREAD_ID, sms.getThreadId());
        values.put(SMS_KEY_TYPE, sms.getType());
        values.put(SMS_KEY_READ, sms.getRead());
        values.put(SMS_KEY_DATE, sms.getDate());
        values.put(SMS_KEY_SENT_DATE, sms.getSentDate());
        values.put(SMS_KEY_BODY, sms.getBody());
        values.put(SMS_KEY_ADDRESS, sms.getAddress());
        db.insert(TABLE_SMS,null, values);
        db.close();
    }

    public List<Conversation> getAllThreads() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Conversation> threads = new ArrayList<>();

        String query = "SELECT DISTINCT(" + SMS_KEY_ADDRESS + ") FROM " + TABLE_SMS;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        Conversation thread;
        if (c.moveToFirst()) {
            do {
                thread = new Conversation();
                String address = c.getString(0);
                String query2 = "SELECT * FROM " + TABLE_SMS + " WHERE " + SMS_KEY_ADDRESS + " = \"" + address
                        + "\" ORDER BY " + SMS_KEY_DATE + " DESC LIMIT 1";
                @SuppressLint("Recycle") Cursor c2 = db.rawQuery(query2, null);
                if(c2.moveToFirst()) {
                    thread.setConversationID(Integer.parseInt(c2.getString(0)));
                    thread.setAddress(c2.getString(6));
                    thread.setLastMessage(c2.getString(5));
                    thread.setLatestDate(c2.getString(3));
                    thread.setRead(Integer.parseInt(c2.getString(2)));
                    threads.add(thread);
                }

            } while(c.moveToNext());
        }
        db.close();
        return threads;
    }

    //encrypted file's methods

    //IMAGE
    void addImage(ImageFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(FILE_KEY_NEW_PATH, f.getNewPath());
        values.put(FILE_KEY_ORIGINAL_EXT, f.getOriginalExt());
        values.put(FILE_KEY_DATE, f.getDate());
        values.put(FILE_KEY_SIZE, f.getSize());
        values.put(FILE_KEY_THUMBNAIL, f.getThumbnail());
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public List<ImageFile> getAllImages() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ImageFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_IMAGE;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                ImageFile f = new ImageFile();
                f.setId(Integer.parseInt(c.getString(0)));
                f.setOriginalPath(c.getString(1));
                f.setNewPath(c.getString(2));
                f.setOriginalExt(c.getString(3));
                f.setDate(c.getString(4));
                f.setSize(c.getString(5));
                f.setThumbnail(c.getBlob(6));
                list.add(f);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }

    // DOC FILE
    void addDoc(DocFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(FILE_KEY_NEW_PATH, f.getNewPath());
        values.put(FILE_KEY_ORIGINAL_EXT, f.getOriginalExt());
        values.put(FILE_KEY_DATE, f.getDate());
        values.put(FILE_KEY_SIZE, f.getSize());
        db.insert(TABLE_DOCS, null, values);
        db.close();
    }

    public List<DocFile> getAllDocFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<DocFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_DOCS;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                DocFile f = new DocFile();
                f.setId(Integer.parseInt(c.getString(0)));
                f.setOriginalPath(c.getString(1));
                f.setNewPath(c.getString(2));
                f.setOriginalExt(c.getString(3));
                f.setDate(c.getString(4));
                f.setSize(c.getString(5));
                list.add(f);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }


    // secret key methods
    public void addUserKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SECRET;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            updateUserKey(key);
        } else {
            ContentValues values = new ContentValues();
            values.put(SECRET_KEY_USERKEY, key);
            db.insert(TABLE_SECRET, null, values);
        }
        db.close();
    }

    private int updateUserKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SECRET_KEY_USERKEY, key);
        return db.update(TABLE_SECRET, values, null, null);
    }

    String getUserKey() {
        String key = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SECRET + " LIMIT 1";
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            key = c.getString(1);
        }
        return key;
    }
}
