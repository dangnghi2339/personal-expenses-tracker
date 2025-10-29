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
import java.util.Map;
import com.example.dack1.data.model.MonthlyCategorySummary; // ĐẢM BẢO CÓ DÒNG NÀY
import com.example.dack1.data.model.CategorySum;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.data.model.MonthlySummary;
import com.example.dack1.data.model.DailySummary;
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


    /**
     * Lấy tất cả giao dịch trong một khoảng timestamp (từ 00:00:00 ngày bắt đầu
     * đến 23:59:59 ngày kết thúc)
     */
    @Query("SELECT * FROM transactions WHERE transaction_date >= :startDate AND transaction_date <= :endDate ORDER BY transaction_date DESC")
    LiveData<List<Transaction>> getTransactionsByTimestampRange(long startDate, long endDate);
    /**
     * Tính tổng số tiền thu nhập ('income') trong một khoảng timestamp.
     * Trả về LiveData<Double>, có thể là null nếu không có giao dịch nào.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND transaction_date >= :startDate AND transaction_date <= :endDate")
    LiveData<Double> getTotalIncomeForMonth(long startDate, long endDate);

    /**
     * Tính tổng số tiền chi tiêu ('expense') trong một khoảng timestamp.
     * Trả về LiveData<Double>, có thể là null nếu không có giao dịch nào.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND transaction_date >= :startDate AND transaction_date <= :endDate")
    LiveData<Double> getTotalExpenseForMonth(long startDate, long endDate);

    /**
     * Lấy tổng thu nhập và chi tiêu theo tháng cho 12 tháng gần nhất.
     */
    @Query("SELECT " +
           "strftime('%Y-%m', datetime(transaction_date/1000, 'unixepoch')) AS monthYear, " +
           "SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS totalIncome, " +
           "SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS totalExpense " +
           "FROM transactions " +
           "WHERE transaction_date >= :startDate " +
           "GROUP BY strftime('%Y-%m', datetime(transaction_date/1000, 'unixepoch')) " +
           "ORDER BY monthYear DESC " +
           "LIMIT 12")
    LiveData<List<MonthlySummary>> getMonthlySummaries(long startDate);

    /**
     * Lấy tổng thu nhập và chi tiêu theo ngày trong một tháng.
     * Trả về Map với key là "yyyy-MM-dd" và value là DailySummary.
     */
    @Query("SELECT " +
            // SỬA: Thêm ', 'localtime'' vào strftime
            "strftime('%Y-%m-%d', datetime(transaction_date/1000, 'unixepoch', 'localtime')) AS date, " +
            "SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS totalIncome, " +
            "SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS totalExpense " +
            "FROM transactions " +
            "WHERE transaction_date >= :startDate AND transaction_date <= :endDate " +
            // SỬA: Thêm ', 'localtime'' vào strftime ở GROUP BY
            "GROUP BY strftime('%Y-%m-%d', datetime(transaction_date/1000, 'unixepoch', 'localtime'))")
    LiveData<List<DailySummaryWithDate>> getDailySummariesForMonth(long startDate, long endDate);

    /**
     * Đếm số giao dịch đang sử dụng một category cụ thể.
     */
    @Query("SELECT COUNT(*) FROM transactions WHERE category_id = :categoryId")
    LiveData<Integer> getTransactionCountByCategoryId(long categoryId);

    /**
     * Lấy tổng tiền theo danh mục, lọc theo loại (thu/chi) và khoảng thời gian.
     * Trả về một List<CategoryNameSum> để dùng cho cả Biểu đồ tròn và RecyclerView.
     * SỬA: Dùng đúng tên cột 'category_id', 'transaction_date', 'icon_name', 'c.id'.
     * Bỏ 'userId' vì bảng transactions không có.
     */
    @Query("SELECT t.category_id as categoryId, c.name as categoryName, c.icon_name as categoryIcon, c.color as categoryColor, SUM(t.amount) as totalAmount " +
            "FROM transactions t JOIN categories c ON t.category_id = c.id " +
            "WHERE t.type = :type AND t.transaction_date BETWEEN :startDate AND :endDate " + // Sửa timestamp -> transaction_date, bỏ userId
            "GROUP BY t.category_id, categoryName, categoryIcon, categoryColor " + // Sửa categoryId -> t.category_id
            "ORDER BY totalAmount DESC")
    LiveData<List<CategoryNameSum>> getCategorySumsByDateRange(String type, long startDate, long endDate); // Bỏ userId khỏi tham số

    /**
     * Lấy tổng số tiền (thu hoặc chi) trong một khoảng thời gian.
     * Dùng cho Box tổng quan (Total revenue, Total expenditure).
     * SỬA: Dùng đúng tên cột 'transaction_date'. Bỏ 'userId'.
     */
    @Query("SELECT SUM(amount) FROM transactions " +
            "WHERE type = :type AND transaction_date BETWEEN :startDate AND :endDate") // Sửa timestamp -> transaction_date, bỏ userId
    LiveData<Double> getTotalAmountByDateRange(String type, long startDate, long endDate); // Bỏ userId khỏi tham số, kiểu trả về là Double như hàm cũ của bạn
    /**
     * Helper class for daily summaries with date.
     */

    /**
     * Lấy TẤT CẢ giao dịch của MỘT danh mục cụ thể trong một khoảng thời gian.
     * Dùng cho RecyclerView ở màn hình chi tiết.
     */
    @Query("SELECT * FROM transactions " +
            "WHERE category_id = :categoryId AND transaction_date BETWEEN :startDate AND :endDate " +
            "ORDER BY transaction_date DESC")
    LiveData<List<Transaction>> getTransactionsForCategoryByDateRange(long categoryId, long startDate, long endDate);

    /**
     * Lấy tổng chi tiêu của MỘT danh mục theo từng tháng (cho 12 tháng gần nhất).
     * Dùng cho BarChart ở màn hình chi tiết.
     * Cần một POJO mới (MonthlyCategorySummary) để giữ kết quả.
     */
    @Query("SELECT " +
            "strftime('%Y-%m', datetime(transaction_date/1000, 'unixepoch')) AS monthYear, " +
            "SUM(amount) AS totalAmount " +
            "FROM transactions " +
            "WHERE category_id = :categoryId AND type = :type AND transaction_date >= :startDate " + // startDate (12 tháng trước)
            "GROUP BY strftime('%Y-%m', datetime(transaction_date/1000, 'unixepoch')) " +
            "ORDER BY monthYear ASC") // Sắp xếp TĂNG DẦN để BarChart hiển thị đúng
    LiveData<List<MonthlyCategorySummary>> getMonthlySummaryForCategory(long categoryId, String type, long startDate);
    class DailySummaryWithDate {
        public String date;
        public double totalIncome;
        public double totalExpense;
    }
}