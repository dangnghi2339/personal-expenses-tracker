package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.login.data.datasource.Prefs;

public class Onboarding01Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Prefs.hasValidSession(this)) {
            startActivity(new Intent(this, IndividualActivity.class));
        } else {
            startActivity(new Intent(this, Onboarding02Activity.class));
        }
        finish();
    }
}
