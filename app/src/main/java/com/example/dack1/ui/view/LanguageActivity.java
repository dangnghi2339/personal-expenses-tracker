package com.example.dack1.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.example.dack1.R; // Đã sửa R file
import com.example.dack1.databinding.ActivityLanguageBinding; // Thêm import Binding
import com.example.dack1.util.SessionManager; // Thêm import SessionManager
public class LanguageActivity extends BaseActivity {
    private ActivityLanguageBinding binding; // Khai báo Binding
    private SessionManager sessionManager; // Khai báo SessionManager

    /**
     * Lưu ngôn ngữ mới và khởi động lại MainActivity
     * @param code Mã ngôn ngữ ("en" hoặc "vi")
     */
    private void applyLanguage(String code) {
        sessionManager.setLanguage(code); // Đã sửa: dùng SessionManager

        // Quay về MainActivity (không phải IndividualActivity)
        Intent i = new Intent(this, MainActivity.class);
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

        // Sử dụng ViewBinding
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Lấy ngôn ngữ hiện tại
        String currentLanguage = sessionManager.getLanguage(); // Đã sửa: dùng SessionManager

        // Đánh dấu RadioButton tương ứng
        if ("en".equals(currentLanguage)) {
            binding.rbEn.setChecked(true);
        } else {
            binding.rbVi.setChecked(true); // Mặc định là 'vi' nếu không phải 'en'
        }

        // Gán sự kiện click (dùng binding)
        binding.rowEn.setOnClickListener(v -> applyLanguage("en"));
        binding.rowVi.setOnClickListener(v -> applyLanguage("vi"));
        binding.rbEn.setOnClickListener(v -> applyLanguage("en"));
        binding.rbVi.setOnClickListener(v -> applyLanguage("vi"));

        // (Không cần các hàm findByNames nữa)
    }

    // (Xóa các hàm findByNames nếu có)
}
