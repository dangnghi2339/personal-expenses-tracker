// File: com/example/login/ui/IndividualActivity.java
package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.login.R;
// Import Prefs từ package 'datasource'
import com.example.login.data.datasource.Prefs;
// 1. Import ViewBinding (RẤT QUAN TRỌNG)
import com.example.login.databinding.ActivityIndividualBinding;

// Kế thừa BaseActivity từ package 'ui'
public class IndividualActivity extends BaseActivity {

    // 2. Khai báo biến binding
    private ActivityIndividualBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 3. Sử dụng ViewBinding (thay cho setContentView(R.layout...))
        binding = ActivityIndividualBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 4. Lấy dữ liệu từ Prefs và gán vào View (ĐÃ SỬA LỖI)

        // Lấy tên từ Prefs
        String name = Prefs.getName(this);

        // Gán vào TextView có ID 'tvName' (đây là ID đúng)
        binding.tvName.setText(name.isEmpty() ? "User" : name);

        // Gán cả email vào TextView 'tvEmail'
        binding.tvEmail.setText(Prefs.getEmail(this));

        // 5. Gán sự kiện click cho hàng Logout (ĐÃ SỬA LỖI)
        // ID đúng trong XML là 'rowLogout'
        binding.rowLogout.setOnClickListener(v -> doLogout());
    }

    private void doLogout() {
        // Xóa session
        Prefs.clearSession(this);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Quay về màn hình Login
        Intent i = new Intent(this, LoginActivity.class); // Về LoginActivity trong 'ui'
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}