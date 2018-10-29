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

import java.util.ArrayList;

import Helper.VideoAdapter;
import Model.VideoFile;

public class Video extends AppCompatActivity implements View.OnLongClickListener {



    public boolean isInActionMode = false;
    public boolean isAllSelected = false;

    private ImageView backButton;
    private ImageView menuButton;
    private ImageView selectAllButton;
    private EditText searchField;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private LinearLayoutManager layoutManager;

    private ViewGroup videoActionMenu;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuRenameButton;
    private ImageView actionMenuInfoButton;
    private ArrayList<VideoFile> videoFiles;
    private ArrayList<VideoFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn_video);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video.super.onBackPressed();
            }
        });

        //preparing conversations data
        prepareData();


        //menu button
        menuButton = (ImageView) findViewById(R.id.menu_btn_video);

        recyclerView = (RecyclerView) findViewById(R.id.video_main_container);
        recyclerView.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(videoFiles, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoAdapter);


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
                selectionCounter = videoFiles.size();
                selectionList.clear();
                selectionList.addAll(videoFiles);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                isAllSelected = false;
            }
            videoAdapter.notifyDataSetChanged();
        });


        //action menu
        videoActionMenu = (ViewGroup) findViewById(R.id.video_action_menu);
        videoActionMenu.setVisibility(View.GONE);


        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.video_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(v -> {
            videoAdapter.updateAdapter(selectionList);
            unSetActionMode();
            videoAdapter.notifyDataSetChanged();
        });

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.video_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            videoAdapter.notifyDataSetChanged();
        });


        //action menu rename button
        actionMenuRenameButton = (ImageView) findViewById(R.id.video_action_menu_rename_btn);
        actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));


        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.video_action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
    }


    private void prepareData() {
        videoFiles = new ArrayList<>();
        videoFiles.add(new VideoFile(1, "The Chainsmoker - closer", 10.48, "00:18", R.drawable.image1));
        videoFiles.add(new VideoFile(2, "Cheap thrills", 9.08, "00:25", R.drawable.image2));
        videoFiles.add(new VideoFile(3, "Mera Intekam Dekhegi", 11.95, "01:38", R.drawable.image3));
        videoFiles.add(new VideoFile(4, "Tu hi hai aashiqui", 6.53, "25:38", R.drawable.image4));
        videoFiles.add(new VideoFile(5, "Girls like you", 12.53,"05:06", R.drawable.image5));
    }

    public void prepareSelection(View view, int position) {

        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(videoFiles.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(videoFiles.get(position));
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
        videoActionMenu.startAnimation(bottomUp);
        videoActionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
        videoAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        videoActionMenu.startAnimation(topDown);
        videoActionMenu.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        selectionCounter = 0;
        selectionList.clear();
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            ArrayList<VideoFile> resultList = new ArrayList<>();
            for(VideoFile f : videoFiles) {
                String name = f.getName();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            videoAdapter.filterList(resultList);
        } else {
            videoAdapter.filterList(videoFiles);
        }
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            videoAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onLongClick(View v) {
        setActionMode();
        return true;
    }
}
