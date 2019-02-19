package Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Model.Conversation;
import Model.LocalSms;

public class SqliteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "seguro";
    private static final String TABLE_SMS = "smses";
    private static final String KEY_THREAD_ID = "threadId";
    private static final String KEY_TYPE = "type";
    private static final String KEY_READ = "read";
    private static final String KEY_DATE = "date";
    private static final String KEY_SENT_DATE = "sentDate";
    private static final String KEY_BODY = "body";
    private static final String KEY_ADDRESS = "address";
    private static final String[] COLUMNS = {KEY_THREAD_ID, KEY_TYPE, KEY_READ, KEY_DATE, KEY_SENT_DATE, KEY_BODY, KEY_ADDRESS};

    public SqliteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATION_TABLE = "CREATE TABLE smses ( "
                + "threadId INTEGER, " + "type INTEGER, " + "read INTEGER, "
                + "date TEXT, " + "sentDate TEXT, " + "body TEXT, " + "address TEXT, PRIMARY KEY (body, date))";

        sqLiteDatabase.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        this.onCreate(sqLiteDatabase);
    }

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
        String query = "SELECT * FROM " + TABLE_SMS + " WHERE " + KEY_ADDRESS + " = \"" + address
                + "\"" + " ORDER BY " + KEY_DATE + " ASC";
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
        values.put(KEY_THREAD_ID, sms.getThreadId());
        values.put(KEY_TYPE, sms.getType());
        values.put(KEY_READ, sms.getRead());
        values.put(KEY_DATE, sms.getDate());
        values.put(KEY_SENT_DATE, sms.getSentDate());
        values.put(KEY_BODY, sms.getBody());
        values.put(KEY_ADDRESS, sms.getAddress());
        db.insert(TABLE_SMS,null, values);
        db.close();
    }

    public List<Conversation> getAllThreads() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Conversation> threads = new ArrayList<>();

        String query = "SELECT DISTINCT(" + KEY_ADDRESS + ") FROM " + TABLE_SMS;
        @SuppressLint("Recycle") Cursor c = db.rawQuery(query, null);
        Conversation thread;
        if (c.moveToFirst()) {
            do {
                thread = new Conversation();
                String address = c.getString(0);
                String query2 = "SELECT * FROM " + TABLE_SMS + " WHERE " + KEY_ADDRESS + " = \"" + address
                        + "\" ORDER BY " + KEY_DATE + " DESC LIMIT 1";
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
}
