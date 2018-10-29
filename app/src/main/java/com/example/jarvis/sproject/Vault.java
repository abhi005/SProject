package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

import java.util.ArrayList;

import Helper.BottomNavigationViewHelper;
import Helper.VaultAdapter;
import Model.File;

public class Vault extends AppCompatActivity implements View.OnLongClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView addButton;
    private BottomNavigationView navigationView;
    private Dialog addButtonDialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private VaultAdapter vaultAdapter;
    private EditText searchField;
    private ImageView selectAllButton;
    private ViewGroup actionMenu;
    private ViewGroup searchBar;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuRenameButton;
    private ImageView actionMenuInfoButton;

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private ArrayList<File> selectionList = new ArrayList<>();

    private ArrayList<File> files;
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        //customizing navigation
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(navigationView);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        addButtonDialog = new Dialog(this);

        //add new button
        addButton = (ImageView) findViewById(R.id.menu_add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonDialog.setContentView(R.layout.vault_add_popup);
                addButtonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addButtonDialog.show();
            }
        });

        //search bar
        searchBar = (ViewGroup) findViewById(R.id.search_bar);

        //prepare data
        prepareData();

        //recycler view - adapter
        recyclerView = (RecyclerView) findViewById(R.id.vault_recyclerview);
        recyclerView.setHasFixedSize(true);
        vaultAdapter = new VaultAdapter(files, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(vaultAdapter);

        //search bar hide effect with recycler view scroll
        /*recyclerView.addOnScrollListener(new RecyclerScrollHideBehaviour() {
            @Override
            public void show() {
                searchBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                searchBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                searchBar.setVisibility(View.GONE);
                searchBar.animate().translationY(-searchBar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });*/

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
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllSelected) {
                    selectAllButton.setImageResource(R.drawable.checkbox_checked);
                    selectionCounter = files.size();
                    selectionList.clear();
                    selectionList.addAll(files);
                    isAllSelected = true;
                } else {
                    selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                    selectionCounter = 0;
                    selectionList.clear();
                    isAllSelected = false;
                }
                vaultAdapter.notifyDataSetChanged();
            }
        });

        //action menu
        actionMenu = (ViewGroup) findViewById(R.id.vault_action_menu);
        actionMenu.setVisibility(View.GONE);

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.vault_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                vaultAdapter.notifyDataSetChanged();
            }
        });


        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.vault_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaultAdapter.updateAdapter(selectionList);
                unSetActionMode();
                vaultAdapter.notifyDataSetChanged();
            }
        });


        //action menu rename button
        actionMenuRenameButton = (ImageView) findViewById(R.id.vault_action_menu_rename_btn);
        actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));


        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.vault_action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));

    }


    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            ArrayList<File> resultList = new ArrayList<>();
            for(File f : files) {
                String name = f.getName();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            vaultAdapter.filterList(resultList);
        } else {
            vaultAdapter.filterList(files);
        }
    }

    public void setActionMode() {
        isInActionMode = true;
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(topDown);
        actionMenu.startAnimation(bottomUp);
        navigationView.setVisibility(View.GONE);
        actionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.GONE);
        vaultAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        actionMenu.startAnimation(topDown);
        navigationView.startAnimation(bottomUp);
        actionMenu.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        selectionCounter = 0;
        selectionList.clear();
    }

    private void prepareData() {
        files = new ArrayList<>();
        files.add(new File("Android", "dir", "04/08/2016", 539.65));
        files.add(new File("resume", "doc", "03/09/2018", 10.123));
        files.add(new File("My Pic", "image", "03/09/2018", 1.66));
        files.add(new File("Shape of you", "audio", "25/07/2017", 11.9936));
        files.add(new File("Interstellar", "video", "17/12/2015", 1228.8));
        files.add(new File("Android", "dir", "04/08/2016", 539.65));
        files.add(new File("resume", "doc", "03/09/2018", 10.123));
        files.add(new File("My Pic", "image", "03/09/2018", 1.66));
        files.add(new File("Shape of you", "audio", "25/07/2017", 11.9936));
        files.add(new File("Interstellar", "video", "17/12/2015", 1228.8));
        files.add(new File("Android", "dir", "04/08/2016", 539.65));
        files.add(new File("resume", "doc", "03/09/2018", 10.123));
        files.add(new File("My Pic", "image", "03/09/2018", 1.66));
        files.add(new File("Shape of you", "audio", "25/07/2017", 11.9936));
        files.add(new File("Interstellar", "video", "17/12/2015", 1228.8));
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(files.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(files.get(position));
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

    @Override
    public boolean onLongClick(View v) {
        setActionMode();

        return true;
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
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            vaultAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_home :
                startActivity(new Intent(Vault.this, MainActivity.class));
                break;

            case R.id.bottom_nav_folder :
                break;

            case R.id.bottom_nav_vault :
                startActivity(new Intent(Vault.this, Vault.class));
                break;

            case R.id.bottom_nav_setting :
                break;

            default :
                break;
        }
        finish();
        return true;
    }

    //highlighting bottom nav item
    private void updateNavigationBarState() {
        MenuItem menuItem = navigationView.getMenu().getItem(2);
        menuItem.setChecked(true);
    }
}
