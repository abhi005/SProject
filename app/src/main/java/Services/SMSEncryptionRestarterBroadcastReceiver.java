package Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSEncryptionRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i(SMSEncryptionRestarterBroadcastReceiver.class.getSimpleName(), "Service stops! oooooppppss!");
        context.startService(new Intent(context, SMSEncryptionService.class));
    }
}
