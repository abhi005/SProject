package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Helper.CallLogsAdapter;
import Model.Call;
import utils.PortraitActivity;

public class CallLogs extends PortraitActivity implements View.OnLongClickListener {

    private List<Call> callHistory;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CallLogsAdapter callLogsAdapter;
    private EditText searchField;
    private TextView callType;
    private ImageView menuButton;
    private ImageView backButton;

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private TextView actionMenuCounterText;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuSelectAllButton;
    private ViewGroup callHistoryActionMenu;
    private List<Call> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogs.super.onBackPressed();
            }
        });

        callType = (TextView) findViewById(R.id.call_type_tv);

        //call type menu
        menuButton = (ImageView) findViewById(R.id.menu_btn);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(CallLogs.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.call_history_type_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.answered_calls :
                                callType.setText(item.getTitle());
                                callTypeFilter("Incoming");
                                break;
                            case R.id.outgoing_calls :
                                callType.setText(item.getTitle());
                                callTypeFilter("Outgoing");
                                break;
                            case R.id.missed_calls :
                                callType.setText(item.getTitle());
                                callTypeFilter("Missed");
                                break;
                            default :
                                callType.setText(item.getTitle());
                                callTypeFilter("All");
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        prepareCallData();

        //adapter
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        callLogsAdapter = new CallLogsAdapter(callHistory, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(callLogsAdapter);

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
        callHistoryActionMenu = (ViewGroup) findViewById(R.id.action_menu);
        callHistoryActionMenu.setVisibility(View.GONE);

        //action menu counter text
        actionMenuCounterText = (TextView) findViewById(R.id.call_history_action_menu_item_count);

        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.call_history_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLogsAdapter.updateAdapter(selectionList);
                unSetActionMode();
                callLogsAdapter.notifyDataSetChanged();
            }
        });

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.call_history_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                callLogsAdapter.notifyDataSetChanged();
            }
        });

        //select all button
        actionMenuSelectAllButton = (ImageView) findViewById(R.id.menu_select_all_btn);
        actionMenuSelectAllButton.setVisibility(View.GONE);
        actionMenuSelectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllSelected) {
                    actionMenuSelectAllButton.setImageResource(R.drawable.checkbox_checked);
                    selectionCounter = callHistory.size();
                    selectionList.clear();
                    selectionList.addAll(callHistory);
                    updateSelectionCounterText(selectionCounter);
                    isAllSelected = true;
                } else {
                    actionMenuSelectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                    selectionCounter = 0;
                    selectionList.clear();
                    updateSelectionCounterText(selectionCounter);
                    isAllSelected = false;
                }
                callLogsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            callLogsAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    //call type - options filter method
    private void callTypeFilter(String type) {
        if(type.equals("Incoming")) {
            List<Call> resultList = new ArrayList<>();
            for(Call c : callHistory) {
                if(c.getType().equals("Incoming")) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else if(type.equals("Outgoing")) {
            List<Call> resultList = new ArrayList<>();
            for(Call c : callHistory) {
                if(c.getType().equals("Outgoing")) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else if(type.equals("Missed")) {
            List<Call> resultList = new ArrayList<>();
            for(Call c : callHistory) {
                if(c.getType().equals("Missed")) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else {
            callLogsAdapter.filterList(callHistory);
        }
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            List<Call> resultList = new ArrayList<>();
            for(Call c : callHistory) {
                String name = c.getName();
                String number = c.getContact();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(c);
                }
                if(number.contains(query.toLowerCase())) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else {
            callLogsAdapter.filterList(callHistory);
        }
    }


    private void prepareCallData() {
        callHistory = new ArrayList<>();
        callHistory.add(new Call("Abhishek Jain", "+919016379997", "", "11:44 AM", "Incoming", "20m 43s", "India"));
        callHistory.add(new Call("", "+918200321853", "", "05:04 PM", "Outgoing", "20m 43s", "India"));
        callHistory.add(new Call("Abcd Enfg", "+911234567856", "", "12:01 PM", "Missed", "43s", "Iceland"));
        callHistory.add(new Call("", "+912365482563", "", "02:25 AM", "Outgoing", "23s", "England"));
        callHistory.add(new Call("Abhishek Jain", "+919016379997", "", "11:44 AM", "Incoming", "20m 43s", "India"));
        callHistory.add(new Call("", "+918200321853", "", "05:04 PM", "Outgoing", "20m 43s", "India"));
        callHistory.add(new Call("Abcd Enfg", "+911234567856", "", "12:01 PM", "Missed", "43s", "Iceland"));
        callHistory.add(new Call("", "+912365482563", "", "02:25 AM", "Outgoing", "23s", "England"));
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

    @Override
    public boolean onLongClick(View v) {
        setActionMode();
        return true;
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(callHistory.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(callHistory.get(position));
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
        callHistoryActionMenu.startAnimation(bottomUp);
        callHistoryActionMenu.setVisibility(View.VISIBLE);
        actionMenuSelectAllButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
        callLogsAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        callHistoryActionMenu.startAnimation(topDown);
        callHistoryActionMenu.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        actionMenuSelectAllButton.setVisibility(View.GONE);
        actionMenuCounterText.setText("0 item selected");
        selectionCounter = 0;
        selectionList.clear();
    }
}
