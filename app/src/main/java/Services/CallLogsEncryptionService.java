package Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

import Helper.SqliteDatabaseHandler;
import Model.LocalCall;

public class CallLogsEncryptionService extends Service {

    private Timer timer;
    private TimerTask timerTask;

    public CallLogsEncryptionService(Context context) {
        super();
    }

    public CallLogsEncryptionService() {
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
        Intent broadcastIntent = new Intent(this, CallLogsEncryptionRestarterBroadcastReceiver.class);

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
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    encryptAllCallLogs();
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

    private void encryptAllCallLogs() {
        ContentResolver cr = this.getContentResolver();
        @SuppressLint("MissingPermission") Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int totalCalls = 0;
        if (c != null) {
            totalCalls = c.getCount();
            SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);
            if (c.moveToFirst()) {
                for (int i = 0; i < totalCalls; i++) {
                    String number = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    String callType = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                    String date = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    String duration = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    int type = 0;
                    int dirCode = Integer.parseInt(callType);
                    if (dirCode == CallLog.Calls.OUTGOING_TYPE) {
                        type = 1;
                    } else if (dirCode == CallLog.Calls.INCOMING_TYPE) {
                        type = 2;
                    } else {
                        type = 3;
                    }
                    LocalCall call = new LocalCall(number, type, date, duration);
                    db.addCall(call);
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
