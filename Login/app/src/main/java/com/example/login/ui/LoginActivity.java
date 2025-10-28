package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.login.R;
import com.example.login.databinding.ActivityLoginBinding;
import com.example.login.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        // Các ID này đã được xác nhận là đúng từ file XML của bạn
        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.etEmail.getText() == null ? "" : binding.etEmail.getText().toString().trim();
            String pass = binding.etPassword.getText() == null ? "" : binding.etPassword.getText().toString();
            viewModel.onLoginClicked(email, pass);
        });

        binding.tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng quên mật khẩu (stub)", Toast.LENGTH_SHORT).show());


        // --- ĐÃ SỬA LỖI ---
        // Dòng này sẽ hoạt động vì ID "tvGoRegister" đã tồn tại trong XML
        binding.tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        // --- KẾT THÚC SỬA LỖI ---
    }

    private void observeViewModel() {
        viewModel.uiState.observe(this, state -> {
            binding.etEmail.setError(null);
            binding.etPassword.setError(null);

            switch (state) {
                case SUCCESS:
                    goToIndividualActivity();
                    viewModel.onStateHandled();
                    break;
                case ERROR_INVALID_EMAIL:
                    binding.etEmail.setError("Email không hợp lệ");
                    binding.etEmail.requestFocus();
                    break;
                case ERROR_EMPTY_PASSWORD:
                    binding.etPassword.setError("Vui lòng nhập mật khẩu");
                    binding.etPassword.requestFocus();
                    break;
                case ERROR_NOT_REGISTERED:
                    Toast.makeText(this, "Bạn chưa đăng ký tài khoản.", Toast.LENGTH_LONG).show();
                    viewModel.onStateHandled();
                    break;
                case ERROR_WRONG_CREDENTIALS:
                    Toast.makeText(this, "Email hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    viewModel.onStateHandled();
                    break;
                case IDLE:
                    // Không làm gì
                    break;
            }
        });
    }

    private void goToIndividualActivity() {
        Intent i = new Intent(this, IndividualActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}