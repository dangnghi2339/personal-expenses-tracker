package com.example.dack1.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dack1.data.local.dao.BudgetDao;
import com.example.dack1.data.local.dao.CategoryDao;
import com.example.dack1.data.local.dao.TransactionDao;
import com.example.dack1.data.model.Budget;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lớp trung tâm của cơ sở dữ liệu Room.
 * Lớp này phải là abstract và kế thừa từ RoomDatabase.
 * @Database annotation khai báo tất cả các Entity thuộc về CSDL này và phiên bản của nó.
 */
@Database(entities = {Transaction.class, Category.class, Budget.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Cung cấp các phương thức abstract để Room có thể tạo ra các DAO tương ứng.
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();

    // --- Bắt đầu triển khai Singleton Pattern ---

    // 'volatile' đảm bảo rằng biến INSTANCE luôn được đọc từ bộ nhớ chính,
    // giúp tránh các vấn đề về thread safety.
    private static volatile AppDatabase INSTANCE;

    // Tạo một thread pool với 4 luồng để chạy các tác vụ CSDL dưới nền.
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Phương thức này đảm bảo rằng chỉ có một instance duy nhất của AppDatabase
     * được tạo ra trong toàn bộ ứng dụng (Singleton Pattern).
     * @param context Context của ứng dụng.
     * @return instance duy nhất của AppDatabase.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "personal_expense_tracker_db")
                            // THÊM DÒNG NÀY:
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}