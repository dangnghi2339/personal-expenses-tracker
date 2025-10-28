package com.example.login.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// --- ĐÃ SỬA LỖI IMPORTS ---
import com.example.login.domain.usecase.RegisterUseCase;
import com.example.login.domain.repository.AuthRepository;
// Import Implementation Repository (chỉ để khởi tạo)
import com.example.login.data.repository.AuthRepositoryImpl;
// --- KẾT THÚC SỬA LỖI ---


public class RegisterViewModel extends AndroidViewModel {

    // Phụ thuộc vào RegisterUseCase
    private final RegisterUseCase registerUseCase;

    // Giữ nguyên UiState
    public enum RegisterUiState {
        IDLE,
        SUCCESS,
        ERROR_EMPTY_USERNAME,
        ERROR_INVALID_EMAIL,
        ERROR_PASSWORD_TOO_SHORT,
        ERROR_PASSWORD_MISMATCH
    }

    private MutableLiveData<RegisterUiState> _uiState = new MutableLiveData<>(RegisterUiState.IDLE);
    public LiveData<RegisterUiState> uiState = _uiState;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        // --- Dependency Injection thủ công (Đã sửa) ---
        // 1. Tạo instance của Repository Implementation
        //    (Lưu ý: Không khai báo kiểu là AuthRepositoryImpl mà là interface AuthRepository)
        AuthRepository repository = new AuthRepositoryImpl(application);
        // 2. Tạo instance của Use Case, truyền Repository interface vào
        this.registerUseCase = new RegisterUseCase(repository);
        // --- Kết thúc DI ---
    }

    public void onRegisterClicked(String username, String email, String password, String confirmPassword) {

        // Bước 1: Validation (vẫn giữ nguyên)
        if (username.isEmpty()) {
            _uiState.setValue(RegisterUiState.ERROR_EMPTY_USERNAME);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.setValue(RegisterUiState.ERROR_INVALID_EMAIL);
            return;
        }
        if (password.length() < 6) {
            _uiState.setValue(RegisterUiState.ERROR_PASSWORD_TOO_SHORT);
            return;
        }
        if (!password.equals(confirmPassword)) {
            _uiState.setValue(RegisterUiState.ERROR_PASSWORD_MISMATCH);
            return;
        }

        // Bước 2: Gọi Use Case (thay vì Repository)
        registerUseCase.execute(username, email, password); // Gọi Use Case

        // Bước 3: Cập nhật UI State (vẫn báo SUCCESS)
        _uiState.setValue(RegisterUiState.SUCCESS);
    }

    public void onStateHandled() {
        _uiState.setValue(RegisterUiState.IDLE);
    }
}