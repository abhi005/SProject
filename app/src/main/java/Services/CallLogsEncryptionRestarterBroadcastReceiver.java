package Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallLogsEncryptionRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, CallLogsEncryptionService.class));
    }
}
