package Helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsHelper {

    public static String getContactName(Context context, String address) {
        String contactName = "";
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
        try {
            c.moveToFirst();
            contactName = c.getString(0);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            c.close();
            return contactName;
        }
    }

    public static void sendSms(Context context, String msg, String address) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, msg, null, null);
        Toast.makeText(context, "Message sent", Toast.LENGTH_LONG).show();
    }
}
