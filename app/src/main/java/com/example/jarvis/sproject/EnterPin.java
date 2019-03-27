package com.example.jarvis.sproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Helper.Global;

public class EnterPin extends AppCompatActivity implements View.OnClickListener {

    private StringBuilder pin;
    private int len = 0;
    private List<Integer> keyPad;
    private int[] cbs;

    private TextView response;

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
        setContentView(R.layout.activity_enter_pin);

        // pin string
        pin = new StringBuilder("");

        //keypad buttons
        keyPad = new ArrayList<>(Arrays.asList(R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9));

        //pin checkboxes
        cbs = new int[]{R.id.pin_cb1, R.id.pin_cb2, R.id.pin_cb3, R.id.pin_cb4};

        //reponse text
        response = findViewById(R.id.response);

        for (int i = 0; i <= 9; i++) {
            TextView tv = findViewById(keyPad.get(i));
            tv.setOnClickListener(this);
        }
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        TextView tv = (TextView) view;
        if (keyPad.contains(id)) {
            pin.append(tv.getText());
            len++;
            setCheckBox(len);
            proceed();
        } else if (id == R.id.btn_clear) {
            pin = new StringBuilder("");
            len = 0;
            unsetCheckBox();
        }
    }

    private void unsetCheckBox() {
        for (int i = 0; i < 4; i++) {
            CheckBox cb = findViewById(cbs[i]);
            cb.setChecked(false);
        }
    }

    private void setCheckBox(int index) {
        CheckBox cb = findViewById(cbs[index - 1]);
        cb.setChecked(true);
    }

    private void proceed() {
        if (len == 4) {
            if (check()) {
                Intent intent = new Intent(EnterPin.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                response.setText(R.string.wrong_pin);
                response.setVisibility(View.VISIBLE);
                Global.vibrate(EnterPin.this, 100);
                response.startAnimation(shakeErrorAnimation());
                unsetCheckBox();
                pin = new StringBuilder("");
                len = 0;
            }
        }
    }

    private TranslateAnimation shakeErrorAnimation() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5));
        return shake;
    }

    private boolean check() {
        if (pin.toString().trim().equals(Global.pin.trim())) {
            return true;
        } else {
            return false;
        }
    }
}
