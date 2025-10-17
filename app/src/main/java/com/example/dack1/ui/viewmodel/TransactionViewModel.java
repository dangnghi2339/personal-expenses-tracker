package com.example.dack1.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.data.repository.TransactionRepository;
import java.util.List;

/**
 * ViewModel cho các màn hình liên quan đến Transaction.
 * Nó cung cấp dữ liệu cho UI và xử lý các tương tác của người dùng.
 * ViewModel giao tiếp với Repository để lấy và lưu dữ liệu.
 */
public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        // Tạo một instance của Repository.
        repository = new TransactionRepository(application);
        // Lấy LiveData từ Repository.
        allTransactions = repository.getAllTransactions();
    }

    /**
     * Cung cấp một phương thức công khai để UI có thể "lắng nghe" dữ liệu.
     * UI sẽ không bao giờ truy cập trực tiếp vào Repository.
     * @return LiveData<List<Transaction>>
     */
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    /**
     * Ủy quyền thao tác chèn cho Repository.
     * Đây là phương thức mà UI sẽ gọi khi người dùng muốn lưu một giao dịch mới.
     * @param transaction Giao dịch cần chèn.
     */
    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
}