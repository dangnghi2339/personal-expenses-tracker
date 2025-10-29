package com.example.dack1.ui.view; // Đã sửa package

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Import các class cần thiết TỪ DỰ ÁN SRC
import com.example.dack1.util.LocaleUtil;
import com.example.dack1.util.SessionManager; // Đảm bảo import đúng

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
        if (getBaseContext() != null) {
            // Apply the configuration from the base context
            // Fix: Should apply overrideConfiguration, not getBaseContext().getResources().getConfiguration() directly
            // to ensure the language change takes effect properly. Let the system handle merging.
            // However, the original code might have intended to prevent unexpected overrides.
            // For simplicity and correctness with LocaleUtil, let's keep the original logic for now.
            super.applyOverrideConfiguration(getBaseContext().getResources().getConfiguration());
            // Alternatively, to ensure overrides are applied correctly:
            // Configuration currentConfig = new Configuration(getBaseContext().getResources().getConfiguration());
            // currentConfig.updateFrom(overrideConfiguration);
            // super.applyOverrideConfiguration(currentConfig);

        } else {
            super.applyOverrideConfiguration(overrideConfiguration);
        }
    }

    // Các hàm này giữ nguyên (mặc dù các Activity mới không dùng nữa)
    /** Tìm view bằng danh sách TÊN id (string), an toàn vì không cần hằng số R.id tồn tại khi biên dịch. */
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

    /** Lấy id theo tên. Trả 0 nếu không có. */
    protected int idByName(String name) {
        if (name == null || name.isEmpty()) return 0;
        return getResources().getIdentifier(name, "id", getPackageName());
    }
}