package com.example.dack1.ui.view; // Đã sửa package

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dack1.util.LocaleUtil;
import com.example.dack1.util.SessionManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // --- ĐOẠN CODE ĐÃ SỬA ---
        // 1. Tạo đối tượng SessionManager
        SessionManager sessionManager = new SessionManager(newBase);
        // 2. Gọi hàm getLanguage() đúng
        String language = sessionManager.getLanguage();

        // 3. Wrap context
        super.attachBaseContext(LocaleUtil.wrap(newBase, language));
        // --- KẾT THÚC SỬA ---
    }

    // Hàm này giữ nguyên
    @Override
    public void applyOverrideConfiguration(@NonNull android.content.res.Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findByNames(String... names) {
        if (names == null) return null;
        for (String name : names) {
            int id = idByName(name);
            if (id != 0) {
                View v = findViewById(id);
                if (v != null) return (T) v;
            }
        }
        return null;
    }

    protected int idByName(String name) {
        if (name == null || name.isEmpty()) return 0;
        return getResources().getIdentifier(name, "id", getPackageName());
    }
}