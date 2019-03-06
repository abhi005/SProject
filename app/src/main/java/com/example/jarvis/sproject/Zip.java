package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

import Helper.AudioAdapter;
import Helper.SqliteDatabaseHandler;
import Helper.ZipAdapter;
import Model.AudioFile;
import Model.ZipFile;

public class Zip extends AppCompatActivity {

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;

    private ImageView backButton;
    private ImageView menuButton;
    private ImageView selectAllButton;
    private EditText searchField;
    private RecyclerView recyclerView;
    private ZipAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SqliteDatabaseHandler db;

    private ViewGroup actionMenu;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuRenameButton;
    private ImageView actionMenuInfoButton;
    private List<ZipFile> zipFiles;
    private List<ZipFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> Zip.super.onBackPressed());

        //fetching all encrypted zip files
        zipFiles = new ArrayList<>();
        db = new SqliteDatabaseHandler(this);
        fetchZipFiles();


        //menu button
        menuButton = (ImageView) findViewById(R.id.menu_btn);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        adapter = new ZipAdapter(zipFiles, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


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

        //select all button
        selectAllButton = (ImageView) findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(v -> {
            if(!isAllSelected) {
                selectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = zipFiles.size();
                selectionList.clear();
                selectionList.addAll(zipFiles);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                isAllSelected = false;
            }
            adapter.notifyDataSetChanged();
        });


        //action menu
        actionMenu = (ViewGroup) findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);


        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.audio_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(v -> {
            adapter.updateAdapter(selectionList);
            unSetActionMode();
            adapter.notifyDataSetChanged();
        });

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.audio_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            adapter.notifyDataSetChanged();
        });


        //action menu rename button
        actionMenuRenameButton = (ImageView) findViewById(R.id.audio_action_menu_rename_btn);
        actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));


        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.audio_action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
    }


    private void fetchZipFiles() {
        zipFiles = db.getAllZipFiles();
    }

    public void prepareSelection(View view, int position) {

        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(zipFiles.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(zipFiles.get(position));
            selectionCounter--;
        }

        if (selectionCounter == 1) {
            actionMenuRenameButton.setAlpha(Float.valueOf("1.0"));
            actionMenuInfoButton.setAlpha(Float.valueOf("1.0"));
        } else {
            actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));
            actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        }
    }

    public void setActionMode(){
        isInActionMode = true;
        backButton.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        actionMenu.startAnimation(bottomUp);
        actionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        selectionCounter = 0;
        selectionList.clear();
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            List<ZipFile> resultList = new ArrayList<>();
            for(ZipFile f : zipFiles) {
                String name = f.getOriginalPath();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            adapter.filterList(resultList);
        } else {
            adapter.filterList(zipFiles);
        }
    }


    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
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
}
