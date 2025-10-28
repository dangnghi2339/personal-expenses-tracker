package com.example.login.ui;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.data.datasource.Prefs;
import com.example.login.R;

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
