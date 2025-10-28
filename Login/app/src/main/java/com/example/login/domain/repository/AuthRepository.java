package com.example.login.domain.repository;

/**
 * Interface này định nghĩa "hợp đồng" cho Repository xử lý các tác vụ
 * liên quan đến xác thực người dùng (đăng nhập, đăng ký).
 * Nó nằm trong tầng Domain, tách biệt logic nghiệp vụ khỏi
 * chi tiết triển khai của tầng Data.
 */
public interface AuthRepository {

    /**
     * Enum định nghĩa các kết quả có thể trả về từ các hoạt động xác thực.
     * Được đặt bên trong interface để thể hiện sự liên kết chặt chẽ.
     */
    enum AuthResult {
        SUCCESS,                // Hành động thành công
        FAILURE_NOT_REGISTERED, // Lỗi: Người dùng chưa đăng ký
        FAILURE_WRONG_CREDENTIALS // Lỗi: Thông tin đăng nhập (email/mật khẩu) không đúng
    }

    /**
     * Thực hiện hành động đăng nhập.
     * @param email Email do người dùng cung cấp.
     * @param password Mật khẩu do người dùng cung cấp.
     * @return AuthResult cho biết kết quả của việc đăng nhập.
     */
    AuthResult login(String email, String password);

    /**
     * Thực hiện hành động đăng ký người dùng mới.
     * Trong trường hợp này, không cần trả về kết quả phức tạp,
     * vì đăng ký thành công sẽ tự động đăng nhập (theo logic cũ).
     * @param username Tên người dùng mới.
     * @param email Email người dùng mới.
     * @param password Mật khẩu người dùng mới.
     */
    void register(String username, String email, String password);
}