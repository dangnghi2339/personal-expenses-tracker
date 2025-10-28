package com.example.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.login.databinding.ActivityRegisterBinding;
import com.example.login.viewmodel.RegisterViewModel;
import com.example.login.ui.BaseActivity; // <-- Đảm bảo import đúng
import com.example.login.R;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        // --- ĐÃ SỬA LỖI TẠI ĐÂY ---
        // Sử dụng đúng ID từ file activity_register.xml của bạn
        // (etUsernameR, etEmailR, etPassR, etConfirmR)

        binding.btnSignUp.setOnClickListener(v -> {
            // Lấy dữ liệu từ View
            String username = binding.etUsernameR.getText() == null ? "" : binding.etUsernameR.getText().toString().trim(); // Sửa ID
            String email = binding.etEmailR.getText() == null ? "" : binding.etEmailR.getText().toString().trim();       // Sửa ID
            String pass = binding.etPassR.getText() == null ? "" : binding.etPassR.getText().toString();         // Sửa ID
            String conf = binding.etConfirmR.getText() == null ? "" : binding.etConfirmR.getText().toString();     // Sửa ID

            viewModel.onRegisterClicked(username, email, pass, conf);
        });

        // ID 'tvAlready' đã đúng
        binding.tvAlready.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void observeViewModel() {
        viewModel.uiState.observe(this, state -> {
            // --- ĐÃ SỬA LỖI TẠI ĐÂY ---
            // Xóa các lỗi cũ (sử dụng đúng ID)
            binding.etUsernameR.setError(null); // Sửa ID
            binding.etEmailR.setError(null);    // Sửa ID
            binding.etPassR.setError(null);     // Sửa ID
            binding.etConfirmR.setError(null);  // Sửa ID

            switch (state) {
                case SUCCESS:
                    Toast.makeText(this, "Đăng ký & đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    goToIndividualActivity();
                    viewModel.onStateHandled();
                    break;
                case ERROR_EMPTY_USERNAME:
                    binding.etUsernameR.setError("Username không được để trống"); // Sửa ID
                    binding.etUsernameR.requestFocus();                          // Sửa ID
                    break;
                case ERROR_INVALID_EMAIL:
                    binding.etEmailR.setError("Email không hợp lệ");             // Sửa ID
                    binding.etEmailR.requestFocus();                             // Sửa ID
                    break;
                case ERROR_PASSWORD_TOO_SHORT:
                    binding.etPassR.setError("Mật khẩu phải ≥ 6 ký tự");        // Sửa ID
                    binding.etPassR.requestFocus();                              // Sửa ID
                    break;
                case ERROR_PASSWORD_MISMATCH:
                    binding.etConfirmR.setError("Mật khẩu xác nhận không khớp"); // Sửa ID
                    binding.etConfirmR.requestFocus();                           // Sửa ID
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