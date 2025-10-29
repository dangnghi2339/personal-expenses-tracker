package com.example.dack1.data.repository;

import android.app.Application; // <- Sẽ không dùng nữa nếu chỉ truyền Dao
import androidx.lifecycle.LiveData;

import com.example.dack1.data.local.dao.UserDao;
import com.example.dack1.data.local.database.AppDatabase;
import com.example.dack1.data.model.User;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt; // <- Thêm import BCrypt

/**
 * Repository for User data operations, including authentication with password hashing.
 */
public class UserRepository {

    private final UserDao userDao; // <- Thay vì Application, giữ tham chiếu đến UserDao

    /**
     * Constructor nhận vào UserDao.
     * @param userDao Data Access Object để tương tác với bảng User.
     */
    public UserRepository(UserDao userDao) {
        // AppDatabase db = AppDatabase.getDatabase(application); // <- Xóa dòng này
        // this.userDao = db.userDao(); // <- Xóa dòng này
        this.userDao = userDao; // <- Gán UserDao được truyền vào
    }

    /**
     * Đăng ký người dùng mới với mật khẩu được mã hóa.
     * @param name Tên người dùng.
     * @param email Email người dùng (sẽ được kiểm tra trùng lặp).
     * @param password Mật khẩu dạng chữ thuần (sẽ được mã hóa).
     * @return Đối tượng User mới nếu đăng ký thành công, null nếu email đã tồn tại.
     */
    public User registerUser(String name, String email, String password) {
        // Chạy kiểm tra và insert trên background thread do là I/O operation
        // Lưu ý: ViewModel đã gọi hàm này trên background thread,
        // nhưng để an toàn, vẫn có thể dùng Executor ở đây.
        // Tuy nhiên, để đơn giản, giả sử hàm này được gọi từ background.
        // Nếu không chắc chắn, hãy bọc các lệnh gọi userDao bằng AppDatabase.databaseWriteExecutor.execute()

        User existingUser = userDao.getUserByEmail(email); // Kiểm tra email tồn tại
        if (existingUser != null) {
            return null; // Email đã tồn tại
        }

        // Mã hóa mật khẩu bằng BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); // Độ phức tạp 12

        // Tạo đối tượng User mới với mật khẩu đã mã hóa
        User newUser = new User();
        newUser.setName(name); // Đảm bảo model User có setter này
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword); // Lưu mật khẩu đã mã hóa

        // Thêm user mới vào database (chạy trên background thread)
        // Dùng Executor của AppDatabase nếu không chắc chắn hàm này được gọi từ background
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(newUser);
        });


        // Trả về user vừa tạo (có thể cần đợi insert hoàn tất hoặc lấy lại từ DB)
        // Cách đơn giản nhất là lấy lại bằng email
        try {
            // Đợi một chút để insert có thể hoàn thành (không lý tưởng, chỉ là giải pháp tạm)
            // Cách tốt hơn là dùng cơ chế callback hoặc Coroutines/RxJava
            Thread.sleep(100); // Đợi 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return userDao.getUserByEmail(email);
    }

    /**
     * Xác thực người dùng bằng email và mật khẩu (kiểm tra với hash).
     * @param email Email người dùng nhập.
     * @param password Mật khẩu dạng chữ thuần người dùng nhập.
     * @return Đối tượng User nếu xác thực thành công, null nếu email không tồn tại hoặc sai mật khẩu.
     */
    public User loginUser(String email, String password) {
        // Lấy user từ database bằng email (chạy trên background thread)
        // Giả sử hàm này được ViewModel gọi từ background thread
        User user = userDao.getUserByEmail(email);

        // Kiểm tra user có tồn tại và mật khẩu có khớp không
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user; // Đăng nhập thành công
        }

        return null; // Email không tồn tại hoặc sai mật khẩu
    }


    /**
     * Tìm kiếm người dùng bằng email (trả về LiveData để observe).
     * Giữ lại hàm này nếu bạn cần observe thông tin user ở đâu đó.
     * @param email Email cần tìm.
     * @return LiveData chứa User hoặc null.
     */
    public LiveData<User> findByEmail(String email) {
        // return userDao.findUserByEmailLiveData(email); // <- Sửa dòng này
        return userDao.findByEmail(email); // <- Thành dòng này
    }


    /**
     * Hàm này không còn phù hợp vì kiểm tra mật khẩu đã chuyển sang loginUser.
     * Cần xóa hàm tương ứng trong UserDao.
     */
    // public LiveData<User> findByEmailAndPassword(String email, String password) { // <- XÓA HÀM NÀY
    //    return userDao.findByEmailAndPassword(email, password);
    // }

    /**
     * Hàm insert cơ bản. Nên sử dụng registerUser để đảm bảo mật khẩu được mã hóa.
     * Có thể giữ lại nếu dùng cho mục đích khác hoặc xóa đi.
     * @param user User object.
     */
    public void insert(User user) {
        // CẢNH BÁO: Hàm này không mã hóa mật khẩu. Chỉ dùng nếu mật khẩu đã được mã hóa trước đó.
        // Nên ưu tiên dùng registerUser.
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

}