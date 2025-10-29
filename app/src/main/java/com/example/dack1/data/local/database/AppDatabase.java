package com.example.dack1.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dack1.data.local.dao.BudgetDao;
import com.example.dack1.data.local.dao.CategoryDao;
import com.example.dack1.data.local.dao.TransactionDao;
import com.example.dack1.data.local.dao.UserDao; // Thêm import
import com.example.dack1.data.model.Budget;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.data.model.User; // Thêm import

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.sqlite.db.SupportSQLiteDatabase; // Thêm import này
import androidx.annotation.NonNull; // Thêm import này
/**
 * Lớp trung tâm của cơ sở dữ liệu Room.
 * Lớp này phải là abstract và kế thừa từ RoomDatabase.
 * @Database annotation khai báo tất cả các Entity thuộc về CSDL này và phiên bản của nó.
 */
// SỬA LỖI 1: Thêm User.class và Tăng version lên 3
@Database(entities = {Transaction.class, Category.class, Budget.class, User.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Cung cấp các phương thức abstract để Room có thể tạo ra các DAO tương ứng.
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();
    public abstract UserDao userDao(); // Thêm abstract method cho UserDao

    // --- Bắt đầu triển khai Singleton Pattern ---

    private static volatile AppDatabase INSTANCE;

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
                            // SỬA LỖI 2: Thêm dòng này để cho phép xóa DB cũ khi nâng cấp
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Thực hiện chèn dữ liệu mẫu trên một luồng nền
            databaseWriteExecutor.execute(() -> {
                // Lấy DAO để thực hiện insert
                CategoryDao dao = INSTANCE.categoryDao();
                // Xóa hết dữ liệu cũ (tùy chọn, nếu bạn muốn reset mỗi lần DB tạo lại)
                // dao.deleteAll(); // Nếu bạn có hàm deleteAll()

                // Tạo các danh mục mẫu (Expense)
                dao.insert(new Category("Ăn uống", "expense", "ic_eat_drink", "#FFBC5C"));
                dao.insert(new Category("Mua sắm", "expense", "ic_shopping", "#1073F5"));
                dao.insert(new Category("Đi chợ", "expense", "ic_market1", "#FF6984"));
                dao.insert(new Category("Xăng xe", "expense", "ic_gasoline", "#43D6CF"));
                dao.insert(new Category("Nhà cửa", "expense", "ic_house", "#B45DE1"));
                dao.insert(new Category("Điện nước", "expense", "ic_electric", "#FFD32A"));
                dao.insert(new Category("Điện thoại", "expense", "ic_sim", "#4FCE6F"));
                dao.insert(new Category("Học phí", "expense", "ic_school", "#514FD4"));
                dao.insert(new Category("Tín dụng", "expense", "ic_credit", "#76D1FA"));


                // Tạo các danh mục mẫu (Income) - Thêm 2 cái cho đủ 10+
                dao.insert(new Category("Lương", "income", "ic_credit", "#4CAF50")); // Dùng icon credit, màu xanh lá
                dao.insert(new Category("Thưởng", "income", "ic_profile", "#FFC107")); // Dùng icon profile, màu vàng cam

                // Bạn có thể thêm các danh mục khác nếu muốn...
            });
        }
    };
}
