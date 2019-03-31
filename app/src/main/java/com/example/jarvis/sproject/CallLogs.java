package com.example.jarvis.sproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Helper.CallLogsAdapter;
import Helper.SqliteDatabaseHandler;
import Model.LocalCall;
import utils.PortraitActivity;

public class CallLogs extends PortraitActivity implements View.OnLongClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CallLogsAdapter callLogsAdapter;
    private EditText searchField;
    private TextView callType;
    private ImageView menuButton;
    private ImageView backButton;

    private SqliteDatabaseHandler db;
    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private TextView counterText;
    private ImageView deleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuSelectAllButton;
    private ViewGroup callHistoryActionMenu;
    private List<LocalCall> callList;
    private List<LocalCall> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.home_header_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        //fetching call data
        db = new SqliteDatabaseHandler(this);
        callList = new ArrayList<>();
        callList = fetchCallLogs();

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> CallLogs.super.onBackPressed());

        callType = findViewById(R.id.call_type_tv);

        //call type menu
        menuButton = findViewById(R.id.menu_btn);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(CallLogs.this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.call_history_type_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.answered_calls:
                        callType.setText(item.getTitle());
                        callTypeFilter("Incoming");
                        break;
                    case R.id.outgoing_calls:
                        callType.setText(item.getTitle());
                        callTypeFilter("Outgoing");
                        break;
                    case R.id.missed_calls:
                        callType.setText(item.getTitle());
                        callTypeFilter("Missed");
                        break;
                    default:
                        callType.setText(item.getTitle());
                        callTypeFilter("All");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        //adapter
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        callLogsAdapter = new CallLogsAdapter(callList, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(callLogsAdapter);

        //search bar
        searchField = findViewById(R.id.search_field);
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
        callHistoryActionMenu = findViewById(R.id.action_menu);
        callHistoryActionMenu.setVisibility(View.GONE);

        //action menu counter text
        counterText = findViewById(R.id.call_history_action_menu_item_count);

        //action menu delete button
        deleteButton = findViewById(R.id.call_history_action_menu_delete_btn);
        deleteButton.setOnClickListener(v -> {
            callLogsAdapter.updateAdapter(selectionList);
            unSetActionMode();
            callLogsAdapter.notifyDataSetChanged();
        });

        //action menu back button
        actionMenuBackButton = findViewById(R.id.call_history_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            callLogsAdapter.notifyDataSetChanged();
        });

        //select all button
        actionMenuSelectAllButton = findViewById(R.id.menu_select_all_btn);
        actionMenuSelectAllButton.setVisibility(View.GONE);
        actionMenuSelectAllButton.setOnClickListener(v -> {
            if (!isAllSelected) {
                actionMenuSelectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = callList.size();
                selectionList.clear();
                selectionList.addAll(callList);
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
        });
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            unSetActionMode();
            callLogsAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    //call type - options filter method
    private void callTypeFilter(String type) {
        if (type.equals("Incoming")) {
            List<LocalCall> resultList = new ArrayList<>();
            for (LocalCall c : callList) {
                if (c.getType() == 2) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else if (type.equals("Outgoing")) {
            List<LocalCall> resultList = new ArrayList<>();
            for (LocalCall c : callList) {
                if (c.getType() == 1) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else if (type.equals("Missed")) {
            List<LocalCall> resultList = new ArrayList<>();
            for (LocalCall c : callList) {
                if (c.getType() == 3) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else {
            callLogsAdapter.filterList(callList);
        }
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if (query.length() != 0) {
            List<LocalCall> resultList = new ArrayList<>();
            for (LocalCall c : callList) {
                String number = c.getNumber();
                if (number.contains(query.toLowerCase())) {
                    resultList.add(c);
                }
            }
            callLogsAdapter.filterList(resultList);
        } else {
            callLogsAdapter.filterList(callList);
        }
    }

    private List<LocalCall> fetchCallLogs() {
        return db.getAllCalls();
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(callList.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(callList.get(position));
            selectionCounter--;
        }
        updateSelectionCounterText(selectionCounter);
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

    @SuppressLint("SetTextI18n")
    private void updateSelectionCounterText(int counter) {
        if (counter == 0) {
            counterText.setText(R.string.no_item_selected);
        } else if (counter == 1) {
            counterText.setText(R.string.one_item_selected);
        } else {
            counterText.setText(counter + " " + getString(R.string.items_selected));
        }
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        callHistoryActionMenu.startAnimation(topDown);
        callHistoryActionMenu.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        actionMenuSelectAllButton.setVisibility(View.GONE);
        selectionCounter = 0;
        updateSelectionCounterText(selectionCounter);
        selectionList.clear();
    }

    @Override
    public boolean onLongClick(View v) {
        setActionMode();
        return true;
    }
}
