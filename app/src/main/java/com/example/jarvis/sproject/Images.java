package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Helper.FileHelper;
import Helper.ImagesAdapter;
import Helper.SqliteDatabaseHandler;
import Model.ImageFile;
import utils.PortraitActivity;

public class Images extends PortraitActivity implements View.OnLongClickListener {

    private RecyclerView recyclerView;
    private ImagesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private List<ImageFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;
    private List<ImageFile> images;

    private ViewGroup actionMenu;
    private TextView counterText;
    private ImageView menuButton;
    private ImageView backButton;
    private ImageView selectAllButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuInfoButton;
    private ImageView actionMenuDeleteButton;
    private SqliteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> Images.super.onBackPressed());

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        //retriving all images from database
        db = new SqliteDatabaseHandler(getApplicationContext());
        images = new ArrayList<>();
        images = fetchImageFiles();
        Log.i("image_trace", "total images: " + images.size());

        adapter = new ImagesAdapter(Images.this, images);
        recyclerView.setAdapter(adapter);

        //action menu
        actionMenu = findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);

        counterText = findViewById(R.id.action_menu_item_count);

        //menu button
        menuButton = findViewById(R.id.menu_btn);
        menuButton.setVisibility(View.VISIBLE);

        //action menu back button
        actionMenuBackButton = findViewById(R.id.action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            adapter.notifyDataSetChanged();
        });

        //action menu info button
        actionMenuInfoButton = findViewById(R.id.action_menu_info_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        actionMenuInfoButton.setOnClickListener(view -> {
            if (selectionList.size() == 1) {
                ImageFile f = selectionList.get(0);
                Dialog infoButtonDialog = new Dialog(Images.this);
                infoButtonDialog.setContentView(R.layout.popup_file_info);
                Objects.requireNonNull(infoButtonDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
                Objects.requireNonNull(infoButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView icon = infoButtonDialog.findViewById(R.id.icon);
                TextView name = infoButtonDialog.findViewById(R.id.name);
                TextView path = infoButtonDialog.findViewById(R.id.path);
                TextView size = infoButtonDialog.findViewById(R.id.size);
                TextView date = infoButtonDialog.findViewById(R.id.date);
                TextView type = infoButtonDialog.findViewById(R.id.type);
                icon.setImageResource(R.drawable.image);
                name.setText(FileHelper.getFileName(f.getNewPath()));
                path.setText(f.getNewPath());
                size.setText(f.getSize());
                date.setText(f.getDate());
                type.setText(R.string.file);
                infoButtonDialog.show();
            }
        });

        //action menu delete button
        actionMenuDeleteButton = findViewById(R.id.action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(v -> {
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
                adapter.deleteItems(selectionList, db);
                unSetActionMode();
                deleteButtonDialog.dismiss();
                adapter.updateAdapter(images = fetchImageFiles());
            });
        });

        //select all button
        selectAllButton = findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(v -> {
            if(!isAllSelected) {
                selectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = images.size();
                selectionList.clear();
                selectionList.addAll(images);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                isAllSelected = false;
            }
            adapter.notifyDataSetChanged();
        });
    }

    public List<ImageFile> fetchImageFiles() {
        return db.getAllImages();
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
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        selectionCounter = 0;
        selectionList.clear();
    }

    public void setActionMode() {
        isInActionMode = true;
        backButton.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        menuButton.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.VISIBLE);
        actionMenu.startAnimation(bottomUp);
        actionMenu.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(images.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(images.get(position));
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
            actionMenuDeleteButton.setAlpha(Float.valueOf("0.5  "));
        }
        updateSelectionCounterText(selectionCounter);
    }

    public void updateSelectionCounterText(int counter) {
        if (counter == 0) {
            counterText.setText(R.string.no_item_selected);
        } else if (counter == 1) {
            counterText.setText(R.string.one_item_selected);
        } else {
            counterText.setText(counter + R.string.items_selected);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        setActionMode();
        return true;
    }
}
