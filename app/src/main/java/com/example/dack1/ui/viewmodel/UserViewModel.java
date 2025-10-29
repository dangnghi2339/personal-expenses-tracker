package com.example.dack1.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <- Thêm import

import com.example.dack1.data.local.dao.UserDao; // <- Thêm import
import com.example.dack1.data.local.database.AppDatabase; // <- Thêm import
import com.example.dack1.data.model.User;
import com.example.dack1.data.repository.UserRepository;
import com.example.dack1.domain.usecase.LoginUseCase; // <- Thêm import
import com.example.dack1.domain.usecase.RegisterUseCase; // <- Thêm import

import java.util.List; // <- Thêm import (nếu cần mGetAllUser)
import java.util.concurrent.ExecutorService; // <- Thêm import
import java.util.concurrent.Executors; // <- Thêm import
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel for User authentication operations using Use Cases.
 */
public class UserViewModel extends AndroidViewModel {

    // --- KHAI BÁO BIẾN ---
    private final UserRepository userRepository; // Giữ lại UserRepository
    private final UserDao userDao; // Cần UserDao để khởi tạo UserRepository
    private final LoginUseCase loginUseCase; // Thêm LoginUseCase
    private final RegisterUseCase registerUseCase; // Thêm RegisterUseCase
    private final ExecutorService executorService; // Thêm ExecutorService (hoặc dùng Coroutines nếu là Kotlin)

    // LiveData cho kết quả Login
    private final MutableLiveData<AuthResult> _loginResult = new MutableLiveData<>();
    public LiveData<AuthResult> getLoginResult() { return _loginResult; }

    // LiveData cho kết quả Register
    private final MutableLiveData<AuthResult> _registerResult = new MutableLiveData<>();
    public LiveData<AuthResult> getRegisterResult() { return _registerResult; }

    // LiveData để lấy tất cả user (nếu cần giữ lại)
    LiveData<List<User>> mGetAllUser; // Giữ lại nếu cần

    /**
     * Hàm mới: Gọi UserRepository để lưu người dùng Google vào Firestore.
     * @param user FirebaseUser vừa đăng nhập thành công bằng Google.
     */
    public void checkAndSaveGoogleUser(FirebaseUser user) {
        // Chỉ cần gọi hàm trong Repository trên background thread
        executorService.execute(() -> { // Sử dụng ExecutorService đã có
            userRepository.saveGoogleUserToFirestoreIfNotExists(user);
        });
    }
    // --- CONSTRUCTOR ---
    public UserViewModel(@NonNull Application application) {
        super(application);
        // Khởi tạo Database và Dao
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        // Khởi tạo Repository với Dao
        userRepository = new UserRepository(userDao);
        // Khởi tạo UseCases với Repository
        loginUseCase = new LoginUseCase(userRepository);
        registerUseCase = new RegisterUseCase(userRepository);
        // Khởi tạo ExecutorService
        executorService = Executors.newSingleThreadExecutor();
        // Khởi tạo LiveData khác (nếu cần)
        mGetAllUser = userDao.getAllUser(); // Giữ lại nếu cần
    }

    // --- LỚP TRẠNG THÁI UI (AuthResult) ---
    public static class AuthResult {
        public enum Status { LOADING, SUCCESS, ERROR }
        public final Status status;
        public final User user; // Dữ liệu khi thành công
        public final String message; // Thông báo lỗi

        private AuthResult(Status status, User user, String message) {
            this.status = status;
            this.user = user;
            this.message = message;
        }

        public static AuthResult loading() { return new AuthResult(Status.LOADING, null, null); }
        public static AuthResult success(User user) { return new AuthResult(Status.SUCCESS, user, null); }
        public static AuthResult error(String msg) { return new AuthResult(Status.ERROR, null, msg); }
    }


    // --- HÀM LOGIN ---
    public void login(String email, String password) {
        _loginResult.postValue(AuthResult.loading()); // Báo trạng thái loading
        executorService.execute(() -> { // Sử dụng ExecutorService
            try {
                // Gọi LoginUseCase
                User user = loginUseCase.execute(email, password);
                if (user != null) {
                    _loginResult.postValue(AuthResult.success(user)); // Thành công
                } else {
                    // Thất bại (sai email/pass, UseCase/Repo đã check BCrypt)
                    _loginResult.postValue(AuthResult.error("Email hoặc mật khẩu không đúng"));
                }
            } catch (Exception e) {
                // Xử lý lỗi khác (ví dụ: lỗi database...)
                _loginResult.postValue(AuthResult.error("Đã xảy ra lỗi: " + e.getMessage()));
            }
        });
    }

    // --- HÀM REGISTER ---
    public void register(String name, String email, String password) {
        _registerResult.postValue(AuthResult.loading()); // Báo trạng thái loading
        executorService.execute(() -> { // Sử dụng ExecutorService
            try {
                // Gọi RegisterUseCase (UseCase/Repo đã mã hóa pass)
                User newUser = registerUseCase.execute(name, email, password);
                if (newUser != null) {
                    _registerResult.postValue(AuthResult.success(newUser)); // Thành công
                } else {
                    // Thất bại (thường do email đã tồn tại)
                    _registerResult.postValue(AuthResult.error("Email đã được đăng ký hoặc có lỗi xảy ra"));
                }
            } catch (Exception e) {
                _registerResult.postValue(AuthResult.error("Đã xảy ra lỗi: " + e.getMessage()));
            }
        });
    }

    // --- CÁC HÀM CŨ (Xóa hoặc giữ lại nếu cần) ---
    /*
     * Các hàm findByEmail, findByEmailAndPassword, và insert cũ không còn cần thiết
     * cho luồng login/register nữa vì đã được xử lý qua UseCase và các hàm mới.
     * Bạn có thể xóa chúng nếu không dùng ở đâu khác.
     * Nếu bạn cần hàm insert User cho mục đích khác, có thể giữ lại hàm insert.
     */
    // public LiveData<User> findByEmail(String email) { return repository.findByEmail(email); } // Xóa
    // public LiveData<User> findByEmailAndPassword(String email, String password) { return repository.findByEmailAndPassword(email, password); } // Xóa
    // public void insert(User user) { repository.insert(user); } // Xóa hoặc giữ nếu cần cho việc khác

    // Giữ lại hàm này nếu bạn dùng nó ở đâu đó để hiển thị danh sách user
    public LiveData<List<User>> getAllUser() {
        return mGetAllUser;
    }

    // --- Cleanup ExecutorService ---
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // Quan trọng: Dừng ExecutorService khi ViewModel bị hủy
    }
}