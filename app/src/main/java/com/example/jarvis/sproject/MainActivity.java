package com.example.jarvis.sproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import Helper.Global;
import Helper.HomeMenuAdapter;
import Helper.SqliteDatabaseHandler;
import Services.SMSEncryptionService;
import utils.CustomBottomNavigation;
import utils.PortraitActivity;

public class MainActivity extends PortraitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Intent smsServiceIntent;
    private SMSEncryptionService smsService;
    Context context;

    private GridView homeMenu;
    private BottomNavigationView navigationView;
    int[] homeIcons = {R.drawable.home_call, R.drawable.home_message, R.drawable.home_image, R.drawable.home_audio, R.drawable.home_video, R.drawable.home_doc, R.drawable.home_zip};
    String[] homeIconsTitle = {"Phone", "Messaging", "Images", "Audios", "Videos", "Docs", "Zip"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkPermissionSms(this);
        SqliteDatabaseHandler db = new SqliteDatabaseHandler(this);
        db.addUserKey("12345678");

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

        //sms service
        smsService = new SMSEncryptionService(getApplicationContext());
        smsServiceIntent = new Intent(getContext(), smsService.getClass());
        if (!isSMSServiceRunning(smsService.getClass())) {
            startService(smsServiceIntent);
        }
    }

    //storage permission
    public void checkPermissionSms(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, Global.MY_PERMISSIONS_REQUEST_READ_STORAGE);
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
                    Toast.makeText(this, "application needs data access to work properly", Toast.LENGTH_LONG).show();
                }
            }
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

    private boolean isSMSServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        stopService(smsServiceIntent);
        super.onDestroy();
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

    public Context getContext() {
        return context;
    }
}
