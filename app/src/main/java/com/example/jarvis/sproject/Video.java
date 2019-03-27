package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Helper.FileHelper;
import Helper.SqliteDatabaseHandler;
import Helper.VideoAdapter;
import Model.VideoFile;
import utils.PortraitActivity;

public class Video extends PortraitActivity implements View.OnLongClickListener {



    public boolean isInActionMode = false;
    public boolean isAllSelected = false;

    private ImageView backButton;
    private ImageView menuButton;
    private ImageView selectAllButton;
    private EditText searchField;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private LinearLayoutManager layoutManager;
    public Dialog fileClickDialog;

    private SqliteDatabaseHandler db;
    private ViewGroup videoActionMenu;
    private TextView counterText;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuInfoButton;
    private List<VideoFile> videoFiles;
    private List<VideoFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> Video.super.onBackPressed());

        // fetching all video files
        videoFiles = new ArrayList<>();
        db = new SqliteDatabaseHandler(this);
        videoFiles = fetchVideoFiles();

        //file click dialogue box
        fileClickDialog = new Dialog(this);
        fileClickDialog.setContentView(R.layout.popup_encrypted_file_click);

        //menu button
        menuButton = findViewById(R.id.menu_btn);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(videoFiles, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoAdapter);


        //search bar
        searchField = findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                searchQueryFilter(s.toString());
            }
        });

        //select all button
        selectAllButton = findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(v -> {
            if(!isAllSelected) {
                selectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = videoFiles.size();
                selectionList.clear();
                selectionList.addAll(videoFiles);
                updateSelectionCounterText(selectionCounter);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                updateSelectionCounterText(selectionCounter);
                isAllSelected = false;
            }
            videoAdapter.notifyDataSetChanged();
        });


        //action menu
        videoActionMenu = findViewById(R.id.action_menu);
        videoActionMenu.setVisibility(View.GONE);

        //counter text
        counterText = findViewById(R.id.action_menu_item_count);

        //action menu delete button
        actionMenuDeleteButton = findViewById(R.id.action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(v -> {
            if (selectionList.size() > 0) {
                Dialog deleteButtonDialog = new Dialog(this);
                deleteButtonDialog.setContentView(R.layout.popup_file_delete);
                Objects.requireNonNull(deleteButtonDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
                Objects.requireNonNull(deleteButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteButtonDialog.show();

                // delete confirmation listener
                TextView cancelBtn = deleteButtonDialog.findViewById(R.id.cancel_btn);
                TextView deleteBtn = deleteButtonDialog.findViewById(R.id.delete_btn);
                cancelBtn.setOnClickListener(view -> {
                    //cancel btn
                    deleteButtonDialog.dismiss();
                });
                deleteBtn.setOnClickListener(view -> {
                    //delete btn
                    videoAdapter.deleteItems(selectionList, db);
                    unSetActionMode();
                    deleteButtonDialog.dismiss();
                    videoAdapter.updateAdapter(videoFiles = fetchVideoFiles());
                });
            }
        });

        //action menu back button
        actionMenuBackButton = findViewById(R.id.action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            videoAdapter.notifyDataSetChanged();
        });

        //action menu info button
        actionMenuInfoButton = findViewById(R.id.action_menu_info_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        actionMenuInfoButton.setOnClickListener(view -> {
            if (selectionList.size() == 1) {
                VideoFile f = selectionList.get(0);
                Dialog infoButtonDialog = new Dialog(Video.this);
                infoButtonDialog.setContentView(R.layout.popup_file_info);
                Objects.requireNonNull(infoButtonDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
                Objects.requireNonNull(infoButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView icon = infoButtonDialog.findViewById(R.id.icon);
                TextView name = infoButtonDialog.findViewById(R.id.name);
                TextView path = infoButtonDialog.findViewById(R.id.path);
                TextView size = infoButtonDialog.findViewById(R.id.size);
                TextView date = infoButtonDialog.findViewById(R.id.date);
                TextView type = infoButtonDialog.findViewById(R.id.type);
                icon.setImageResource(R.drawable.video);
                name.setText(FileHelper.getFileName(f.getNewPath()));
                path.setText(f.getNewPath());
                size.setText(f.getSize());
                date.setText(f.getDate());
                type.setText(R.string.file);
                infoButtonDialog.show();
            }
        });
    }


    public List<VideoFile> fetchVideoFiles() {
        return db.getAllVideoFiles();
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
            actionMenuInfoButton.setAlpha(Float.valueOf("1.0"));
        } else {
            actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        }
        if (selectionCounter > 0) {
            actionMenuDeleteButton.setAlpha(Float.valueOf("1.0"));
        } else {
            actionMenuDeleteButton.setAlpha(Float.valueOf("0.5"));
        }
        updateSelectionCounterText(selectionCounter);
    }

    private void updateSelectionCounterText(int counter) {
        if (counter == 0) {
            counterText.setText(R.string.no_item_selected);
        } else if (counter == 1) {
            counterText.setText(R.string.one_item_selected);
        } else {
            counterText.setText(counter + R.string.items_selected);
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
                String name = f.getOriginalPath();
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

    @Override
    protected void onDestroy() {
        FileHelper.deleteTempFile();
        super.onDestroy();
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
    public boolean onLongClick(View view) {
        setActionMode();
        return true;
    }
}
