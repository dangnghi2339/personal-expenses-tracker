package com.example.dack1.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View; // <- Thêm import này
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // <- Thêm import này (Nếu bạn có ProgressBar)
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
// import com.example.dack1.data.model.User; // <- Không cần import User trực tiếp ở đây nữa
import com.example.dack1.ui.viewmodel.UserViewModel;
import com.example.dack1.util.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private ProgressBar progressBar; // <- Thêm khai báo ProgressBar (đảm bảo ID là progressBar trong layout)
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel(); // <- Gọi hàm observe mới
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email); // Giữ nguyên ID gốc của bạn
        etPassword = findViewById(R.id.et_password); // Giữ nguyên ID gốc của bạn
        btnLogin = findViewById(R.id.btn_login); // Giữ nguyên ID gốc của bạn
        tvRegisterLink = findViewById(R.id.tv_register_link); // Giữ nguyên ID gốc của bạn
        progressBar = findViewById(R.id.progressBar); // <- Thêm dòng này, đảm bảo có ProgressBar với ID này trong activity_login.xml
        sessionManager = new SessionManager(this);

        // Ẩn ProgressBar ban đầu
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            // Không nên finish() ở đây, để người dùng có thể quay lại Login
            // finish(); // <- Xóa hoặc comment dòng này
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // --- THAY ĐỔI CHÍNH ---
        // Chỉ gọi hàm login trong ViewModel, không observe ở đây nữa
        userViewModel.login(email, password);
        // --- KẾT THÚC THAY ĐỔI ---
    }

    // --- THÊM HÀM MỚI ĐỂ OBSERVE VIEWMODEL ---
    private void observeViewModel() {
        userViewModel.getLoginResult().observe(this, result -> {
            if (result == null) return; // An toàn nếu LiveData chưa có giá trị

            switch (result.status) {
                case LOADING:
                    // Hiển thị ProgressBar, vô hiệu hóa nút Login
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    // Ẩn ProgressBar, kích hoạt lại nút
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    // Lưu session
                    // Cần đảm bảo model User có hàm getName() nếu bạn thêm trường name
                    sessionManager.createLoginSession((int)result.user.getId(), result.user.getEmail()); // Ép kiểu id sang int nếu cần
                    sessionManager.setUserName(result.user.getName());
                    // Thông báo và chuyển màn hình
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    // Xóa stack activity cũ để không quay lại Login khi nhấn back từ MainActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Kết thúc LoginActivity
                    break;
                case ERROR:
                    // Ẩn ProgressBar, kích hoạt lại nút
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    // Hiển thị lỗi
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show(); // Hiển thị thông báo lỗi từ ViewModel
                    break;
            }
        });
    }
    // --- KẾT THÚC HÀM MỚI ---
}