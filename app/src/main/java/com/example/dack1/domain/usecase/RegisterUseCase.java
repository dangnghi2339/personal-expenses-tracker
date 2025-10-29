package com.example.dack1.domain.usecase; // <- Sửa package

import com.example.dack1.data.model.User; // <- Sửa import model
import com.example.dack1.data.repository.UserRepository; // <- Sửa import repository

/**
 * Use case để xử lý logic đăng ký người dùng mới.
 */
public class RegisterUseCase {

    private final UserRepository userRepository; // <- Dùng UserRepository của PET

    /**
     * Constructor nhận vào UserRepository.
     * @param userRepository Repository để tương tác với dữ liệu người dùng.
     */
    public RegisterUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Thực thi use case đăng ký.
     * @param name Tên người dùng.
     * @param email Email người dùng.
     * @param password Mật khẩu người dùng.
     * @return Đối tượng User mới được tạo nếu đăng ký thành công, null nếu thất bại (ví dụ: email đã tồn tại).
     */
    public User execute(String name, String email, String password) {
        // Gọi phương thức registerUser từ UserRepository
        // (Lưu ý: UserRepository sẽ xử lý mã hóa mật khẩu)
        return userRepository.registerUser(name, email, password); // <- Gọi hàm registerUser
    }
}