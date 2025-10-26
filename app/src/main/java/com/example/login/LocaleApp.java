package com.example.login;

import android.app.Application;
import android.content.Context;

public class LocaleApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        // Lấy ngôn ngữ đã lưu (vi/en) rồi wrap base context
        super.attachBaseContext(LocaleUtil.wrap(base, Prefs.getLang(base)));
    }
}
