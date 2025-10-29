package com.example.dack1.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // <-- THÊM IMPORT NÀY
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
import com.example.dack1.ui.viewmodel.UserViewModel;
import com.example.dack1.util.SessionManager;

// === CÁC IMPORT MỚI CHO GOOGLE SIGN-IN ===
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
// ==========================================

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    // === CÁC BIẾN MỚI CHO GOOGLE SIGN-IN ===
    private ImageButton btnGoogle; // Nút Google từ layout của bạn
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001; // Mã yêu cầu cho Google Sign-In
    // =====================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        configureGoogleSignIn();
        initViews();
        initViewModel(); // Đảm bảo userViewModel được khởi tạo
        setupClickListeners();
        observeViewModel();

        // Cấu hình Google Sign-In
        configureGoogleSignIn();

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    /**
     * Hàm mới: Cấu hình GoogleSignInClient
     */
    private void configureGoogleSignIn() {
        // Cấu hình Google Sign-In để yêu cầu ID token và email
        // Rất quan trọng: Phải lấy web client ID từ file google-services.json
        // (Bạn không cần nhập thủ công, chỉ cần gọi R.string.default_web_client_id
        // mà Firebase tự động thêm vào)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        progressBar = findViewById(R.id.progressBar);
        sessionManager = new SessionManager(this);

        // Ánh xạ nút Google
        btnGoogle = findViewById(R.id.btnGoogle); //

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
        });

        // === THÊM SỰ KIỆN CLICK CHO NÚT GOOGLE ===
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        // ======================================
    }

    /**
     * Hàm mới: Bắt đầu quá trình đăng nhập Google
     */
    private void signInWithGoogle() {
        showLoading(true); // Hiển thị ProgressBar
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Hàm mới: Xử lý kết quả trả về từ Google Sign-In
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kết quả trả về từ việc khởi chạy Intent của GoogleSignInClient.getSignInIntent()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập Google thành công, bây giờ xác thực với Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    showLoading(false);
                    Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Đăng nhập Google thất bại
                showLoading(false);
                Log.w("LoginActivity", "Google sign in failed", e);
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Hàm mới: Xác thực với Firebase sử dụng ID Token của Google
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            // === GỌI VIEWMODEL ĐỂ LƯU VÀO FIRESTORE ===
                            // Gọi hàm mới trong UserViewModel
                            userViewModel.checkAndSaveGoogleUser(user);
                            // =========================================

                            // Lưu session và chuyển màn hình (như cũ)
                            sessionManager.createLoginSession((int) user.hashCode(), user.getEmail());
                            sessionManager.setUserName(user.getDisplayName());

                            showLoading(false);
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            // Trường hợp hiếm gặp user null sau khi signIn thành công
                            showLoading(false);
                            Toast.makeText(this, "Lỗi: Không lấy được thông tin người dùng.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Xác thực Firebase thất bại (như cũ)
                        showLoading(false);
                        Toast.makeText(this, "Lỗi xác thực Firebase: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Hàm này giữ nguyên cho đăng nhập bằng email/pass
    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.login(email, password);
    }

    // Observer này vẫn dùng cho đăng nhập bằng email/pass
    private void observeViewModel() {
        userViewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    // Lưu session
                    sessionManager.createLoginSession((int)result.user.getId(), result.user.getEmail());
                    sessionManager.setUserName(result.user.getName());

                    showLoading(false);
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                    break;
                case ERROR:
                    showLoading(false);
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    /**
     * Hàm mới: Hiển thị/ẩn ProgressBar và vô hiệu hóa các nút
     */
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!isLoading);
        btnGoogle.setEnabled(!isLoading);
    }

    /**
     * Hàm mới: Chuyển đến MainActivity
     */
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}