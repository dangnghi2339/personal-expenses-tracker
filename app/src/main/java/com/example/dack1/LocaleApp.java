package com.example.dack1; // Package đúng

import android.app.Application;
import android.content.Context;

// Import các class cần thiết TỪ DỰ ÁN SRC
import com.example.dack1.util.LocaleUtil;
import com.example.dack1.util.SessionManager; // <-- Sửa import này

public class LocaleApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}