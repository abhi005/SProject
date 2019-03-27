package com.example.jarvis.sproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.BitSet;

import Helper.Global;
import Helper.SqliteDatabaseHandler;

public class SplashScreen extends AppCompatActivity {

    private static boolean firstTime;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.darkBlack));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> new PrefetchData().execute(), Global.SPLASH_TIME_OUT);
    }

    private String computeEncryptionKey(String pin, String str) {
        final String charSet = "US-ASCII";
        BitSet aBitSet = null;
        try {
            aBitSet = BitSet.valueOf(pin.getBytes(charSet));
            aBitSet.or(BitSet.valueOf(str.getBytes(charSet)));
        } catch (UnsupportedEncodingException e) {
            computeEncryptionKey(pin, str);
        }
        assert aBitSet != null;
        return new String(aBitSet.toByteArray(), Charset.forName(charSet));
    }

    @SuppressLint("StaticFieldLeak")
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SqliteDatabaseHandler db = new SqliteDatabaseHandler(SplashScreen.this);
            db.addUserKey("3127");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            SqliteDatabaseHandler db = new SqliteDatabaseHandler(SplashScreen.this);
            String key = db.getUserKey();
            if (key.equals("")) {
                firstTime = true;
            } else {
                Global.pin = key;
                firstTime = false;
                Global.encryptionKey = computeEncryptionKey(Global.pin, "12345678");
            }
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!firstTime) {
                Intent intent = new Intent(SplashScreen.this, EnterPin.class);
                startActivity(intent);
                finish();
            } else {

            }
        }
    }
}
