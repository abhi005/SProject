package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import Helper.ChatAdapter;
import Helper.SmsHelper;
import Helper.SqliteDatabaseHandler;
import Model.LocalSms;
import utils.PortraitActivity;

public class Chat extends PortraitActivity {

    private List<LocalSms> smses;
    private String address;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter chatAdapter;
    private LinearLayout messageEditContainer;
    private ImageView sendButton;
    private ImageView backButton;
    private EditText smsContent;


    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditContainer = (LinearLayout) findViewById(R.id.edit_text);

        Intent i = getIntent();
        address = Objects.requireNonNull(i.getExtras()).getString("ADDRESS");

        TextView title = (TextView) findViewById(R.id.person_name_tv);

        // geting contact name
        String contactName = SmsHelper.getContactName(this, address);
        if (contactName == null || contactName.equals("")) {
            title.setText(address);
        } else {
            title.setText(contactName);
        }

        //fetching all sms
        getAllSms();

        //edit text - sms content
        smsContent = (EditText) findViewById(R.id.message_text);
        //back button
        backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> Chat.super.onBackPressed());

        //send button
        sendButton = (ImageView) findViewById(R.id.send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = smsContent.getText().toString();
                if (msg.equals("")) {
                    Toast.makeText(getApplicationContext(), "message can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    SmsHelper.sendSms(getApplicationContext(), msg, address);
                    smsContent.setText("");
                    recreate();
                }
            }
        });

        //adapter
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(smses, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void getAllSms() {
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(getApplicationContext());
        smses = db.getSmsByAddress(address);
        db.close();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
