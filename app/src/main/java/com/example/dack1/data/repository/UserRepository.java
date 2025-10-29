package com.example.dack1.data.repository;

import android.app.Application; // <- Sẽ không dùng nữa nếu chỉ truyền Dao
import androidx.lifecycle.LiveData;

import com.example.dack1.data.local.dao.UserDao;
import com.example.dack1.data.local.database.AppDatabase;
import com.example.dack1.data.model.User;

import java.util.HashMap;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest; // Để cập nhật tên hiển thị
import com.google.android.gms.tasks.Task; // Cho các tác vụ bất đồng bộ của Firebase
import com.google.android.gms.tasks.Tasks; // Để đợi tác vụ hoàn thành (dùng cẩn thận)
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import android.util.Log; // Để ghi log lỗi
/**
 * Repository for User data operations, including authentication with password hashing.
 */
public class UserRepository {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserDao userDao; // <- Thay vì Application, giữ tham chiếu đến UserDao

    private final FirebaseAuth mAuth;
    private static final String TAG = "UserRepository";
    /**
     * Constructor nhận vào UserDao.
     * @param userDao Data Access Object để tương tác với bảng User.
     */
    public UserRepository(UserDao userDao) {
        // AppDatabase db = AppDatabase.getDatabase(application); // <- Xóa dòng này
        // this.userDao = db.userDao(); // <- Xóa dòng này
        this.userDao = userDao;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Đăng ký người dùng mới với mật khẩu được mã hóa.
     * @param name Tên người dùng.
     * @param email Email người dùng (sẽ được kiểm tra trùng lặp).
     * @param password Mật khẩu dạng chữ thuần (sẽ được mã hóa).
     * @return Đối tượng User mới nếu đăng ký thành công, null nếu email đã tồn tại.
     */
    public User registerUser(String name, String email, String password) {
        try {
            // Bước 1: Tạo user (như cũ)
            Task<com.google.firebase.auth.AuthResult> registerTask = mAuth.createUserWithEmailAndPassword(email, password);
            com.google.firebase.auth.AuthResult authResult = Tasks.await(registerTask);
            FirebaseUser firebaseUser = authResult.getUser();

            if (firebaseUser != null) {
                // Bước 2: Cập nhật profile (như cũ)
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                Task<Void> updateProfileTask = firebaseUser.updateProfile(profileUpdates);
                Tasks.await(updateProfileTask);

                // *** PHẦN MỚI: LƯU THÔNG TIN USER VÀO FIRESTORE ***
                String uid = firebaseUser.getUid(); // Lấy UID (ID duy nhất) của user

                // Tạo một Map để lưu trữ
                Map<String, Object> userDocument = new HashMap<>();
                userDocument.put("name", name);
                userDocument.put("email", email);
                userDocument.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp()); // Thêm dấu thời gian

                // Ghi vào collection "users" với ID document là UID của user
                // Dùng Tasks.await để đảm bảo việc này hoàn tất trước khi trả về
                Tasks.await(db.collection("users").document(uid).set(userDocument));

                Log.d(TAG, "User document đã được thêm vào Firestore với UID: " + uid);
                // *** KẾT THÚC PHẦN MỚI ***

                // Trả về đối tượng User (như cũ)
                User newUser = new User();
                newUser.setEmail(firebaseUser.getEmail());
                newUser.setName(firebaseUser.getDisplayName());
                newUser.setPassword(null);
                return newUser;
            } else {
                Log.w(TAG, "createUserWithEmail:failure - User is null after creation");
                return null;
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Registration failed", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Xác thực người dùng bằng email và mật khẩu (kiểm tra với hash).
     * @param email Email người dùng nhập.
     * @param password Mật khẩu dạng chữ thuần người dùng nhập.
     * @return Đối tượng User nếu xác thực thành công, null nếu email không tồn tại hoặc sai mật khẩu.
     */
    public User loginUser(String email, String password) {
        try {
            Task<com.google.firebase.auth.AuthResult> loginTask = mAuth.signInWithEmailAndPassword(email, password);
            com.google.firebase.auth.AuthResult authResult = Tasks.await(loginTask);
            FirebaseUser firebaseUser = authResult.getUser();

            if (firebaseUser != null) {
                // *** TÙY CHỌN: LẤY DỮ LIỆU TỪ FIRESTORE KHI LOGIN ***
                // Bạn có thể lấy dữ liệu mới nhất từ Firestore thay vì chỉ dùng Auth
                // DocumentSnapshot userDoc = Tasks.await(db.collection("users").document(firebaseUser.getUid()).get());
                // String nameFromFirestore;
                // if (userDoc.exists()) {
                //    nameFromFirestore = userDoc.getString("name");
                // } else {
                //    nameFromFirestore = firebaseUser.getDisplayName(); // Dùng dự phòng
                // }
                // *** KẾT THÚC TÙY CHỌN ***

                User loggedInUser = new User();
                loggedInUser.setEmail(firebaseUser.getEmail());
                loggedInUser.setName(firebaseUser.getDisplayName()); // Hoặc nameFromFirestore
                loggedInUser.setPassword(null);

                Log.d(TAG, "User logged in successfully: " + firebaseUser.getUid());
                return loggedInUser;
            } else {
                Log.w(TAG, "signInWithEmail:failure - User is null after sign in");
                return null;
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Login failed", e);
            Thread.currentThread().interrupt();
            return null;
        }
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