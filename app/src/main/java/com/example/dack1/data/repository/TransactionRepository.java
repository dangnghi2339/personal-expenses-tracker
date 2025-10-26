package com.example.dack1.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.dack1.data.local.dao.TransactionDao;
import com.example.dack1.data.local.database.AppDatabase;
import com.example.dack1.data.model.Transaction;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.dack1.data.model.CategorySum;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.data.model.MonthlySummary;
import com.example.dack1.data.model.DailySummary;
import com.example.dack1.data.local.dao.TransactionDao;

/**
 * Repository quản lý dữ liệu cho Transaction.
 * Đây là lớp trung gian giữa ViewModel và các nguồn dữ liệu (Data Sources).
 * Nó cung cấp một API sạch sẽ cho ViewModel để tương tác với dữ liệu.
 */
public class TransactionRepository {

    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;

    /**
     * Constructor của Repository.
     * @param application Context của ứng dụng, cần thiết để khởi tạo database.
     */
    public TransactionRepository(Application application) {
        // Lấy instance của database.
        AppDatabase db = AppDatabase.getDatabase(application);
        // Lấy DAO từ database.
        transactionDao = db.transactionDao();
        // Lấy danh sách giao dịch dưới dạng LiveData.
        // Dữ liệu này sẽ được cache lại và tự động cập nhật.
        allTransactions = transactionDao.getAllTransactions();
    }

    /**
     * Cung cấp danh sách tất cả các giao dịch cho ViewModel.
     * ViewModel sẽ "lắng nghe" LiveData này.
     * @return LiveData<List<Transaction>>
     */
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }
    public LiveData<Transaction> getTransactionById(long id) {
        return transactionDao.getTransactionById(id);
    }
    public LiveData<List<CategoryNameSum>> getExpenseSumByCategoryName() {
        return transactionDao.getExpenseSumByCategoryName();
    }
    public LiveData<List<Transaction>> getTransactionsByTimestampRange(long startDate, long endDate) {
        return transactionDao.getTransactionsByTimestampRange(startDate, endDate);
    }
    /**
     * Chèn một giao dịch mới.
     * Thao tác này phải được thực hiện trên một luồng nền (background thread)
     * để không làm treo giao diện người dùng.
     * Chúng ta sử dụng ExecutorService đã tạo trong AppDatabase.
     * @param transaction Giao dịch cần chèn.
     */
    public void insert(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.insert(transaction);
        });
    }

    public void update(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.update(transaction);
        });
    }

    public void delete(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.delete(transaction);
        });
    }
    public LiveData<Double> getTotalIncomeForMonth(long startDate, long endDate) {
        return transactionDao.getTotalIncomeForMonth(startDate, endDate);
    }

    public LiveData<Double> getTotalExpenseForMonth(long startDate, long endDate) {
        return transactionDao.getTotalExpenseForMonth(startDate, endDate);
    }

    public LiveData<List<MonthlySummary>> getMonthlySummaries(long startDate) {
        return transactionDao.getMonthlySummaries(startDate);
    }

    public LiveData<Map<String, DailySummary>> getDailySummariesForMonth(long startDate, long endDate) {
        return androidx.lifecycle.Transformations.map(
            transactionDao.getDailySummariesForMonth(startDate, endDate),
            dailySummaryWithDates -> {
                Map<String, DailySummary> dailySummaries = new HashMap<>();
                if (dailySummaryWithDates != null) {
                    for (TransactionDao.DailySummaryWithDate item : dailySummaryWithDates) {
                        dailySummaries.put(item.date, new DailySummary(item.totalIncome, item.totalExpense));
                    }
                }
                return dailySummaries;
            }
        );
    }
}