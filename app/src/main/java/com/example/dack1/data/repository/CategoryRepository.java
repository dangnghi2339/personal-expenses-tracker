package com.example.dack1.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import com.example.dack1.data.local.dao.CategoryDao;
import com.example.dack1.data.local.database.AppDatabase;
import com.example.dack1.data.model.Category;

import java.util.List;

public class CategoryRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase((Context) application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    // --- Các hàm CRUD ---

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }

    // Các hàm Insert, Update, Delete cần chạy trên background thread
    // Chúng ta dùng ExecutorService đơn giản
    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    public void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    public void delete(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.delete(category);
        });
    }
}