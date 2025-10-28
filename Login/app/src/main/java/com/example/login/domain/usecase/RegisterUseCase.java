package com.example.login.domain.usecase;

// --- ĐÃ THÊM IMPORT CÒN THIẾU TẠI ĐÂY ---
import com.example.login.domain.repository.AuthRepository;
// --- KẾT THÚC THÊM IMPORT ---

/**
 * Use Case (Trường hợp sử dụng) cho chức năng đăng ký.
 * Chứa logic nghiệp vụ cụ thể liên quan đến việc đăng ký.
 * Phụ thuộc vào AuthRepository interface để tương tác với tầng Data.
 */
public class RegisterUseCase {

    private final AuthRepository authRepository;

    // Constructor nhận vào một implementation của AuthRepository
    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Thực thi logic đăng ký.
     * @param username Tên người dùng mới.
     * @param email Email người dùng mới.
     * @param password Mật khẩu người dùng mới.
     */
    public void execute(String username, String email, String password) {
        // Có thể thêm logic nghiệp vụ ở đây nếu cần, ví dụ:
        // - Chuẩn hóa username trước khi lưu.
        // - Gửi email xác thực (logic phức tạp hơn).

        // Gọi phương thức register của Repository
        authRepository.register(username, email, password);
        // Do logic hiện tại tự động đăng nhập sau khi đăng ký,
        // nên không cần trả về kết quả ở đây.
    }
}