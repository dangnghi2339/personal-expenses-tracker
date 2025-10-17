package com.example.dack1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dack1.data.model.Transaction;

import java.util.List;

/**
 * DAO (Data Access Object) cho bảng Transaction.
 * Interface này chứa tất cả các phương thức để thao tác với bảng 'transactions'.
 * Room sẽ tự động tạo ra phần thực thi cho các phương thức này.
 */
@Dao
public interface TransactionDao {

    /**
     * Chèn một giao dịch mới vào CSDL.
     * @param transaction đối tượng giao dịch cần chèn.
     * @OnConflictStrategy.REPLACE: Nếu có một giao dịch với id đã tồn tại,
     * nó sẽ được thay thế bằng giao dịch mới.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    /**
     * Cập nhật một giao dịch đã có.
     * Room sẽ tìm giao dịch có cùng khóa chính (id) và cập nhật các trường của nó.
     * @param transaction đối tượng giao dịch với thông tin đã được cập nhật.
     */
    @Update
    void update(Transaction transaction);

    /**
     * Xóa một giao dịch khỏi CSDL.
     * @param transaction đối tượng giao dịch cần xóa.
     */
    @Delete
    void delete(Transaction transaction);

    /**
     * Lấy tất cả các giao dịch từ CSDL, sắp xếp theo ngày gần nhất lên đầu.
     * @return một đối tượng LiveData chứa danh sách các giao dịch.
     * LiveData sẽ tự động cập nhật UI khi dữ liệu trong bảng thay đổi.
     */
    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    /**
     * Lấy một giao dịch cụ thể dựa vào ID của nó.
     * @param id của giao dịch cần tìm.
     * @return LiveData chứa giao dịch duy nhất (hoặc null nếu không tìm thấy).
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<Transaction> getTransactionById(long id);
}