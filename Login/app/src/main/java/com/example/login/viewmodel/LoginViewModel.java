package com.example.login.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// --- ĐÃ SỬA LỖI IMPORTS ---
import com.example.login.domain.usecase.LoginUseCase;
import com.example.login.domain.repository.AuthRepository;
// Import AuthResult từ BÊN TRONG AuthRepository
import com.example.login.domain.repository.AuthRepository.AuthResult;
// Import Implementation Repository (chỉ để khởi tạo)
import com.example.login.data.repository.AuthRepositoryImpl;
// --- KẾT THÚC SỬA LỖI ---

public class LoginViewModel extends AndroidViewModel {

    // Giờ ViewModel phụ thuộc vào Use Case
    private final LoginUseCase loginUseCase;

    // Định nghĩa lại UiState cho phù hợp (hoặc giữ nguyên nếu đã đủ)
    public enum LoginUiState {
        IDLE,
        SUCCESS,
        ERROR_INVALID_EMAIL,
        ERROR_EMPTY_PASSWORD,
        ERROR_NOT_REGISTERED,
        ERROR_WRONG_CREDENTIALS
    }

    private MutableLiveData<LoginUiState> _uiState = new MutableLiveData<>(LoginUiState.IDLE);
    public LiveData<LoginUiState> uiState = _uiState;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        // --- Dependency Injection thủ công (Đã sửa) ---
        // 1. Tạo instance của Repository Implementation
        //    (Lưu ý: Không khai báo kiểu là AuthRepositoryImpl mà là interface AuthRepository)
        AuthRepository repository = new AuthRepositoryImpl(application);
        // 2. Tạo instance của Use Case, truyền Repository interface vào
        this.loginUseCase = new LoginUseCase(repository);
        // --- Kết thúc DI ---
    }

    public void onLoginClicked(String email, String password) {
        // Bước 1: Validation (vẫn giữ nguyên)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.setValue(LoginUiState.ERROR_INVALID_EMAIL);
            return;
        }
        if (password.isEmpty()) {
            _uiState.setValue(LoginUiState.ERROR_EMPTY_PASSWORD);
            return;
        }

        // Bước 2: Gọi Use Case (thay vì Repository)
        AuthResult result = loginUseCase.execute(email, password); // Gọi Use Case

        // Bước 3: Cập nhật UI State dựa trên kết quả từ Use Case
        switch (result) {
            case SUCCESS:
                _uiState.setValue(LoginUiState.SUCCESS);
                break;
            case FAILURE_NOT_REGISTERED:
                _uiState.setValue(LoginUiState.ERROR_NOT_REGISTERED);
                break;
            case FAILURE_WRONG_CREDENTIALS:
                _uiState.setValue(LoginUiState.ERROR_WRONG_CREDENTIALS);
                break;
        }
    }

    public void onStateHandled() {
        _uiState.setValue(LoginUiState.IDLE);
    }
}