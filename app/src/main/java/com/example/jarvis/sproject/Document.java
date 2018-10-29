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

import Helper.DocumentAdapter;
import Model.DocFile;

public class Document extends AppCompatActivity implements View.OnLongClickListener {


    public boolean isInActionMode = false;
    public boolean isAllSelected = false;

    private ImageView backButton;
    private ImageView menuButton;
    private ImageView selectAllButton;
    private EditText searchField;
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private LinearLayoutManager layoutManager;

    private ViewGroup documentActionMenu;
    private ImageView actionMenuDeleteButton;
    private ImageView actionMenuBackButton;
    private ImageView actionMenuRenameButton;
    private ImageView actionMenuInfoButton;
    private ArrayList<DocFile> documentFiles;
    private ArrayList<DocFile> selectionList = new ArrayList<>();
    private int selectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        //back button
        backButton = (ImageView) findViewById(R.id.back_btn_document);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document.super.onBackPressed();
            }
        });

        //preparing conversations data
        prepareData();


        //menu button
        menuButton = (ImageView) findViewById(R.id.menu_btn_document);

        recyclerView = (RecyclerView) findViewById(R.id.document_main_container);
        recyclerView.setHasFixedSize(true);
        documentAdapter = new DocumentAdapter(documentFiles, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(documentAdapter);


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
                selectionCounter = documentFiles.size();
                selectionList.clear();
                selectionList.addAll(documentFiles);
                isAllSelected = true;
            } else {
                selectAllButton.setImageResource(R.drawable.checkbox_unchecked);
                selectionCounter = 0;
                selectionList.clear();
                isAllSelected = false;
            }
            documentAdapter.notifyDataSetChanged();
        });


        //action menu
        documentActionMenu = (ViewGroup) findViewById(R.id.document_action_menu);
        documentActionMenu.setVisibility(View.GONE);


        //action menu delete button
        actionMenuDeleteButton = (ImageView) findViewById(R.id.document_action_menu_delete_btn);
        actionMenuDeleteButton.setOnClickListener(v -> {
            documentAdapter.updateAdapter(selectionList);
            unSetActionMode();
            documentAdapter.notifyDataSetChanged();
        });

        //action menu back button
        actionMenuBackButton = (ImageView) findViewById(R.id.document_action_menu_back_btn);
        actionMenuBackButton.setOnClickListener(v -> {
            unSetActionMode();
            documentAdapter.notifyDataSetChanged();
        });


        //action menu rename button
        actionMenuRenameButton = (ImageView) findViewById(R.id.document_action_menu_rename_btn);
        actionMenuRenameButton.setAlpha(Float.valueOf("0.5"));


        //action menu info button
        actionMenuInfoButton = (ImageView) findViewById(R.id.document_action_menu_details_btn);
        actionMenuInfoButton.setAlpha(Float.valueOf("0.5"));
    }


    public void prepareData() {
        documentFiles = new ArrayList<>();
        documentFiles.add(new DocFile(1, "resume.docx", 10.48, "17/11/2017", "12.24 pm"));
        documentFiles.add(new DocFile(2, "development.pptx", 9.08, "17/11/2017", "12.25 pm"));
        documentFiles.add(new DocFile(3, "resume.pdf", 11.95, "17/11/2017", "12.23 pm"));
        documentFiles.add(new DocFile(4, "project brief.pdf", 6.53, "17/11/2017", "12.23 pm"));
        documentFiles.add(new DocFile(5, "hhh.pdf", 12.53, "25/08/2018", "1.05 am"));
    }


    public void prepareSelection(View view, int position) {

        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) {
            selectionList.add(documentFiles.get(position));
            cb.setChecked(true);
            selectionCounter++;
        } else {
            cb.setChecked(false);
            selectionList.remove(documentFiles.get(position));
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
        documentActionMenu.startAnimation(bottomUp);
        documentActionMenu.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
        documentAdapter.notifyDataSetChanged();
    }

    public void unSetActionMode() {
        isInActionMode = false;
        backButton.setVisibility(View.VISIBLE);
        Animation topDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        documentActionMenu.startAnimation(topDown);
        documentActionMenu.setVisibility(View.GONE);
        selectAllButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        selectionCounter = 0;
        selectionList.clear();
    }

    //search query filter method
    private void searchQueryFilter(String query) {
        if(query.length() != 0) {
            ArrayList<DocFile> resultList = new ArrayList<>();
            for(DocFile f : documentFiles) {
                String name = f.getName();
                if(name.toLowerCase().contains(query.toLowerCase())) {
                    resultList.add(f);
                }
            }
            documentAdapter.filterList(resultList);
        } else {
            documentAdapter.filterList(documentFiles);
        }
    }

    @Override
    public void onBackPressed() {
        if(isInActionMode) {
            unSetActionMode();
            documentAdapter.notifyDataSetChanged();
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
