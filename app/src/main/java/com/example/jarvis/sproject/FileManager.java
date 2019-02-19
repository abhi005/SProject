package com.example.jarvis.sproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import Helper.FileHelper;
import Helper.FileManagerAdapter;
import Helper.Global;
import Model.FileManagerItem;
import utils.CustomBottomNavigation;
import utils.PortraitActivity;

public class FileManager extends PortraitActivity implements View.OnLongClickListener, BottomNavigationView.OnNavigationItemSelectedListener  {

    private ImageView storageButton;
    private BottomNavigationView navigationView;
    private Dialog storageButtonDialog;
    private ViewGroup searchBar;
    private EditText searchField;
    private TextView currentPath;
    private RecyclerView recyclerView;
    private ViewGroup actionMenu;
    private ImageView selectAllButton;
    private ImageView backButton;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout actionMenuEncryptButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuInfoButton;


    //storage type variable
    private static int currentStorageType = 0;
    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private static int selectionCounter = 0;
    private ArrayList<FileManagerItem> selectionList = new ArrayList<>();
    List<FileManagerItem> directories = new ArrayList<FileManagerItem>();
    List<FileManagerItem> files = new ArrayList<FileManagerItem>();
    Stack<String> paths = new Stack<>();

    private File currentDir;
    private FileManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        //check storage access permission
        checkPermissionReadStorage(this);

        //customizing navigation
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        CustomBottomNavigation.disableShiftMode(navigationView);

        //seting current path to tv
        currentPath = (TextView) findViewById(R.id.current_path_tv);

        //recycler view initialization
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //setting file adapter
        adapter = new FileManagerAdapter(directories, this);
        recyclerView.setAdapter(adapter);

        //storage type button
        storageButton = (ImageView) findViewById(R.id.menu_storage_btn);
        storageButtonDialog = new Dialog(this);
        storageButton.setOnClickListener(v -> {
            storageButtonDialog.setContentView(R.layout.popup_storage_type);
            Objects.requireNonNull(storageButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            storageButtonDialog.show();

            //storage type popup on click listeners
            LinearLayout internalStorageBtn = (LinearLayout) storageButtonDialog.findViewById(R.id.internal_storage_btn);
            LinearLayout sdcardStorageBtn = (LinearLayout) storageButtonDialog.findViewById(R.id.sd_card_btn);
            internalStorageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeStorageType(0);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    paths.clear();
                    forwardDirectory(path);
                    storageButtonDialog.dismiss();
                }
            });
            sdcardStorageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeStorageType(1);
                    List<String> tempList = getExternalStorageDirectories();
                    String path = tempList.get(0);
                    paths.clear();
                    forwardDirectory(path);
                    storageButtonDialog.dismiss();
                }
            });
        });

        //search bar
        searchBar = (ViewGroup) findViewById(R.id.search_bar);
        searchField = (EditText) findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQueryFilter(s.toString());
            }
        });

        // setting base directory
        String initialPath = Environment.getExternalStorageDirectory().getPath();
        forwardDirectory(initialPath);

        //select all button
        selectAllButton = (ImageView) findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllSelected) {
                    selectAllButton.setImageResource(R.drawable.checkbox_checked);
                    selectionCounter = directories.size();
                    selectionList.clear();
                    selectionList.addAll(directories);
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

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //action menu
        actionMenu = (ViewGroup) findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSetActionMode();
                adapter.notifyDataSetChanged();
            }
        });


        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateAdapter(selectionList);
                unSetActionMode();
                adapter.notifyDataSetChanged();
            }
        });


        //action menu encrypt button
        actionMenuEncryptButton = (LinearLayout) findViewById(R.id.action_menu_encryption_btn);
        actionMenuEncryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FileManager.this, "encryption clicked", Toast.LENGTH_LONG).show();
            }
        });

        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
    }


    @SuppressLint("StaticFieldLeak")
    class FetchFilesList extends AsyncTask<Void, Void, Void> {

        private FileManager activity;

        FetchFilesList(Context context) {
            this.activity = (FileManager) context;
        }

        @Override
        protected Void doInBackground(Void... paramss) {
            directories.clear();
            files.clear();
            File[] dirs = currentDir.listFiles();

            try {
                for(File f : dirs) {
                    Date lastModDate = new Date(f.lastModified());
                    DateFormat formater = DateFormat.getDateInstance();
                    String dateModify = formater.format(lastModDate);

                    if(f.isDirectory()) {
                        File[] fBuffer = f.listFiles();
                        int buf = 0;
                        if(fBuffer != null) {
                            buf = fBuffer.length;
                        } else {
                            buf = 0;
                        }
                        String numOfItems = String.valueOf(buf);
                        if (buf == 0) {
                            numOfItems += " item";
                        } else {
                            numOfItems += " items";
                        }

                        directories.add(new FileManagerItem(f.getName(), f.getAbsolutePath(), numOfItems, dateModify, "dir", null));
                    } else {
                        String dataSize = FileHelper.getReadableFileSize(f.length());
                        String ext = FileHelper.getFileExtension(f);
                        files.add(new FileManagerItem(f.getName(), f.getAbsolutePath(), dataSize, dateModify, "file", ext));
                    }
                }
            } catch (Exception e) {
                Log.e("file_access_exception", e.getMessage());
            }
            Collections.sort(directories);
            Collections.sort(files);
            directories.addAll(files);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            adapter.notifyDataSetChanged();
        }
    }

    public void forwardDirectory(String path) {
        currentPath.setText(path);
        currentDir = new File(path);
        paths.add(path);
        FetchFilesList obj = new FetchFilesList(this);
        obj.execute();
    }

    public void backwardDirectory() {
        paths.pop();
        String newPath = paths.peek();
        currentPath.setText(newPath);
        currentDir = new File(newPath);
        FetchFilesList obj = new FetchFilesList(this);
        obj.execute();
    }

    public List<String> getExternalStorageDirectories() {
        List<String> results = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);
            String internalRoot = Environment.getExternalStorageDirectory().getAbsolutePath().toLowerCase();

            for (File file : externalDirs) {
                if (file == null) //solved NPE on some Lollipop devices
                    continue;
                String path = file.getPath().split("/Android")[0];

                if (path.toLowerCase().startsWith(internalRoot))
                    continue;

                boolean addPath = false;
                addPath = Environment.isExternalStorageRemovable(file);
                if (addPath) {
                    results.add(path);
                }
            }
        }
        return results;
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            adapter.notifyDataSetChanged();
        } else {
            if(paths.size() > 1) {
                backwardDirectory();
            } else {
                super.onBackPressed();
            }
        }
    }

    //storage permission
    public void checkPermissionReadStorage(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Global.MY_PERMISSIONS_REQUEST_READ_STORAGE);
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
                return;
            }
        }
    }

    private void changeStorageType(int index) {
        if(index == 0) {
            currentStorageType = 0;
            storageButton.setImageResource(R.drawable.storage);
        } else {
            currentStorageType = 1;
            storageButton.setImageResource(R.drawable.sd_card);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_home :
                startActivity(new Intent(FileManager.this, MainActivity.class));
                break;

            case R.id.bottom_nav_folder :
                startActivity(new Intent(FileManager.this, FileManager.class));
                break;

            case R.id.bottom_nav_vault :
                startActivity(new Intent(FileManager.this, Vault.class));
                break;

            case R.id.bottom_nav_setting :
                break;

            default :
                break;
        }
        finish();
        return true;
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            List<FileManagerItem> resultList = new ArrayList<>();
            for(FileManagerItem f : directories) {
                String name = f.getName();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            adapter.filterList(resultList);
        } else {
            adapter.filterList(directories);
        }
    }

    public void setActionMode() {
        isInActionMode = true;
        backButton.setVisibility(View.GONE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(topDown);
        actionMenu.startAnimation(bottomUp);
        navigationView.setVisibility(View.GONE);
        actionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        storageButton.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(bottomUp);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        storageButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        selectionCounter = 0;
        selectionList.clear();
    }

    public void prepareSelection(View view, int position) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(directories.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(directories.get(position));
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
    }

    //highlighting bottom nav item
    private void updateNavigationBarState() {
        MenuItem menuItem = navigationView.getMenu().getItem(1);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onLongClick(View view) {
        setActionMode();
        return true;
    }
}