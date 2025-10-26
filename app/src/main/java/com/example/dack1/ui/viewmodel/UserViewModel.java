package com.example.dack1.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dack1.data.model.User;
import com.example.dack1.data.repository.UserRepository;

/**
 * ViewModel for User authentication operations.
 */
public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public LiveData<User> findByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    public void insert(User user) {
        repository.insert(user);
    }
}
