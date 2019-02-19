package Services;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Helper.SqliteDatabaseHandler;
import Model.LocalSms;

public class SMSEncryptionService extends Service {


    private Timer timer;
    private TimerTask timerTask;

    public SMSEncryptionService(Context context) {
        super();
    }

    public SMSEncryptionService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, SMSEncryptionRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    public void startService() {
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    encryptAllSms();
                }
            }
        };
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void encryptAllSms() {
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);;
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    int read = c.getInt(c.getColumnIndexOrThrow(Telephony.Sms.READ));
                    String smsDateSent = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT));
                    int threadId = c.getInt(c.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID));
                    String address = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    int type = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)));

                    LocalSms sms = new LocalSms(threadId, type, read, smsDate, smsDateSent, body, address);
                    db.addSms(sms);
                    //cr.delete(Telephony.Sms.CONTENT_URI, null, null);
                    c.moveToNext();
                }
            }
            c.close();
            db.close();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
