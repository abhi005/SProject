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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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


    public Dialog fileClickDialog;
    private BottomNavigationView navigationView;
    private EditText searchField;
    private TextView currentPath;
    private RecyclerView recyclerView;
    private ViewGroup actionMenu;
    private ImageView selectAllButton;
    private ImageView backButton;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuInfoButton;
    private TextView actionMenuCountText;


    public boolean isInActionMode = false;
    public boolean isAllSelected = false;
    private static int selectionCounter = 0;
    private ArrayList<FileManagerItem> selectionList = new ArrayList<>();
    List<FileManagerItem> directories = new ArrayList<FileManagerItem>();
    List<FileManagerItem> files = new ArrayList<FileManagerItem>();
    Stack<String> paths = new Stack<>();

    public File currentDir;
    private FileManagerAdapter adapter;

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

    public void forwardDirectory(String path) {
        currentPath.setText(path);
        currentDir = new File(path);
        paths.add(path);
        FetchFilesList obj = new FetchFilesList();
        obj.execute();
    }

    public void backwardDirectory() {
        paths.pop();
        String newPath = paths.peek();
        currentPath.setText(newPath);
        currentDir = new File(newPath);
        FetchFilesList obj = new FetchFilesList();
        obj.execute();
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
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        //check storage access permission
        checkPermissionReadStorage(this);

        //customizing navigation
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        CustomBottomNavigation.disableShiftMode(navigationView);

        //seting current path to tv
        currentPath = findViewById(R.id.current_path_tv);

        //recycler view initialization
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //setting file adapter
        adapter = new FileManagerAdapter(directories, this);
        recyclerView.setAdapter(adapter);


        //file click event
        fileClickDialog = new Dialog(this);
        fileClickDialog.setContentView(R.layout.popup_file_click);

        //search bar
        searchField = findViewById(R.id.search_field);
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
        selectAllButton = findViewById(R.id.menu_select_all_btn);
        selectAllButton.setVisibility(View.GONE);
        selectAllButton.setOnClickListener(v -> {
            if (!isAllSelected) {
                selectAllButton.setImageResource(R.drawable.checkbox_checked);
                selectionCounter = directories.size();
                selectionList.clear();
                selectionList.addAll(directories);
                updateSelectionCounterText(selectionCounter);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                updateSelectionCounterText(selectionCounter);
                isAllSelected = false;
            }
            adapter.notifyDataSetChanged();
        });

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> onBackPressed());

        //action menu
        actionMenu = findViewById(R.id.action_menu);
        actionMenu.setVisibility(View.GONE);

        //action menu back button
        actionMenuBackButton = findViewById(R.id.action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            adapter.notifyDataSetChanged();
        });

        //action menu count text
        actionMenuCountText = findViewById(R.id.action_menu_item_count);


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
                    adapter.deleteItems(selectionList);
                    unSetActionMode();
                    deleteButtonDialog.dismiss();
                });
            }
        });


        //action menu info button
        actionMenuInfoButton = findViewById(R.id.action_menu_info_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
        actionMenuInfoButton.setOnClickListener(view -> {
            if (selectionList.size() == 1) {
                FileManagerItem f = selectionList.get(0);
                Dialog infoButtonDialog = new Dialog(FileManager.this);
                infoButtonDialog.setContentView(R.layout.popup_file_info);
                Objects.requireNonNull(infoButtonDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
                Objects.requireNonNull(infoButtonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView icon = infoButtonDialog.findViewById(R.id.icon);
                TextView name = infoButtonDialog.findViewById(R.id.name);
                TextView path = infoButtonDialog.findViewById(R.id.path);
                TextView size = infoButtonDialog.findViewById(R.id.size);
                TextView date = infoButtonDialog.findViewById(R.id.date);
                TextView type = infoButtonDialog.findViewById(R.id.type);
                if (f.getType().equals("dir")) {
                    icon.setImageResource(R.drawable.folder);
                    name.setText(FileHelper.getDirectoryName(f.getPath()));
                } else {
                    icon.setImageResource(FileHelper.getFileIcon(f.getExt()));
                    name.setText(FileHelper.getFileName(f.getPath()));
                }
                path.setText(f.getPath());
                size.setText(f.getData());
                date.setText(f.getDate());
                type.setText(f.getType());
                infoButtonDialog.show();

            }
        });
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

    @SuppressLint("StaticFieldLeak")
    class FetchFilesList extends AsyncTask<Void, Void, Void> {

        FetchFilesList() {
        }

        @Override
        protected Void doInBackground(Void... paramss) {
            directories.clear();
            files.clear();
            File[] dirs = currentDir.listFiles();

            try {
                for (File f : dirs) {
                    Date lastModDate = new Date(f.lastModified());
                    DateFormat formater = DateFormat.getDateInstance();
                    String dateModify = formater.format(lastModDate);

                    if (f.isDirectory()) {
                        File[] fBuffer = f.listFiles();
                        int buf = 0;
                        if (fBuffer != null) {
                            buf = fBuffer.length;
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
        updateSelectionCounterText(selectionCounter);
    }

    public void updateSelectionCounterText(int counter) {
        if(counter == 0) {
            actionMenuCountText.setText("0 item selected");
        } else if(counter == 1) {
            actionMenuCountText.setText("1 item selected");
        } else {
            actionMenuCountText.setText(counter + " items selected");
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

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        navigationView.startAnimation(bottomUp);
        actionMenu.startAnimation(topDown);
        actionMenu.setVisibility(View.GONE);
        navigationView.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        selectionCounter = 0;
        selectionList.clear();
        updateSelectionCounterText(selectionCounter);
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
            case R.id.bottom_nav_home:
                startActivity(new Intent(FileManager.this, MainActivity.class));
                break;

            case R.id.bottom_nav_folder:
                startActivity(new Intent(FileManager.this, FileManager.class));
                break;

            case R.id.bottom_nav_vault:
                startActivity(new Intent(FileManager.this, Vault.class));
                break;

            case R.id.bottom_nav_setting:
                break;

            default:
                break;
        }
        finish();
        return true;
    }
}
