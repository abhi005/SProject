package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import Helper.SqliteDatabaseHandler;
import utils.CustomBottomNavigation;
import utils.PortraitActivity;

public class Setting extends PortraitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigationView;
    private TextView userDetails;
    private TextView changePin;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        //customizing navigation
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        CustomBottomNavigation.disableShiftMode(navigationView);

        //user details btn
        userDetails = findViewById(R.id.user_details);
        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog userDetailsDialog = new Dialog(Setting.this);
                userDetailsDialog.setContentView(R.layout.popup_user_detail);
                TextView emailTv = userDetailsDialog.findViewById(R.id.email);
                TextView nameTv = userDetailsDialog.findViewById(R.id.name);

                SqliteDatabaseHandler db = new SqliteDatabaseHandler(Setting.this);
                String[] details = db.getUserDetails();
                emailTv.setText(details[1]);
                nameTv.setText(details[0]);
                userDetailsDialog.show();
            }
        });


        //change pin btn
        changePin = findViewById(R.id.change_pin);
        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_home:
                startActivity(new Intent(Setting.this, MainActivity.class));
                break;

            case R.id.bottom_nav_folder:
                startActivity(new Intent(Setting.this, FileManager.class));
                break;

            case R.id.bottom_nav_vault:
                startActivity(new Intent(Setting.this, Vault.class));
                break;

            case R.id.bottom_nav_setting:
                startActivity(new Intent(Setting.this, Setting.class));
                break;

            default:
                break;
        }
        finish();
        return true;
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

    //highlighting bottom nav item
    private void updateNavigationBarState() {
        MenuItem menuItem = navigationView.getMenu().getItem(3);
        menuItem.setChecked(true);
    }
}
