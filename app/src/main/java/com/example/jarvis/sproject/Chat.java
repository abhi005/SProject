package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Helper.ChatAdapter;
import Model.Message;
import utils.PortraitActivity;

public class Chat extends PortraitActivity implements View.OnLongClickListener{

    private ArrayList<Message> messages;
    private String name;
    private String contact;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter chatAdapter;
    private LinearLayout messageEditContainer;
    private ImageView backButton;

    public boolean isInActionMode = false;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuCopyButton;
    private ImageView actionMenuForwardButton;
    private ImageView actionMenuDetailsButton;
    private ViewGroup chatActionMenu;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditContainer = (LinearLayout) findViewById(R.id.edit_text);
        chatActionMenu = (ViewGroup) findViewById(R.id.action_menu);
        chatActionMenu.setVisibility(View.GONE);

        Intent i = getIntent();
        messages = (ArrayList<Message>) i.getSerializableExtra("MESSAGES");
        name = (String) i.getStringExtra("PERSON_NAME");
        contact = (String) i.getStringExtra("PERSON_CONTACT");

        TextView title = (TextView) findViewById(R.id.person_name_tv);
        if (name.length() != 0) {
            title.setText(name);
        } else {
            title.setText(contact);
        }

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat.super.onBackPressed();
            }
        });

        //adapter
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(messages, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.chat_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                chatAdapter.notifyDataSetChanged();
            }
        });
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

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            chatAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        setActionMode();
        return true;
    }

    public void setActionMode(){
        isInActionMode = true;
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        backButton.setVisibility(View.GONE);
        chatActionMenu.startAnimation(bottomUp);
        chatActionMenu.setVisibility(View.VISIBLE);
        messageEditContainer.setVisibility(View.GONE);
        chatAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        chatActionMenu.startAnimation(topDown);
        chatActionMenu.setVisibility(View.GONE);
        messageEditContainer.setVisibility(View.VISIBLE);
    }
}
