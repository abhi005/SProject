package com.example.jarvis.sproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Helper.FileHelper;
import Helper.Global;
import Helper.SqliteDatabaseHandler;
import Helper.VaultAdapter;
import Model.VaultFile;
import utils.CustomBottomNavigation;
import utils.PortraitActivity;


public class Vault extends PortraitActivity implements View.OnLongClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView addButton;
    private BottomNavigationView navigationView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private VaultAdapter vaultAdapter;
    private EditText searchField;
    private ImageView selectAllButton;
    private ViewGroup actionMenu;
    public Dialog itemClickDialog;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuInfoButton;
    private TextView counterText;

    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private List<VaultFile> selectionList = new ArrayList<>();
    private SqliteDatabaseHandler db;
    private List<VaultFile> files;
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        //check storage access permission
        checkPermissionReadStorage(this);

        //fetching all encrypted audio files
        files = new ArrayList<>();
        db = new SqliteDatabaseHandler(this);
        files = fetchVaultFiles();

        //customizing navigation
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        CustomBottomNavigation.disableShiftMode(navigationView);

        //add new button
        addButton = findViewById(R.id.menu_add_btn);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(Vault.this, FilePicker.class);
            startActivity(intent);
            finish();
        });

        //item click dialogue
        itemClickDialog = new Dialog(this);
        itemClickDialog.setContentView(R.layout.popup_vault_file_click);

        //recycler view - adapter
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        vaultAdapter = new VaultAdapter(files, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(vaultAdapter);

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

        //select all button
        selectAllButton = findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(v -> {
            if (!isAllSelected) {
                selectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = files.size();
                selectionList.clear();
                selectionList.addAll(files);
                updateSelectionCounterText(selectionCounter);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                updateSelectionCounterText(selectionCounter);
                isAllSelected = false;
            }
            vaultAdapter.notifyDataSetChanged();
        });

        //action menu
        actionMenu = findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);

        //counter text
        counterText = findViewById(R.id.action_menu_item_count);

        //action menu delete button
        actionMenuDeleteButton = findViewById(R.id.action_menu_delete_btn);
        actionMenuDeleteButton.setAlpha(Float.valueOf("0.5"));
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
                    vaultAdapter.deleteItems(selectionList, db);
                    unSetActionMode();
                    deleteButtonDialog.dismiss();
                    vaultAdapter.updateAdapter(files = fetchVaultFiles());
                });
            }
        });

        //action menu back button
        actionMenuBackButton = findViewById(R.id.action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            vaultAdapter.notifyDataSetChanged();
        });

        //action menu info button
        actionMenuInfoButton = findViewById(R.id.action_menu_info_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        actionMenuInfoButton.setOnClickListener(view -> {
            if (selectionList.size() == 1) {
                VaultFile f = selectionList.get(0);
                Dialog infoButtonDialog = new Dialog(Vault.this);
                infoButtonDialog.setContentView(R.layout.popup_file_info);
                Objects.requireNonNull(infoButtonDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
                Objects.requireNonNull(infoButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView icon = infoButtonDialog.findViewById(R.id.icon);
                TextView name = infoButtonDialog.findViewById(R.id.name);
                TextView path = infoButtonDialog.findViewById(R.id.path);
                TextView size = infoButtonDialog.findViewById(R.id.size);
                TextView date = infoButtonDialog.findViewById(R.id.date);
                icon.setImageResource(FileHelper.getFileIcon(f.getOriginalExt()));
                name.setText(f.getName());
                path.setText(f.getOriginalPath());
                size.setText(f.getSize());
                date.setText(f.getDate());
                infoButtonDialog.show();
            }
        });
    }

    public List<VaultFile> fetchVaultFiles() {
        return db.getAllVaultFiles();
    }

    //storage permission
    public void checkPermissionReadStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Global.MY_PERMISSIONS_REQUEST_READ_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Global.MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(this, "application needs storage access to work properly", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    public void setActionMode() {
        isInActionMode = true;
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(topDown);
        actionMenu.startAnimation(bottomUp);
        navigationView.setVisibility(View.GONE);
        actionMenu.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.VISIBLE);
        vaultAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(bottomUp);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        addButton.setVisibility(View.VISIBLE);
        selectionCounter = 0;
        selectionList.clear();
        updateSelectionCounterText(selectionCounter);
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if (query.length() != 0) {
            List<VaultFile> resultList = new ArrayList<>();
            for (VaultFile f : files) {
                String name = f.getName();
                if (name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            vaultAdapter.filterList(resultList);
        } else {
            vaultAdapter.filterList(files);
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
    protected void onDestroy() {
        FileHelper.deleteTempFile();
        super.onDestroy();
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
                startActivity(new Intent(Vault.this, FileManager.class));
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
