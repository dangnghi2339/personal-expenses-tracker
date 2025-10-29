package com.example.dack1.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat; // Cần import này
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dack1.data.model.MonthlyCategorySummary;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.data.repository.TransactionRepository;

import java.util.Calendar;
import java.util.List;

/**
 * ViewModel cho màn hình CategoryDetailActivity.
 * Quản lý việc tải dữ liệu chi tiết cho MỘT danh mục.
 */
public class CategoryDetailViewModel extends AndroidViewModel {

    private final TransactionRepository repository;

    // Biến trung gian để giữ các tham số được truyền từ ChartFragment
    private final MutableLiveData<DetailParams> params = new MutableLiveData<>();

    // Luồng 1: LiveData cho danh sách giao dịch (cho RecyclerView)
    public final LiveData<List<Transaction>> transactionList;

    // Luồng 2: LiveData cho biểu đồ cột (BarChart)
    public final LiveData<List<MonthlyCategorySummary>> barChartData;

    public CategoryDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);

        // Sử dụng switchMap để tự động gọi Repository khi 'params' thay đổi

        // 1. Lấy danh sách giao dịch chi tiết
        transactionList = Transformations.switchMap(params, p -> {
            if (p == null) return new MutableLiveData<>();
            return repository.getTransactionsForCategoryByDateRange(p.categoryId, p.startDate, p.endDate);
        });

        // 2. Lấy dữ liệu 12 tháng cho BarChart
        barChartData = Transformations.switchMap(params, p -> {
            if (p == null) return new MutableLiveData<>();

            // Tính ngày bắt đầu là 12 tháng trước từ hôm nay (hoặc từ endDate)
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(p.endDate); // Dựa trên ngày kết thúc đang xem
            cal.add(Calendar.MONTH, -11); // Lùi 11 tháng (để bao gồm tháng hiện tại là 12)
            cal.set(Calendar.DAY_OF_MONTH, 1);
            setTimeToStartOfDay(cal); // 00:00:00
            long barChartStartDate = cal.getTimeInMillis();

            return repository.getMonthlySummaryForCategory(p.categoryId, p.type, barChartStartDate);
        });
    }

    /**
     * Activity sẽ gọi hàm này DUY NHẤT MỘT LẦN trong onCreate
     * để truyền các tham số cần thiết.
     */
    public void init(long categoryId, String type, long startDate, long endDate) {
        // Chỉ set giá trị nếu nó chưa được set (tránh reload khi xoay màn hình)
        if (this.params.getValue() == null) {
            this.params.setValue(new DetailParams(categoryId, type, startDate, endDate));
        }
    }

    // --- Hàm tiện ích (Helper) ---
    private void setTimeToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Lớp nội bộ (private class) để đóng gói các tham số.
     * Giúp cho việc dùng switchMap dễ dàng hơn.
     */
    private static class DetailParams {
        final long categoryId;
        final String type;
        final long startDate;
        final long endDate;

        DetailParams(long categoryId, String type, long startDate, long endDate) {
            this.categoryId = categoryId;
            this.type = type;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Cần equals và hashCode để LiveData nhận biết sự thay đổi (nếu cần)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DetailParams that = (DetailParams) o;
            return categoryId == that.categoryId &&
                    startDate == that.startDate &&
                    endDate == that.endDate &&
                    ObjectsCompat.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return ObjectsCompat.hash(categoryId, type, startDate, endDate);
        }
    }
}