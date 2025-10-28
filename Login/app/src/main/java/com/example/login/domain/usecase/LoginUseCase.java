package com.example.login.domain.usecase;

// Import interface AuthRepository
import com.example.login.domain.repository.AuthRepository;
// --- ĐÃ SỬA LỖI IMPORT TẠI ĐÂY ---
// Import enum AuthResult từ BÊN TRONG AuthRepository interface
import com.example.login.domain.repository.AuthRepository.AuthResult;
// --- KẾT THÚC SỬA LỖI ---

/**
 * Use Case (Trường hợp sử dụng) cho chức năng đăng nhập.
 * Chứa logic nghiệp vụ cụ thể liên quan đến việc đăng nhập.
 * Phụ thuộc vào AuthRepository interface để tương tác với tầng Data.
 */
public class LoginUseCase {

    private final AuthRepository authRepository;

    // Constructor nhận vào một implementation của AuthRepository
    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Thực thi logic đăng nhập.
     * @param email Email người dùng nhập.
     * @param password Mật khẩu người dùng nhập.
     * @return AuthResult kết quả của việc đăng nhập.
     */
    public AuthResult execute(String email, String password) {
        // Có thể thêm logic nghiệp vụ ở đây nếu cần, ví dụ:
        // - Kiểm tra định dạng email/password phức tạp hơn (tầng Domain có thể làm).
        // - Ghi log sự kiện đăng nhập.

        // Gọi phương thức login của Repository
        return authRepository.login(email, password);
    }
}