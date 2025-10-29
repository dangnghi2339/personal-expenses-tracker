package com.example.dack1.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Thêm để test GĐ 5

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dack1.R;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.databinding.FragmentChartBinding; // SỬ DỤNG VIEW BINDING
import com.example.dack1.ui.adapter.CategoryReportAdapter; // Adapter mới
import com.example.dack1.ui.view.CategoryDetailActivity;
import com.example.dack1.ui.viewmodel.TransactionViewModel;
import com.example.dack1.util.FormatUtils; // Tiện ích của bạn
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChartFragment extends Fragment {

    private FragmentChartBinding binding; // Sử dụng ViewBinding
    private TransactionViewModel transactionViewModel;
    private CategoryReportAdapter categoryReportAdapter; // Adapter mới

    private final Calendar currentCalendar = Calendar.getInstance(); // Biến tạm để giữ ngày

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout bằng ViewBinding
        binding = FragmentChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupAdapter();
        setupPieChart(); // Cài đặt biểu đồ trống ban đầu
        setupEventListeners();
        setupObservers();
    }

    // Khởi tạo ViewModel
    private void setupViewModel() {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
    }

    // Khởi tạo Adapter và RecyclerView
    private void setupAdapter() {
        categoryReportAdapter = new CategoryReportAdapter();
        binding.rvCategoryReport.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCategoryReport.setAdapter(categoryReportAdapter);

        // Chuẩn bị cho Giai đoạn 5: Xử lý khi click vào item
        categoryReportAdapter.setOnItemClickListener(item -> {
            // Lấy các tham số hiện tại từ ViewModel
            long[] dateRange = transactionViewModel.dateRange.getValue();
            String currentType = transactionViewModel.selectedType.getValue();

            if (dateRange == null || currentType == null) {
                Toast.makeText(getContext(), "Error: Cannot get data", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
            intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY_ID, item.categoryId);
            intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY_NAME, item.categoryName);
            intent.putExtra(CategoryDetailActivity.EXTRA_TYPE, currentType);
            intent.putExtra(CategoryDetailActivity.EXTRA_START_DATE, dateRange[0]);
            intent.putExtra(CategoryDetailActivity.EXTRA_END_DATE, dateRange[1]);

            startActivity(intent);
        });
    }

    // Cài đặt các Listener cho nút bấm
    private void setupEventListeners() {
        // 1. Bộ chọn Tháng/Năm
        binding.toggleViewMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnViewMonth) {
                    transactionViewModel.setViewMode(true); // true = Month
                } else if (checkedId == R.id.btnViewYear) {
                    transactionViewModel.setViewMode(false); // false = Year
                }
            }
        });

        // 2. Bộ chọn Ngày
        binding.btnPrevious.setOnClickListener(v -> transactionViewModel.previousPeriod());
        binding.btnNext.setOnClickListener(v -> transactionViewModel.nextPeriod());
        binding.tvSelectedDate.setOnClickListener(v -> openDatePicker());

        // 3. Tabs Thu/Chi
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Tab "Expenditure" (Chi tiêu)
                    transactionViewModel.setSelectedType("expense");
                    categoryReportAdapter.setTransactionType("expense");
                } else {
                    // Tab "Revenue" (Thu nhập)
                    transactionViewModel.setSelectedType("income");
                    categoryReportAdapter.setTransactionType("income");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // "Lắng nghe" dữ liệu từ ViewModel
    private void setupObservers() {
        // 1. Lắng nghe ngày tháng thay đổi
        transactionViewModel.selectedDate.observe(getViewLifecycleOwner(), calendar -> {
            currentCalendar.setTime(calendar.getTime()); // Cập nhật biến tạm
            updateDateTextView();
        });

        // 2. Lắng nghe chế độ xem thay đổi (để cập nhật format ngày tháng)
        transactionViewModel.isMonthView.observe(getViewLifecycleOwner(), isMonth -> {
            updateDateTextView();
        });

        // 3. Lắng nghe TỔNG THU
        transactionViewModel.totalIncome.observe(getViewLifecycleOwner(), income -> {
            double totalIncome = (income != null) ? income : 0.0;
            binding.tvTotalIncome.setText(FormatUtils.formatCurrency(totalIncome));
            updateTotalRemaining(); // Cập nhật số dư
        });

        // 4. Lắng nghe TỔNG CHI
        transactionViewModel.totalExpense.observe(getViewLifecycleOwner(), expense -> {
            double totalExpense = (expense != null) ? expense : 0.0;
            binding.tvTotalExpense.setText(FormatUtils.formatCurrency(totalExpense));
            updateTotalRemaining(); // Cập nhật số dư
        });

        // 5. Lắng nghe DỮ LIỆU CHÍNH (cho Biểu đồ và RecyclerView)
        transactionViewModel.categoryDataList.observe(getViewLifecycleOwner(), dataList -> {
            if (dataList != null) {
                // Tính tổng của list này
                double grandTotal = 0.0;
                for (CategoryNameSum item : dataList) {
                    grandTotal += item.totalAmount;
                }

                // Cập nhật PieChart
                updatePieChart(dataList, grandTotal);

                // Cập nhật RecyclerView
                categoryReportAdapter.setGrandTotal(grandTotal); // Cung cấp tổng cho adapter
                categoryReportAdapter.submitList(dataList); // Gửi list cho adapter
            }
        });
    }

    // --- Các hàm Helper ---

    // Cập nhật text ngày tháng
    private void updateDateTextView() {
        SimpleDateFormat sdf;
        if (Boolean.TRUE.equals(transactionViewModel.isMonthView.getValue())) {
            sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        }
        binding.tvSelectedDate.setText(sdf.format(currentCalendar.getTime()));
    }

    // Cập nhật số dư
    private void updateTotalRemaining() {
        // Lấy giá trị đã quan sát (an toàn hơn)
        Double income = transactionViewModel.totalIncome.getValue();
        Double expense = transactionViewModel.totalExpense.getValue();
        double totalIncome = (income != null) ? income : 0.0;
        double totalExpense = (expense != null) ? expense : 0.0;
        double remaining = totalIncome - totalExpense;
        binding.tvTotalRemaining.setText(FormatUtils.formatCurrency(remaining));

        // Đổi màu số dư
        if (remaining >= 0) {
            binding.tvTotalRemaining.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            binding.tvTotalRemaining.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
    }

    // Mở cửa sổ chọn ngày
    private void openDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            currentCalendar.set(Calendar.YEAR, year);
            currentCalendar.set(Calendar.MONTH, month);
            currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            transactionViewModel.setSelectedDate(currentCalendar); // Gửi ngày mới cho ViewModel
        };

        new DatePickerDialog(getContext(), dateSetListener,
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // --- Cập nhật Biểu đồ PieChart ---

    // Cài đặt ban đầu
    private void setupPieChart() {
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setExtraOffsets(5, 10, 5, 5);
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.TRANSPARENT);
        binding.pieChart.setTransparentCircleRadius(61f);
        binding.pieChart.setRotationAngle(0);
        binding.pieChart.setRotationEnabled(true);
        binding.pieChart.setHighlightPerTapEnabled(true);
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad);
        binding.pieChart.getLegend().setEnabled(false); // Ẩn chú thích
    }

    // Cập nhật dữ liệu mới cho PieChart
    private void updatePieChart(List<CategoryNameSum> dataList, double grandTotal) {
        // Chuyển đổi dataList sang PieEntry
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (CategoryNameSum item : dataList) {
            if (item.totalAmount > 0) {
                entries.add(new PieEntry((float) item.totalAmount, item.categoryName));
                // Lấy màu từ dữ liệu
                try {
                    colors.add(Color.parseColor(item.categoryColor));
                } catch (Exception e) {
                    colors.add(Color.GRAY); // Màu dự phòng
                }
            }
        }

        if (entries.isEmpty()) {
            binding.pieChart.clear();
            binding.pieChart.setCenterText("No Data");
            binding.pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors); // Sử dụng màu từ database

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChart));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        binding.pieChart.setData(data);
        binding.pieChart.setCenterText(FormatUtils.formatCurrency(grandTotal)); // Set tổng tiền ở giữa
        binding.pieChart.setCenterTextSize(16f);
        binding.pieChart.setCenterTextColor(Color.BLACK);
        binding.pieChart.animateY(1000, Easing.EaseInOutQuad);
        binding.pieChart.invalidate();
    }

    // Hủy binding khi Fragment bị hủy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}