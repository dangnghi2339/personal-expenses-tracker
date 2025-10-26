package com.example.login;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtil.wrap(newBase, Prefs.getLang(newBase)));
    }

    @Override
    public void applyOverrideConfiguration(@NonNull android.content.res.Configuration overrideConfiguration) {
        if (getBaseContext() != null) {
            super.applyOverrideConfiguration(getBaseContext().getResources().getConfiguration());
        } else {
            super.applyOverrideConfiguration(overrideConfiguration);
        }
    }

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
