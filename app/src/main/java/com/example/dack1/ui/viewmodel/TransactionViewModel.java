package com.example.dack1.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.data.repository.TransactionRepository;
import java.util.List;
import java.util.Map;
import com.example.dack1.data.model.CategorySum;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.data.model.MonthlySummary;
import com.example.dack1.data.model.DailySummary;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.core.util.Pair;
import java.util.Calendar;
/**
 * ViewModel cho các màn hình liên quan đến Transaction.
 * Nó cung cấp dữ liệu cho UI và xử lý các tương tác của người dùng.
 * ViewModel giao tiếp với Repository để lấy và lưu dữ liệu.
 */
public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;
    // 1. Lưu ngày tháng đang được chọn (mặc định là hôm nay)
    private final MutableLiveData<Calendar> _selectedDate = new MutableLiveData<>(Calendar.getInstance());
    public final LiveData<Calendar> selectedDate = _selectedDate;

    // 2. Lưu chế độ xem (true = Tháng, false = Năm)
    private final MutableLiveData<Boolean> _isMonthView = new MutableLiveData<>(true);
    public final LiveData<Boolean> isMonthView = _isMonthView;

    // 3. Lưu loại giao dịch đang xem (expense = chi, income = thu)
    private final MutableLiveData<String> _selectedType = new MutableLiveData<>("expense");
    public final LiveData<String> selectedType = _selectedType;

    // 4. Biến trung gian: Tính toán [startDate, endDate] dựa trên 1 và 2
    //    Lưu ý: Khởi tạo trong constructor
    private final MediatorLiveData<long[]> _dateRange = new MediatorLiveData<>();
    public final LiveData<long[]> dateRange = _dateRange;
    // 5. Biến trung gian: Kết hợp 3 và 4
    //    Lưu ý: Khởi tạo trong constructor
    private final MediatorLiveData<Pair<long[], String>> _reportTrigger = new MediatorLiveData<>();

    // --- LiveData Cung cấp cho UI ---

    // 6. Box Tổng quan: TỔNG THU (chỉ phụ thuộc vào ngày tháng)
    //    Lưu ý: Khởi tạo trong constructor
    public final LiveData<Double> totalIncome;

    // 7. Box Tổng quan: TỔNG CHI (chỉ phụ thuộc vào ngày tháng)
    //    Lưu ý: Khởi tạo trong constructor
    public final LiveData<Double> totalExpense;

    // 8. Biểu đồ tròn & RecyclerView (phụ thuộc vào ngày tháng VÀ loại thu/chi)
    //    Lưu ý: Khởi tạo trong constructor
    public final LiveData<List<CategoryNameSum>> categoryDataList;
    public TransactionViewModel(@NonNull Application application) {
        super(application);
        // Tạo một instance của Repository.
        repository = new TransactionRepository(application);
        // Lấy LiveData từ Repository.
        allTransactions = repository.getAllTransactions();

        // --- Logic tính toán cho Giai đoạn 2 ---
        // 1. Gắn listener để TỰ ĐỘNG tính toán lại _dateRange khi ngày hoặc chế độ xem thay đổi
        _dateRange.addSource(_selectedDate, calendar -> updateDateRange());
        _dateRange.addSource(_isMonthView, isMonth -> updateDateRange());

        // 2. Tự động gọi Repository khi _dateRange thay đổi
        totalIncome = Transformations.switchMap(_dateRange, range ->
                repository.getTotalAmountByDateRange("income", range[0], range[1]));

        totalExpense = Transformations.switchMap(_dateRange, range ->
                repository.getTotalAmountByDateRange("expense", range[0], range[1]));

        // 3. Gắn listener cho trigger kết hợp
        _reportTrigger.addSource(_dateRange, dateRange ->
                _reportTrigger.setValue(Pair.create(dateRange, _selectedType.getValue())));

        _reportTrigger.addSource(_selectedType, type ->
                _reportTrigger.setValue(Pair.create(_dateRange.getValue(), type)));

        // 4. Tự động gọi Repository khi _reportTrigger thay đổi
        categoryDataList = Transformations.switchMap(_reportTrigger, trigger -> {
            long[] range = trigger.first;
            String type = trigger.second;
            // Đảm bảo dữ liệu không null
            if (range == null || range.length < 2 || type == null) {
                return new MutableLiveData<>(); // Trả về LiveData rỗng
            }
            return repository.getCategorySumsByDateRange(type, range[0], range[1]);
        });

        // 5. Khởi chạy tính toán lần đầu
        updateDateRange();

    }

    public LiveData<Transaction> getTransactionById(long id) {
        return repository.getTransactionById(id);
    }

    /**
     * Cung cấp một phương thức công khai để UI có thể "lắng nghe" dữ liệu.
     * UI sẽ không bao giờ truy cập trực tiếp vào Repository.
     * @return LiveData<List<Transaction>>
     */
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getTransactionsByTimestampRange(long startDate, long endDate) {
        return repository.getTransactionsByTimestampRange(startDate, endDate);
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
    public LiveData<Double> getTotalIncomeForMonth(long startDate, long endDate) {
        return repository.getTotalIncomeForMonth(startDate, endDate);
    }

    public LiveData<Double> getTotalExpenseForMonth(long startDate, long endDate) {
        return repository.getTotalExpenseForMonth(startDate, endDate);
    }

    public LiveData<List<MonthlySummary>> getMonthlySummaries(long startDate) {
        return repository.getMonthlySummaries(startDate);
    }

    public LiveData<Map<String, DailySummary>> getDailySummariesForMonth(long startDate, long endDate) {
        return repository.getDailySummariesForMonth(startDate, endDate);
    }
    private void updateDateRange() {
        Calendar calendar = _selectedDate.getValue();
        if (calendar == null) return;

        Calendar startCal = (Calendar) calendar.clone();
        Calendar endCal = (Calendar) calendar.clone();

        if (Boolean.TRUE.equals(_isMonthView.getValue())) {
            // Xem theo THÁNG
            startCal.set(Calendar.DAY_OF_MONTH, 1);
            setTimeToStartOfDay(startCal); // 00:00:00

            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndOfDay(endCal); // 23:59:59
        } else {
            // Xem theo NĂM
            startCal.set(Calendar.DAY_OF_YEAR, 1);
            setTimeToStartOfDay(startCal); // 00:00:00

            endCal.set(Calendar.DAY_OF_YEAR, endCal.getActualMaximum(Calendar.DAY_OF_YEAR));
            setTimeToEndOfDay(endCal); // 23:59:59
        }

        // Phát ra giá trị mới
        _dateRange.setValue(new long[]{startCal.getTimeInMillis(), endCal.getTimeInMillis()});
    }

    // --- Các hàm Public cho Fragment gọi ---

    /**
     * Fragment gọi hàm này khi người dùng bấm nút "Month" hoặc "Year"
     */
    public void setViewMode(boolean isMonth) {
        _isMonthView.setValue(isMonth);
    }

    /**
     * Fragment gọi hàm này khi người dùng chọn tab "Thu" hoặc "Chi"
     */
    public void setSelectedType(String type) {
        _selectedType.setValue(type);
    }

    /**
     * Fragment gọi hàm này khi người dùng chọn ngày từ DatePicker
     */
    public void setSelectedDate(Calendar newDate) {
        _selectedDate.setValue(newDate);
    }

    /**
     * Fragment gọi hàm này khi người dùng bấm mũi tên '>' (Next)
     */
    public void nextPeriod() {
        Calendar cal = (Calendar) _selectedDate.getValue().clone();
        if (Boolean.TRUE.equals(_isMonthView.getValue())) {
            cal.add(Calendar.MONTH, 1);
        } else {
            cal.add(Calendar.YEAR, 1);
        }
        _selectedDate.setValue(cal);
    }

    /**
     * Fragment gọi hàm này khi người dùng bấm mũi tên '<' (Previous)
     */
    public void previousPeriod() {
        Calendar cal = (Calendar) _selectedDate.getValue().clone();
        if (Boolean.TRUE.equals(_isMonthView.getValue())) {
            cal.add(Calendar.MONTH, -1);
        } else {
            cal.add(Calendar.YEAR, -1);
        }
        _selectedDate.setValue(cal);
    }

    // --- Hàm tiện ích (Helper) ---
    private void setTimeToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void setTimeToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}