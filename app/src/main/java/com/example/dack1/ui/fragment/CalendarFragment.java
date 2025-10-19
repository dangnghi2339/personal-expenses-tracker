package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.adapter.CalendarAdapter;
import com.example.dack1.ui.adapter.TransactionAdapter;
import com.example.dack1.ui.view.EditTransactionActivity;
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener,
        TransactionAdapter.OnItemClickListener, TransactionAdapter.OnDeleteClickListener {

    // View Models & Adapters
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter transactionAdapter;
    private CalendarAdapter calendarAdapter;

    // Views từ layout
    private TextView monthTextView;
    private ImageView backArrow, forwardArrow;
    private RecyclerView calendarRecyclerView, transactionRecyclerView;
    private TextView totalRevenueTextView, totalExpenditureTextView, remainingTextView;

    // Biến quản lý ngày tháng
    private Calendar selectedDate;

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

        // 4. Cài đặt RecyclerView cho danh sách giao dịch (ở dưới)
        setupTransactionRecyclerView();

        // 5. Cài đặt RecyclerView cho Lịch (ở trên)
        setMonthView();

        // 6. Gán sự kiện cho các nút
        setupClickListeners();

        // 7. Tải dữ liệu cho ngày hôm nay khi mới mở
        fetchTransactionsForDate(selectedDate);

        // 8. (Nợ kỹ thuật) Tải dữ liệu tóm tắt cho tháng
        // fetchSummaryForMonth(selectedDate);
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
        // fetchSummaryForMonth(selectedDate); // (Nợ kỹ thuật)
    }

    private void nextMonthAction() {
        selectedDate.add(Calendar.MONTH, 1); // Tăng 1 tháng
        setMonthView(); // Vẽ lại lịch
        // fetchSummaryForMonth(selectedDate); // (Nợ kỹ thuật)
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