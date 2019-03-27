package com.example.jarvis.sproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import Helper.Global;
import Helper.SmsHelper;

public class NewChat extends AppCompatActivity {

    private TextView personName;
    private ImageView sendButton;
    private ImageView backButton;
    private EditText smsContent;
    private String address = "";
    private ImageView contactButton;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        //person name - title
        personName = findViewById(R.id.person_name_tv);

        // sms content
        smsContent = findViewById(R.id.message_text);

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> NewChat.super.onBackPressed());

        //contact button
        contactButton = findViewById(R.id.contact_btn);
        contactButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, Global.PICK_CONTACT);
        });

        // send button
        sendButton = findViewById(R.id.send_btn);
        sendButton.setOnClickListener(view -> {
            String msg = smsContent.getText().toString();
            if (msg.equals("")) {
                Toast.makeText(getApplicationContext(), "message can't be empty", Toast.LENGTH_LONG).show();
            } else if (address.equals("")) {
                Toast.makeText(getApplicationContext(), "no contact selected", Toast.LENGTH_LONG).show();
            } else {
                SmsHelper.sendSms(getApplicationContext(), msg, address);
                smsContent.setText("");
                Intent intent = new Intent(NewChat.this, Messaging.class);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Global.PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                assert contactUri != null;
                @SuppressLint("Recycle") Cursor c = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                assert c != null;
                c.moveToFirst();
                address = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = SmsHelper.getContactName(NewChat.this, address);
                personName.setText(name);
            }
        }
    }
}
