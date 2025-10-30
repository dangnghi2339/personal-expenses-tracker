package com.example.dack1.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View; // <- Thêm import
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // <- Thêm import
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
// import com.example.dack1.data.model.User; // <- Không cần import User trực tiếp
import com.example.dack1.ui.viewmodel.UserViewModel;
import com.example.dack1.util.SessionManager;

public class RegisterActivity extends AppCompatActivity {
    // --- THÊM EditText cho Name ---
    private EditText etName; // <- Thêm dòng này
    // --- KẾT THÚC THÊM ---
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressBar progressBar; // <- Thêm ProgressBar
    private UserViewModel userViewModel;
    private SessionManager sessionManager; // <- Giữ lại SessionManager

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel(); // <- Gọi hàm observe mới
    }

    private void initViews() {
        // --- THÊM findViewById cho Name ---
        etName = findViewById(R.id.et_name); // <- Thêm dòng này (Đảm bảo ID là et_name trong layout)
        // --- KẾT THÚC THÊM ---
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
        progressBar = findViewById(R.id.progressBar); // <- Thêm ProgressBar (Đảm bảo ID là progressBar trong layout)
        sessionManager = new SessionManager(this); // <- Khởi tạo SessionManager

        // Ẩn ProgressBar ban đầu
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
        tvLoginLink.setOnClickListener(v -> {
            // Chuyển về Login, không cần finish RegisterActivity ngay
            startActivity(new Intent(this, LoginActivity.class));
            // finish(); // Có thể không cần finish ở đây
        });
    }

    // --- SỬA LẠI HOÀN TOÀN HÀM NÀY ---
    private void handleRegister() {
        // Lấy dữ liệu từ các EditText
        String name = etName.getText().toString().trim(); // <- Lấy name
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu nhập
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) { // <- Thêm kiểm tra name
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) { // Hoặc độ dài mật khẩu tối thiểu bạn muốn
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi hàm register mới trong ViewModel
        userViewModel.register(name, email, password);
    }
    // --- KẾT THÚC SỬA ---

    // --- THÊM HÀM MỚI ĐỂ OBSERVE VIEWMODEL ---
    private void observeViewModel() {
        userViewModel.getRegisterResult().observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    // Đăng ký thành công, có thể tự động đăng nhập hoặc chuyển về màn hình Login
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();

                    // Tùy chọn 1: Chuyển về Login để người dùng đăng nhập lại
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa stack về Login
                    startActivity(loginIntent);
                    finish(); // Đóng RegisterActivity

                    // Tùy chọn 2: Tự động đăng nhập (nếu muốn)
                    /*
                    if (sessionManager != null && result.user != null) {
                         sessionManager.createLoginSession((int)result.user.getId(), result.user.getEmail());
                         Intent mainIntent = new Intent(this, MainActivity.class);
                         mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(mainIntent);
                         finish(); // Đóng RegisterActivity
                    }
                    */
                    break;
                case ERROR:
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    // Hiển thị lỗi (ví dụ: email đã tồn tại)
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
    // --- KẾT THÚC HÀM MỚI ---
}