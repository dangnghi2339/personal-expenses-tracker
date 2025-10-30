package com.example.dack1.ui.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem; // Import này
import androidx.annotation.NonNull; // Import này
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dack1.R;
import com.example.dack1.data.model.MonthlyCategorySummary;
import com.example.dack1.databinding.ActivityCategoryDetailBinding; // ViewBinding cho layout mới
import com.example.dack1.ui.adapter.TransactionAdapter; // Tái sử dụng Adapter của bạn
import com.example.dack1.ui.viewmodel.CategoryDetailViewModel; // ViewModel mới
import com.example.dack1.util.FormatUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.example.dack1.ui.view.BaseActivity;

public class CategoryDetailActivity extends BaseActivity {

    // Các key để truyền Intent
    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";
    public static final String EXTRA_CATEGORY_NAME = "EXTRA_CATEGORY_NAME";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_START_DATE = "EXTRA_START_DATE";
    public static final String EXTRA_END_DATE = "EXTRA_END_DATE";

    private ActivityCategoryDetailBinding binding;
    private CategoryDetailViewModel viewModel;
    private TransactionAdapter transactionAdapter; // Tái sử dụng TransactionAdapter

    private long categoryId;
    private String categoryName;
    private String type;
    private long startDate;
    private long endDate;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Nhận dữ liệu từ Intent
        if (!getIntentData()) {
            finish(); // Nếu không có dữ liệu, đóng Activity
            return;
        }

        // 2. Cài đặt Toolbar
        setupToolbar();

        // 3. Cài đặt RecyclerView (Tái sử dụng adapter)
        setupRecyclerView();

        // 4. Cài đặt ViewModel
        setupViewModel();

        // 5. Cài đặt Observers
        setupObservers();

        // 6. Cài đặt Biểu đồ cột
        setupBarChart();

        // 7. Khởi chạy ViewModel với dữ liệu
        viewModel.init(categoryId, type, startDate, endDate);
    }

    private boolean getIntentData() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_CATEGORY_ID)) {
            return false;
        }
        categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, -1);
        categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);
        type = intent.getStringExtra(EXTRA_TYPE);
        startDate = intent.getLongExtra(EXTRA_START_DATE, 0);
        endDate = intent.getLongExtra(EXTRA_END_DATE, 0);

        return categoryId != -1;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Cập nhật tiêu đề dựa trên Figma
            binding.toolbar.setTitle(categoryName + " Details");
        }
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(); // Khởi tạo adapter
        binding.rvTransactionDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTransactionDetails.setAdapter(transactionAdapter);
        // (Bạn có thể thêm OnClickListener cho item ở đây nếu muốn mở EditTransactionActivity)
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CategoryDetailViewModel.class);
    }

    private void setupObservers() {
        // 1. Lắng nghe danh sách giao dịch
        viewModel.transactionList.observe(this, transactions -> {
            transactionAdapter.submitList(transactions); // Cập nhật adapter

            // Tính tổng số tiền và cập nhật UI
            totalAmount = 0.0;
            for (com.example.dack1.data.model.Transaction t : transactions) {
                totalAmount += t.getAmount();
            }
            binding.tvCategoryTotalAmount.setText(FormatUtils.formatCurrency(totalAmount));
            binding.tvCategoryNameDetail.setText(categoryName + " in " + FormatUtils.formatMonthYear(startDate)); // Giả định là xem theo tháng

            // Set màu
            int colorRes = (type.equals("income")) ? R.color.green : R.color.red;
            binding.tvCategoryTotalAmount.setTextColor(ContextCompat.getColor(this, colorRes));
        });

        // 2. Lắng nghe dữ liệu biểu đồ cột
        viewModel.barChartData.observe(this, summaries -> {
            updateBarChart(summaries);
        });
    }

    // --- Logic cho BarChart ---

    private void setupBarChart() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.setDrawValueAboveBar(true);
        binding.barChart.setExtraOffsets(0, 0, 0, 10); // Thêm khoảng trống ở dưới cho X-Axis

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45); // Xoay label nếu cần
    }

    private void updateBarChart(List<MonthlyCategorySummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            binding.barChart.clear();
            binding.barChart.invalidate();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (MonthlyCategorySummary summary : summaries) {
            entries.add(new BarEntry(i, (float) summary.totalAmount));
            // Lấy 2 số cuối của năm và tháng (ví dụ: 25-10)
            String[] parts = summary.monthYear.split("-");
            if(parts.length == 2) {
                labels.add(parts[1] + "/" + parts[0].substring(2)); // "10/25"
            } else {
                labels.add(summary.monthYear);
            }
            i++;
        }

        // Gán labels cho XAxis
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(labels.size());

        BarDataSet dataSet = new BarDataSet(entries, categoryName);

        // Set màu cho BarChart
        int color = (type.equals("income")) ?
                ContextCompat.getColor(this, R.color.green) :
                ContextCompat.getColor(this, R.color.red);
        dataSet.setColor(color);

        BarData barData = new BarData(dataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.6f);

        binding.barChart.setData(barData);
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }


    // Xử lý khi bấm nút back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}