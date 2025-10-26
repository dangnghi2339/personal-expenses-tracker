package com.example.dack1.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dack1.R;
import com.example.dack1.util.SessionManager;

public class LogoActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Check if user is already logged in
        SessionManager sessionManager = new SessionManager(this);
        
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                // User is logged in, go to MainActivity
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // User is not logged in, go to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
