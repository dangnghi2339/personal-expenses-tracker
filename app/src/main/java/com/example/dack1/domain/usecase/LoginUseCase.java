package com.example.dack1.domain.usecase;

// Import interface AuthRepository

import com.example.dack1.data.repository.UserRepository;
import com.example.dack1.data.model.User;
//import com.example.dack1.domain.repository.AuthRepository.AuthResult;
// --- KẾT THÚC SỬA LỖI ---

/**
 * Use Case (Trường hợp sử dụng) cho chức năng đăng nhập.
 * Chứa logic nghiệp vụ cụ thể liên quan đến việc đăng nhập.
 * Phụ thuộc vào AuthRepository interface để tương tác với tầng Data.
 */
public class LoginUseCase {

    private final UserRepository userRepository;

    // Constructor nhận vào một implementation của AuthRepository
    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Thực thi logic đăng nhập.
     * @param email Email người dùng nhập.
     * @param password Mật khẩu người dùng nhập.
     * @return AuthResult kết quả của việc đăng nhập.
     */
    public User execute(String email, String password) {
        // Gọi phương thức loginUser từ UserRepository
        return userRepository.loginUser(email, password); // <- Gọi hàm loginUser
    }
}