package com.example.dack1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dack1.data.model.Category;

import java.util.List;

/**
 * DAO cho bảng Category.
 * Cung cấp các phương thức để thao tác với dữ liệu danh mục.
 */
@Dao
public interface CategoryDao {

    /**
     * Chèn một danh mục mới.
     * OnConflictStrategy.IGNORE: Nếu cố gắng chèn một danh mục có tên đã tồn tại
     * (do chúng ta đã đặt 'unique' cho cột tên), Room sẽ bỏ qua thao tác chèn này.
     * Điều này an toàn hơn 'REPLACE' trong trường hợp này.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    /**
     * Lấy tất cả các danh mục, sắp xếp theo tên A-Z.
     * @return LiveData chứa danh sách tất cả các danh mục.
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    /**
     * Lấy các danh mục dựa trên loại (INCOME hoặc EXPENSE).
     * Rất hữu ích khi người dùng đang ở màn hình "Thêm Chi Tiêu",
     * chúng ta chỉ cần hiển thị các danh mục thuộc loại EXPENSE.
     * @param type Loại danh mục ("INCOME" hoặc "EXPENSE").
     * @return LiveData chứa danh sách các danh mục phù hợp.
     */
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesByType(String type);
}