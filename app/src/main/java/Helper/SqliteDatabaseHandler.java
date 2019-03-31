package Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import Model.AudioFile;
import Model.Conversation;
import Model.DocFile;
import Model.ImageFile;
import Model.LocalCall;
import Model.LocalSms;
import Model.VaultFile;
import Model.VideoFile;
import Model.ZipFile;

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

    // calllogs table attributes
    private static final String TABLE_CALLS = "calls";
    private static final String CALLS_KEY_ID = "id";
    private static final String CALLS_KEY_TYPE = "type";
    private static final String CALLS_KEY_NUMBER = "contact";
    private static final String CALLS_KEY_DATE = "date";
    private static final String CALLS_KEY_DURATION = "duration";

    // secret key table attributes
    private static final String TABLE_SECRET = "secret";
    private static final String SECRET_KEY_USERKEY = "userkey";


    // vault table attributes
    private static final String TABLE_VAULT = "vault";
    private static final String VAULT_KEY_ID = "id";
    private static final String VAULT_KEY_NAME = "name";
    private static final String VAULT_KEY_ORIGINAL_PATH = "originalPath";
    private static final String VAULT_KEY_EXT = "ext";
    private static final String VAULT_KEY_DATE = "date";
    private static final String VAULT_KEY_SIZE = "size";

    //encrypted file tabels
    private static final String TABLE_IMAGE = "image";
    private static final String TABLE_AUDIO = "audio";
    private static final String TABLE_VIDEO = "video";
    private static final String TABLE_DOCS = "docs";
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

        String CREATION_TABLE_CALLS = "CREATE TABLE calls ("
                + CALLS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CALLS_KEY_TYPE + " INTEGER, "
                + CALLS_KEY_NUMBER + " TEXT, " + CALLS_KEY_DATE + " TEXT, " + CALLS_KEY_DURATION
                + " TEXT, UNIQUE (" + CALLS_KEY_NUMBER + ", " + CALLS_KEY_DATE + "))";

        String CREATION_TABLE_SECRET = "CREATE TABLE " + TABLE_SECRET + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + SECRET_KEY_USERKEY + " TEXT )";

        String CREATION_TABLE_IMAGE = "CREATE TABLE " + TABLE_IMAGE + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT, " + FILE_KEY_THUMBNAIL + " BLOB )";

        String CREATION_TABLE_VIDEO = "CREATE TABLE " + TABLE_VIDEO + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT NOT NULL UNIQUE, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT, " + FILE_KEY_DURATION + " TEXT )";

        String CREATION_TABLE_AUDIO = "CREATE TABLE " + TABLE_AUDIO + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT NOT NULL UNIQUE, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        String CREATION_TABLE_DOCS = "CREATE TABLE " + TABLE_DOCS + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT NOT NULL UNIQUE, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        String CREATION_TABLE_ZIP = "CREATE TABLE " + TABLE_ZIP + " ( "
                + FILE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_KEY_ORIGINAL_PATH + " TEXT NOT NULL UNIQUE, "
                + FILE_KEY_NEW_PATH + " TEXT, " + FILE_KEY_ORIGINAL_EXT + " TEXT, " + FILE_KEY_DATE + " TEXT, "
                + FILE_KEY_SIZE + " TEXT )";

        String CREATION_TABLE_VAULT = "CREATE TABLE " + TABLE_VAULT + " ( "
                + VAULT_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + VAULT_KEY_NAME + " TEXT NOT NULL, "
                + VAULT_KEY_ORIGINAL_PATH + " TEXT NOT NULL UNIQUE, " + VAULT_KEY_EXT + " TEXT, " + VAULT_KEY_DATE + " TEXT, "
                + VAULT_KEY_SIZE + " TEXT )";

        sqLiteDatabase.execSQL(CREATION_TABLE_SMS);
        sqLiteDatabase.execSQL(CREATION_TABLE_CALLS);
        sqLiteDatabase.execSQL(CREATION_TABLE_SECRET);
        sqLiteDatabase.execSQL(CREATION_TABLE_IMAGE);
        sqLiteDatabase.execSQL(CREATION_TABLE_VIDEO);
        sqLiteDatabase.execSQL(CREATION_TABLE_AUDIO);
        sqLiteDatabase.execSQL(CREATION_TABLE_DOCS);
        sqLiteDatabase.execSQL(CREATION_TABLE_ZIP);
        sqLiteDatabase.execSQL(CREATION_TABLE_VAULT);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VAULT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
        this.onCreate(sqLiteDatabase);
    }

    // sms encryption methods
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

    void deleteSmsesByAddress(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SMS, SMS_KEY_ADDRESS + " = ?", new String[]{address});
        db.close();
    }

    void readSms(String address) {
        ContentValues values = new ContentValues();
        values.put(SMS_KEY_READ, 1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_SMS, values, SMS_KEY_ADDRESS + " = ?", new String[] {address});
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

    // call logs encryption methods
    public void addCall(LocalCall call) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CALLS_KEY_NUMBER, call.getNumber());
        values.put(CALLS_KEY_TYPE, call.getType());
        values.put(CALLS_KEY_DATE, call.getDate());
        values.put(CALLS_KEY_DURATION, call.getDuration());
        db.insert(TABLE_CALLS, null, values);
        db.close();
    }

    public List<LocalCall> getAllCalls() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocalCall> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CALLS;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                LocalCall call = new LocalCall();
                call.setId(Integer.parseInt(c.getString(0)));
                call.setType(Integer.parseInt(c.getString(1)));
                call.setNumber(c.getString(2));
                call.setDate(c.getString(3));
                call.setDuration(c.getString(4));
                list.add(call);
            } while (c.moveToNext());
        }
        db.close();
        return list;
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
        long i = db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    void deleteImage(ImageFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGE, FILE_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
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

    void deleteDoc(DocFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCS, FILE_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
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


    // AUDIO FILE
    void addAudio(AudioFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(FILE_KEY_NEW_PATH, f.getNewPath());
        values.put(FILE_KEY_ORIGINAL_EXT, f.getOriginalExt());
        values.put(FILE_KEY_DATE, f.getDate());
        values.put(FILE_KEY_SIZE, f.getSize());
        db.insert(TABLE_AUDIO, null, values);
        db.close();
    }

    void deleteAudio(AudioFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AUDIO, FILE_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
        db.close();
    }
    public List<AudioFile> getAllAudioFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<AudioFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_AUDIO;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                AudioFile f = new AudioFile();
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


    // VIDEO FILE
    void addVideo(VideoFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(FILE_KEY_NEW_PATH, f.getNewPath());
        values.put(FILE_KEY_ORIGINAL_EXT, f.getOriginalExt());
        values.put(FILE_KEY_DATE, f.getDate());
        values.put(FILE_KEY_SIZE, f.getSize());
        values.put(FILE_KEY_DURATION, f.getDuration());
        db.insert(TABLE_VIDEO, null, values);
        db.close();
    }

    void deleteVideo(VideoFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VIDEO, FILE_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
        db.close();
    }

    public List<VideoFile> getAllVideoFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<VideoFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_VIDEO;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                VideoFile f = new VideoFile();
                f.setId(Integer.parseInt(c.getString(0)));
                f.setOriginalPath(c.getString(1));
                f.setNewPath(c.getString(2));
                f.setOriginalExt(c.getString(3));
                f.setDate(c.getString(4));
                f.setSize(c.getString(5));
                f.setDuration(c.getString(6));
                list.add(f);
            } while (c.moveToNext());
        }
        db.close();
        return list;
    }


    // ZIP FILE

    void addZip(ZipFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(FILE_KEY_NEW_PATH, f.getNewPath());
        values.put(FILE_KEY_ORIGINAL_EXT, f.getOriginalExt());
        values.put(FILE_KEY_DATE, f.getDate());
        values.put(FILE_KEY_SIZE, f.getSize());
        db.insert(TABLE_ZIP, null, values);
        db.close();
    }

    void deleteZip(ZipFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ZIP, FILE_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
        db.close();
    }

    public List<ZipFile> getAllZipFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ZipFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ZIP;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                ZipFile f = new ZipFile();
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

    //vault
    void addVaultFile(VaultFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VAULT_KEY_NAME, f.getName());
        values.put(VAULT_KEY_ORIGINAL_PATH, f.getOriginalPath());
        values.put(VAULT_KEY_EXT, f.getOriginalExt());
        values.put(VAULT_KEY_DATE, f.getDate());
        values.put(VAULT_KEY_SIZE, f.getSize());
        db.insert(TABLE_VAULT, null, values);
        db.close();
    }

    void deleteVaultFile(VaultFile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VAULT, VAULT_KEY_ID + " = ?", new String[]{String.valueOf(f.getId())});
        db.close();
    }

    public List<VaultFile> getAllVaultFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<VaultFile> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_VAULT;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                VaultFile f = new VaultFile();
                f.setId(Integer.parseInt(c.getString(0)));
                f.setName(c.getString(1));
                f.setOriginalPath(c.getString(2));
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

    private void updateUserKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SECRET_KEY_USERKEY, key);
        db.update(TABLE_SECRET, values, null, null);
    }

    public String getUserKey() {
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
