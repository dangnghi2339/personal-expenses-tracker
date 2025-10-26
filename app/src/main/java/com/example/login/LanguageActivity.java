package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class LanguageActivity extends BaseActivity {

    private void apply(String code) {
        Prefs.setLang(this, code);

        // Clear task và khởi động lại từ trang cá nhân (hoặc trang bạn muốn)
        Intent i = new Intent(this, IndividualActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        // Không animation để nhanh
        overridePendingTransition(0, 0);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language); // giữ nguyên layout của bạn

        // Tìm theo NHIỀU TÊN ID có thể có trong layout của bạn
        View rowEn = findByNames("rowEn", "langEn", "itemEn");
        View rowVi = findByNames("rowVi", "langVi", "itemVi");

        RadioButton rbEn = findByNames("rbEn", "radioEn");
        RadioButton rbVi = findByNames("rbVi", "radioVi");

        String cur = Prefs.getLang(this);
        if (rbEn != null) rbEn.setChecked("en".equals(cur));
        if (rbVi != null) rbVi.setChecked("vi".equals(cur));

        if (rowEn != null) rowEn.setOnClickListener(v -> apply("en"));
        if (rowVi != null) rowVi.setOnClickListener(v -> apply("vi"));
        if (rbEn  != null) rbEn .setOnClickListener(v -> apply("en"));
        if (rbVi  != null) rbVi .setOnClickListener(v -> apply("vi"));
    }
}
