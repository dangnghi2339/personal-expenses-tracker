package com.example.dack1.ui.view; // Đã sửa package

import android.os.Bundle;
// Import Switch nếu bạn dùng findViewById, không cần nếu dùng Binding
// import android.widget.Switch;

// Kế thừa từ BaseActivity
import com.example.dack1.R; // Đã sửa R file
import com.example.dack1.databinding.ActivityNotificationBinding; // Thêm import Binding
import com.example.dack1.util.SessionManager; // Thêm import SessionManager

// Kế thừa từ BaseActivity thay vì AppCompatActivity
public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding; // Khai báo Binding
    private SessionManager sessionManager; // Khai báo SessionManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng ViewBinding
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Lấy trạng thái hiện tại và đặt cho Switch
        boolean isNotifyEnabled = sessionManager.isNotifyEnabled(); // Đã sửa: dùng SessionManager
        binding.swNotify.setChecked(isNotifyEnabled);

        // Lắng nghe sự kiện thay đổi trạng thái Switch
        binding.swNotify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotifyEnabled(isChecked); // Đã sửa: dùng SessionManager
        });
    }
}