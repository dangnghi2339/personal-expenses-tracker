package com.example.dack1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dack1.data.model.Budget;

import java.util.List;

/**
 * DAO cho bảng Budget.
 * Cung cấp các phương thức để thao tác với dữ liệu ngân sách.
 */
@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    /**
     * Lấy tất cả các ngân sách đã được thiết lập.
     * Sắp xếp theo ngày bắt đầu gần nhất.
     * @return LiveData chứa danh sách tất cả ngân sách.
     */
    @Query("SELECT * FROM budgets ORDER BY start_date DESC")
    LiveData<List<Budget>> getAllBudgets();

    /**
     * Lấy ngân sách đang hoạt động cho một danh mục cụ thể tại một thời điểm.
     * Câu lệnh này kiểm tra xem ngày hiện tại (currentTime) có nằm trong khoảng
     * từ start_date đến end_date của ngân sách hay không.
     * @param categoryId ID của danh mục cần kiểm tra.
     * @param currentTime Thời gian hiện tại (dạng Unix Timestamp).
     * @return LiveData chứa ngân sách phù hợp (hoặc null nếu không có).
     */
    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND :currentTime BETWEEN start_date AND end_date LIMIT 1")
    LiveData<Budget> findBudgetForCategoryAt(long categoryId, long currentTime);
}