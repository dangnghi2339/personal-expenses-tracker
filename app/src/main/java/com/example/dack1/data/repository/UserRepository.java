package com.example.dack1.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.dack1.data.local.dao.UserDao;
import com.example.dack1.data.local.database.AppDatabase;
import com.example.dack1.data.model.User;

/**
 * Repository for User data operations.
 */
public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    public LiveData<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public LiveData<User> findByEmailAndPassword(String email, String password) {
        return userDao.findByEmailAndPassword(email, password);
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }
}
