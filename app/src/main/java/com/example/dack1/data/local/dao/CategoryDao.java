package com.example.dack1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dack1.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    // Lấy tất cả danh mục, trả về LiveData để tự động cập nhật UI
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    // Lấy một danh mục theo ID (có thể dùng LiveData hoặc không)
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id); // Dùng LiveData cho nhất quán

    // Thêm một danh mục mới
    @Insert
    void insert(Category category);

    // Cập nhật thông tin danh mục
    @Update
    void update(Category category);

    // Xóa một danh mục
    @Delete
    void delete(Category category);

    // (Hàm cũ của bạn, không cần thiết nữa nếu dùng getCategoryById)
    // @Query("SELECT name FROM categories WHERE id = :id")
    // String getNamecate(int id);

    // Tìm theo tên để kiểm tra trùng lặp
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    LiveData<Category> findByName(String name);
}