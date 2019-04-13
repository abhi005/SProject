package Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CallLogsHelper {

    @SuppressLint("MissingPermission")
    static void makeCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    static void deleteCallLog(Context context, int id) {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(context);
        db.deleteCallLogById(id);
    }
}
