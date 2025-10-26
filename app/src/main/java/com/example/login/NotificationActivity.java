package com.example.login;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        Switch sw = findViewById(R.id.swNotify);
        sw.setChecked(Prefs.isNotify(this));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> Prefs.setNotify(this, isChecked));
    }
}
