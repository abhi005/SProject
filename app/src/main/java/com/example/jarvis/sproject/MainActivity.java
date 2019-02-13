package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import Helper.HomeMenuAdapter;
import utils.CustomBottomNavigation;
import utils.PortraitActivity;

public class MainActivity extends PortraitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private GridView homeMenu;
    private BottomNavigationView navigationView;
    int[] homeIcons = {R.drawable.home_call, R.drawable.home_message, R.drawable.home_image, R.drawable.home_audio, R.drawable.home_video, R.drawable.home_doc, R.drawable.home_zip};
    String[] homeIconsTitle = {"Phone", "Messaging", "Images", "Audios", "Videos", "Docs", "Zip"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // gridview
        homeMenu = (GridView) findViewById(R.id.grid_view_menu);
        HomeMenuAdapter homeMenuAdapter = new HomeMenuAdapter(this, homeIcons, homeIconsTitle);
        homeMenu.setAdapter(homeMenuAdapter);
        homeMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if(position == 0) {
                    intent = new Intent(MainActivity.this, CallLogs.class);
                } else if (position == 1) {
                    intent = new Intent(MainActivity.this, Messaging.class);
                } else if (position == 2) {
                    intent = new Intent(MainActivity.this, Images.class);
                } else if (position == 3) {
                    intent = new Intent(MainActivity.this, Audio.class);
                } else if (position == 4) {
                    intent = new Intent(MainActivity.this, Video.class);
                } else if (position == 5) {
                    intent = new Intent(MainActivity.this, Document.class);
                } else {
                    intent = new Intent(MainActivity.this, MainActivity.class);
                }
                startActivity(intent);
            }
        });

        //customizing navigation
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        CustomBottomNavigation.disableShiftMode(navigationView);
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
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;

            case R.id.bottom_nav_folder :
                startActivity(new Intent(MainActivity.this, FileManager.class));
                break;

            case R.id.bottom_nav_vault :
                startActivity(new Intent(MainActivity.this, Vault.class));
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
        MenuItem menuItem = navigationView.getMenu().getItem(0);
        menuItem.setChecked(true);
    }
}
