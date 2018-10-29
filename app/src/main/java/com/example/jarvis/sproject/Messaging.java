package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Helper.MessagingAdapter;
import Model.Conversation;
import Model.Message;

public class Messaging extends AppCompatActivity implements View.OnLongClickListener {

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;

    private List<Conversation> conversations;
    private EditText searchField;
    private ImageView menuButton;
    private ImageView backButton;
    private ImageView selectAllButton;
    private RecyclerView recyclerView;
    private MessagingAdapter messagingAdapter;
    private LinearLayoutManager layoutManager;

    private TextView actionMenuCounterText;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ViewGroup messagingActionMenu;
    private List<Conversation> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn_messaging);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messaging.super.onBackPressed();
            }
        });

        //preparing conversations data
        prepareConversationData();

        //menu button
        menuButton = (ImageView) findViewById(R.id.menu_btn_messaging);

        recyclerView = (RecyclerView) findViewById(R.id.messaging_main_container);
        recyclerView.setHasFixedSize(true);
        messagingAdapter = new MessagingAdapter(conversations, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messagingAdapter);


        //search bar
        searchField = (EditText) findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQueryFilter(s.toString());
            }
        });


        //action mode menu
        messagingActionMenu = (ViewGroup) findViewById(R.id.messaging_action_menu);
        messagingActionMenu.setVisibility(View.GONE);

        //action menu counter text
        actionMenuCounterText = (TextView) findViewById(R.id.messaging_action_menu_item_count);

        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.messaging_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagingAdapter.updateAdapter(selectionList);
                unSetActionMode();
                messagingAdapter.notifyDataSetChanged();
            }
        });

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.messaging_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                messagingAdapter.notifyDataSetChanged();
            }
        });

        //select all button
        selectAllButton = (ImageView) findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllSelected) {
                    selectAllButton.setImageResource(R.drawable.checkbox_checked);
                    selectionCounter = conversations.size();
                    selectionList.clear();
                    selectionList.addAll(conversations);
                    updateSelectionCounterText(selectionCounter);
                    isAllSelected = true;
                } else {
                    selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                    selectionCounter = 0;
                    selectionList.clear();
                    updateSelectionCounterText(selectionCounter);
                    isAllSelected = false;
                }
                messagingAdapter.notifyDataSetChanged();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.home_header_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public void prepareConversationData() {
        conversations = new ArrayList<>();
        ArrayList<Message> list = new ArrayList<>();
        list.add(new Message(1, "Your airtel mobile has been registered for Fully Blocked category in NDNC registry.", "8:23 PM", "28 Sep", "self", "1909"));
        list.add(new Message(1, "amount of rs. 17.7 is debited from A/C XXXX5284 on date 08-10-2018 Available Clear Bal is Rs. 1687.85", "8:23 PM", "28 Sep", "1909", "self"));
        conversations.add(new Conversation(1, "1909", "1909", list ));
    }


    public void prepareSelection(View view, int position) {

        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(conversations.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(conversations.get(position));
            selectionCounter--;
        }

        updateSelectionCounterText(selectionCounter);
    }

    public void updateSelectionCounterText(int counter) {
        if(counter == 0) {
            actionMenuCounterText.setText("0 item selected");
        } else if(counter == 1) {
            actionMenuCounterText.setText("1 item selected");
        } else {
            actionMenuCounterText.setText(counter + " items selected");
        }
    }

    public void setActionMode(){
        isInActionMode = true;
        backButton.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        messagingActionMenu.startAnimation(bottomUp);
        messagingActionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
        messagingAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        messagingActionMenu.startAnimation(topDown);
        messagingActionMenu.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        actionMenuCounterText.setText("0 item selected");
        selectionCounter = 0;
        selectionList.clear();
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            List<Conversation> resultList = new ArrayList<>();
            for(Conversation c : conversations) {
                String name = c.getPersonName();
                String messageText = c.getLastMessage();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(c);
                }
                if(messageText.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(c);
                }
            }
            messagingAdapter.filterList(resultList);
        } else {
            messagingAdapter.filterList(conversations);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        setActionMode();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            messagingAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}