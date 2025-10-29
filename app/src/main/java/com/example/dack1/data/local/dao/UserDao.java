package com.example.dack1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import com.example.dack1.data.model.User;

/**
 * DAO for User entity operations.
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") // Sửa users -> user
    LiveData<User> findByEmail(String email); // Hoặc findByEmailLiveData nếu chọn cách 2 ở trên

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") // Sửa users -> user
    User getUserByEmail(String email);
    @Query("SELECT * FROM users ORDER BY id ASC") // Lấy tất cả user, sắp xếp theo ID
    LiveData<List<User>> getAllUser();
}
