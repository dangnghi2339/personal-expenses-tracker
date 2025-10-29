package com.example.dack1; // Package đúng

import android.app.Application;
import android.content.Context;

// Import các class cần thiết TỪ DỰ ÁN SRC
import com.example.dack1.util.LocaleUtil;
import com.example.dack1.util.SessionManager; // <-- Sửa import này

public class LocaleApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        // Lấy ngôn ngữ đã lưu từ SessionManager
        // Tạo một instance mới của SessionManager để lấy ngôn ngữ
        SessionManager sessionManager = new SessionManager(base);
        String language = sessionManager.getLanguage(); // <-- Sửa dòng này

        // Wrap base context với ngôn ngữ đã lấy
        super.attachBaseContext(LocaleUtil.wrap(base, language));
    }
}