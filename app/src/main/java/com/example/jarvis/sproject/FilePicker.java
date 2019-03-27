package com.example.jarvis.sproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import Helper.FileHelper;
import Helper.FilePickerAdapter;
import Model.FileManagerItem;


public class FilePicker extends AppCompatActivity {

    public File currentDir;
    Stack<String> paths = new Stack<>();
    List<FileManagerItem> directories = new ArrayList<FileManagerItem>();
    List<FileManagerItem> files = new ArrayList<FileManagerItem>();
    private TextView currentPath;
    private RecyclerView recyclerView;
    private ImageView backButton;
    private RecyclerView.LayoutManager layoutManager;
    private FilePickerAdapter adapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        //seting current path to tv
        currentPath = findViewById(R.id.current_path_tv);

        //recycler view initialization
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //setting file adapter
        adapter = new FilePickerAdapter(directories, this);
        recyclerView.setAdapter(adapter);

        // setting base directory
        String initialPath = Environment.getExternalStorageDirectory().getPath();
        forwardDirectory(initialPath);

        //back button
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> onBackPressed());
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

    @Override
    public void onBackPressed() {
        if (paths.size() > 1) {
            backwardDirectory();
        } else {
            super.onBackPressed();
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
}
