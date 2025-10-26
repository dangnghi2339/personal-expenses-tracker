package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.data.model.DailySummary;
import com.example.dack1.ui.adapter.CalendarAdapter;
import com.example.dack1.ui.adapter.TransactionAdapter;
import com.example.dack1.ui.view.EditTransactionActivity;
import com.example.dack1.ui.viewmodel.CategoryViewModel;
import com.example.dack1.ui.viewmodel.TransactionViewModel;
import java.text.NumberFormat; // Để format tiền tệ
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener,
        TransactionAdapter.OnItemClickListener, TransactionAdapter.OnDeleteClickListener {

    // View Models & Adapters
    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionAdapter transactionAdapter;
    private CalendarAdapter calendarAdapter;

    // Views từ layout
    private TextView monthTextView;
    private ImageView backArrow, forwardArrow;
    private RecyclerView calendarRecyclerView, transactionRecyclerView;
    private TextView totalRevenueTextView, totalExpenditureTextView, remainingTextView;
    private TextView tvEmptyCalendarTransactions;

    // Biến quản lý ngày tháng
    private Calendar selectedDate;
    private Map<String, DailySummary> dailySummaries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo ngày hiện tại
        selectedDate = Calendar.getInstance();

        // 2. Ánh xạ Views
        initViews(view);

        // 3. Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        // 4. Cài đặt RecyclerView cho danh sách giao dịch (ở dưới)
        setupTransactionRecyclerView();
        observeCategories();
        // 5. Cài đặt RecyclerView cho Lịch (ở trên)
        setMonthView();

        // 6. Gán sự kiện cho các nút
        setupClickListeners();

        // 7. Tải dữ liệu cho ngày hôm nay khi mới mở
        fetchTransactionsForDate(selectedDate);

        // 8. Tải dữ liệu tóm tắt cho tháng
        fetchSummaryForMonth(selectedDate);
        observeDailySummaries(selectedDate);
    }

    private void initViews(View view) {
        monthTextView = view.findViewById(R.id.monthTextView);
        backArrow = view.findViewById(R.id.backArrow);
        forwardArrow = view.findViewById(R.id.forwardArrow);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        transactionRecyclerView = view.findViewById(R.id.rv_calendar_transactions);

        // Summary (tạm thời chưa có logic)
        totalRevenueTextView = view.findViewById(R.id.totalRevenueTextView);
        totalExpenditureTextView = view.findViewById(R.id.totalExpenditureTextView);
        remainingTextView = view.findViewById(R.id.remainingTextView);
        tvEmptyCalendarTransactions = view.findViewById(R.id.tv_empty_calendar_transactions);
    }

    private void setupClickListeners() {
        backArrow.setOnClickListener(v -> previousMonthAction());
        forwardArrow.setOnClickListener(v -> nextMonthAction());
    }

    /**
     * Hàm cốt lõi: Cập nhật toàn bộ Lịch
     */
    private void setMonthView() {
        // 1. Cập nhật tên tháng (ví dụ: "October 2025")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        monthTextView.setText(sdf.format(selectedDate.getTime()));

        // 2. Tạo danh sách 42 ngày
        ArrayList<String> daysInMonth = generateDaysInMonth(selectedDate);

        // 3. Tạo Adapter
        calendarAdapter = new CalendarAdapter(daysInMonth, this);
        calendarAdapter.setCurrentMonth(selectedDate);
        
        // Pass daily summaries to adapter if available
        if (dailySummaries != null) {
            calendarAdapter.setDailySummaries(dailySummaries);
        }

        // 4. Cài đặt RecyclerView Lịch
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    /**
     * Hàm "thần kỳ": Tạo ra 42 ô ngày cho lịch
     */
    private ArrayList<String> generateDaysInMonth(Calendar date) {
        ArrayList<String> daysList = new ArrayList<>();
        Calendar monthCalendar = (Calendar) date.clone();

        // 1. Đặt về ngày 1 của tháng
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // 2. Lấy ngày trong tuần của ngày 1 (ví dụ: Thứ 4)
        // (Calendar.SUNDAY = 1, MONDAY = 2...)
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);

        // 3. Tính số ô trống cần thêm vào (ví dụ: nếu T4, cần 3 ô trống T2, T3)
        // (Trừ 1 vì Calendar.SUNDAY = 1)
        int daysBefore = firstDayOfMonth - 1;
        for (int i = 0; i < daysBefore; i++) {
            daysList.add(""); // Thêm ô trống
        }

        // 4. Lấy số ngày trong tháng (ví dụ: 31)
        int daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 5. Thêm các ngày thật (1, 2, 3... 31)
        for (int i = 1; i <= daysInMonth; i++) {
            daysList.add(String.valueOf(i));
        }

        // 6. Thêm các ô trống còn lại cho đủ 42
        int remainingCells = 42 - daysList.size();
        for (int i = 0; i < remainingCells; i++) {
            daysList.add("");
        }

        return daysList;
    }

    private void previousMonthAction() {
        selectedDate.add(Calendar.MONTH, -1); // Lùi 1 tháng
        setMonthView(); // Vẽ lại lịch
        fetchSummaryForMonth(selectedDate);
        observeDailySummaries(selectedDate);
    }

    private void nextMonthAction() {
        selectedDate.add(Calendar.MONTH, 1); // Tăng 1 tháng
        setMonthView(); // Vẽ lại lịch
        fetchSummaryForMonth(selectedDate);
        observeDailySummaries(selectedDate);
    }

    /**
     * Hàm này được gọi khi bấm vào 1 NGÀY trên LỊCH
     */
    @Override
    public void onItemClick(String day) {
        if (!day.isEmpty()) {
            // 1. Cập nhật ngày được chọn
            selectedDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

            // 2. Tải giao dịch cho ngày này
            fetchTransactionsForDate(selectedDate);
        }
    }

    // --- Logic cho RecyclerView Giao Dịch (Bên dưới) ---

    private void setupTransactionRecyclerView() {
        transactionAdapter = new TransactionAdapter(); // Dùng ListAdapter
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRecyclerView.setAdapter(transactionAdapter);
        transactionAdapter.setOnItemClickListener(this);
        transactionAdapter.setOnDeleteClickListener(this);
    }

    private void fetchTransactionsForDate(Calendar calendar) {
        Calendar start = (Calendar) calendar.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        long startDate = start.getTimeInMillis();

        Calendar end = (Calendar) calendar.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        long endDate = end.getTimeInMillis();

        transactionViewModel.getTransactionsByTimestampRange(startDate, endDate)
                .observe(getViewLifecycleOwner(), transactions -> {
                    transactionAdapter.submitList(transactions);
                    
                    // Show/hide empty state
                    if (transactions == null || transactions.isEmpty()) {
                        transactionRecyclerView.setVisibility(View.GONE);
                        tvEmptyCalendarTransactions.setVisibility(View.VISIBLE);
                    } else {
                        transactionRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyCalendarTransactions.setVisibility(View.GONE);
                    }
                });
    }
    private void fetchSummaryForMonth(Calendar calendar) {
        // 1. Tính timestamp đầu tháng (ngày 1, 00:00:00)
        Calendar startOfMonth = (Calendar) calendar.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        long startDate = startOfMonth.getTimeInMillis();

        // 2. Tính timestamp cuối tháng (ngày cuối cùng, 23:59:59)
        Calendar endOfMonth = (Calendar) calendar.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);
        long endDate = endOfMonth.getTimeInMillis();

        // 3. Observe tổng thu nhập
        transactionViewModel.getTotalIncomeForMonth(startDate, endDate).removeObservers(getViewLifecycleOwner());
        transactionViewModel.getTotalIncomeForMonth(startDate, endDate).observe(getViewLifecycleOwner(), totalIncome -> {
            double income = (totalIncome != null) ? totalIncome : 0.0;
            updateSummaryUI(income, -1); // -1 nghĩa là chưa có expense
        });

        // 4. Observe tổng chi tiêu
        transactionViewModel.getTotalExpenseForMonth(startDate, endDate).removeObservers(getViewLifecycleOwner());
        transactionViewModel.getTotalExpenseForMonth(startDate, endDate).observe(getViewLifecycleOwner(), totalExpense -> {
            double expense = (totalExpense != null) ? totalExpense : 0.0;
            updateSummaryUI(-1, expense); // -1 nghĩa là chưa có income
        });
    }

    private void observeDailySummaries(Calendar calendar) {
        // Calculate start and end dates for the month
        Calendar startOfMonth = (Calendar) calendar.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        long startDate = startOfMonth.getTimeInMillis();

        Calendar endOfMonth = (Calendar) calendar.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);
        long endDate = endOfMonth.getTimeInMillis();

        transactionViewModel.getDailySummariesForMonth(startDate, endDate).removeObservers(getViewLifecycleOwner());
        transactionViewModel.getDailySummariesForMonth(startDate, endDate).observe(getViewLifecycleOwner(), summaries -> {
            dailySummaries = summaries;
            // Update calendar adapter with new summaries
            if (calendarAdapter != null) {
                calendarAdapter.setDailySummaries(dailySummaries);
            }
        });
    }

    // Biến tạm để lưu trữ giá trị thu/chi
    private double currentMonthIncome = -1;
    private double currentMonthExpense = -1;

    /**
     * Cập nhật giao diện Tóm tắt tháng
     * @param income Tổng thu (hoặc -1 nếu chưa có)
     * @param expense Tổng chi (hoặc -1 nếu chưa có)
     */
    private void updateSummaryUI(double income, double expense) {
        // Lưu lại giá trị mới
        if (income != -1) currentMonthIncome = income;
        if (expense != -1) currentMonthExpense = expense;

        // Chỉ cập nhật UI khi cả hai giá trị đều đã có
        if (currentMonthIncome != -1 && currentMonthExpense != -1) {
            // Format tiền tệ VND
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            // Cập nhật TextViews
            totalRevenueTextView.setText("+" + currencyFormatter.format(currentMonthIncome));
            totalExpenditureTextView.setText("-" + currencyFormatter.format(currentMonthExpense));

            double remaining = currentMonthIncome - currentMonthExpense;
            remainingTextView.setText((remaining >= 0 ? "+" : "") + currencyFormatter.format(remaining));
            // Đổi màu số dư nếu âm
            remainingTextView.setTextColor(remaining >= 0 ? Color.parseColor("#4CAF50") : Color.RED); // Green or Red

            // Reset biến tạm để chờ lần cập nhật tiếp theo
            currentMonthIncome = -1;
            currentMonthExpense = -1;
        }
    }
    /**
     * Lắng nghe danh sách Category và cung cấp cho TransactionAdapter.
     */
    private void observeCategories() {
        // Lắng nghe LiveData từ CategoryViewModel
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            // Kiểm tra xem danh sách categories và adapter đã sẵn sàng chưa
            if (categories != null && transactionAdapter != null) {
                // Tạo một Map để lưu trữ Category theo ID
                Map<Long, Category> categoryMap = new HashMap<>();
                // Duyệt qua danh sách categories và đưa vào Map
                for (Category category : categories) {
                    categoryMap.put(category.getId(), category);
                }
                // Gọi hàm setCategoryMap của TransactionAdapter để cung cấp dữ liệu
                transactionAdapter.setCategoryMap(categoryMap);
                // Bạn có thể thêm Log để kiểm tra nếu muốn:
                // Log.d("CalendarFragment", "Category map set to adapter. Size: " + categoryMap.size());
            } else {
                // Log lỗi nếu cần:
                // Log.w("CalendarFragment", "Categories list is null or transactionAdapter not ready");
            }
        });
    }





    @Override
    public void onItemClick(Transaction transaction) {
        Intent intent = new Intent(getActivity(), EditTransactionActivity.class);
        intent.putExtra("TRANSACTION_ID", transaction.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Transaction transaction) {
        transactionViewModel.delete(transaction);
        Toast.makeText(getContext(), "Đã xóa: " + transaction.getDescription(), Toast.LENGTH_SHORT).show();
    }
}