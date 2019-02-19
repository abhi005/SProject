package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import Helper.ImagesAdapter;
import Model.ImageFile;
import utils.PortraitActivity;

public class Images extends PortraitActivity {

    private GridView gridView;
    private ImagesAdapter adapter;

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private List<ImageFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;
    private List<ImageFile> thumbnails;

    private ViewGroup actionMenu;
    private ImageView menuButton;
    private ImageView selectAllButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuRenameButton;
    private ImageView actionMenuInfoButton;
    private ImageView actionMenuDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        //back button
        ImageView backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Images.super.onBackPressed();
            }
        });

        gridView = (GridView) findViewById(R.id.gridview);
        prepareData();

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);
        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = convertDpToPixel(dp, getApplicationContext());
            gridView.setColumnWidth(Math.round(px));
        }
        adapter = new ImagesAdapter(Images.this, thumbnails);
        gridView.setAdapter(adapter);

        //action menu
        actionMenu = (ViewGroup) findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);

        //menu button
        menuButton = (ImageView) findViewById(R.id.menu_btn);
        menuButton.setVisibility(View.VISIBLE);

        //select all button
        selectAllButton = (ImageView) findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.images_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                adapter.notifyDataSetChanged();
            }
        });

        //action menu rename button
        actionMenuRenameButton = (ImageView) findViewById(R.id.images_action_menu_rename_btn);
        actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));

        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.images_action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));

        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.images_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateAdapter(selectionList);
                unSetActionMode();
                adapter.notifyDataSetChanged();
            }
        });

        //select all button
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllSelected) {
                    selectAllButton.setImageResource(R.drawable.checkbox_checked);
                    selectionCounter = thumbnails.size();
                    selectionList.clear();
                    selectionList.addAll(thumbnails);
                    isAllSelected = true;
                } else {
                    selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                    selectionCounter = 0;
                    selectionList.clear();
                    isAllSelected = false;
                }
                adapter.notifyDataSetChanged();
            }
        });
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
        menuButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        selectionCounter = 0;
        selectionList.clear();
    }

    public void setActionMode(Animation bottomUp){
        menuButton.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.VISIBLE);
        actionMenu.startAnimation(bottomUp);
        actionMenu.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void prepareData() {
        thumbnails = new ArrayList<>();
        thumbnails.add(new ImageFile(R.drawable.image1));
        thumbnails.add(new ImageFile(R.drawable.image2));
        thumbnails.add(new ImageFile(R.drawable.image3));
        thumbnails.add(new ImageFile(R.drawable.image4));
        thumbnails.add(new ImageFile(R.drawable.image5));
    }

    private float convertDpToPixel(float dp, Context context) {
        Resources resource = context.getResources();
        DisplayMetrics metrics = resource.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view.findViewById(R.id.image_cb);
        if (!cb.isChecked()) {
            selectionList.add(thumbnails.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(thumbnails.get(position));
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
}
