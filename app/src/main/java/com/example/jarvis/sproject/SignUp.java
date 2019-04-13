package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Helper.SqliteDatabaseHandler;
import utils.PortraitActivity;

public class SignUp extends PortraitActivity implements View.OnClickListener {

    private Button nextButton;
    private EditText nameEt;
    private EditText emailEt;

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
        setContentView(R.layout.activity_sign_up);

        nameEt = findViewById(R.id.name_et);
        emailEt = findViewById(R.id.email_et);

        nextButton = findViewById(R.id.next_btn);
        nextButton.setOnClickListener(view -> {

            String name = nameEt.getText().toString();
            String email = emailEt.getText().toString();
            if (name.trim().equals("")) {
                Toast.makeText(SignUp.this, "Enter valid name", Toast.LENGTH_LONG).show();
            } else if (email.trim().equals("")) {
                Toast.makeText(SignUp.this, "Enter valid email", Toast.LENGTH_LONG).show();
            } else {
                SqliteDatabaseHandler db = new SqliteDatabaseHandler(SignUp.this);
                long i = db.addUser(name, email);
                if (i != -1 && i != 0) {
                    Intent intent = new Intent(SignUp.this, SetPin.class);
                    startActivity(intent);
                    finish();
                } else {
                    nameEt.setText("");
                    emailEt.setText("");
                    Toast.makeText(SignUp.this, "Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
