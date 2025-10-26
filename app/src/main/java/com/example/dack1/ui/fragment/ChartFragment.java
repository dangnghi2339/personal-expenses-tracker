package com.example.dack1.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.data.model.MonthlySummary;
import com.example.dack1.ui.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private TransactionViewModel transactionViewModel;
    private PieChart pieChart;
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gắn layout "fragment_chart.xml"
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ Charts từ layout
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);

        // 2. Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // 3. Cấu hình biểu đồ
        setupPieChart();
        setupBarChart();

        // 4. Lắng nghe dữ liệu từ ViewModel
        observeChartData();
        observeMonthlyData();
    }

    private void setupPieChart() {
        pieChart.setDescription(null); // Tắt mô tả
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false); // Tắt chú thích
    }

    private void observeChartData() {
        // Gọi hàm mới mà chúng ta đã tạo
        transactionViewModel.getExpenseSumByCategoryName().observe(getViewLifecycleOwner(), categorySums -> {
            Log.d("ChartFragment", "Observer được gọi! Số lượng categorySums: " + (categorySums != null ? categorySums.size() : "null")); // <-- Thêm dòng log
            if (categorySums != null && !categorySums.isEmpty()) {
                // 5. Chuyển đổi List<CategorySum> thành List<PieEntry>
                List<PieEntry> entries = new ArrayList<>();
                for (CategoryNameSum sum : categorySums) {
                    String label = sum.categoryName != null ? sum.categoryName : getString(R.string.unknown_category);
                    entries.add(new PieEntry((float) sum.total, label));
                }

                // 6. Tạo DataSet và gán màu
                PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu theo danh mục");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextSize(14f);
                dataSet.setValueTextColor(Color.BLACK);
                Log.d("ChartFragment", "Đang cập nhật biểu đồ...");
                // 7. Gán DataSet vào PieData
                PieData pieData = new PieData(dataSet);

                // 8. Đặt dữ liệu cho biểu đồ và "vẽ"
                pieChart.setData(pieData);
                pieChart.invalidate(); // Vẽ lại biểu đồ
            } else {
                Log.d("ChartFragment", "Không có dữ liệu, đang xóa biểu đồ...");
                // Nếu không có dữ liệu, xóa biểu đồ cũ
                pieChart.clear();
                            pieChart.setCenterText(getString(R.string.no_expense_data));
                pieChart.invalidate();
            }
        });
    }

    private void setupBarChart() {
        barChart.setDescription(null);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(12);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        // Configure X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);

        // Configure Y-axis
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setEnabled(false);

        // Configure legend
        barChart.getLegend().setEnabled(true);
        barChart.getLegend().setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        barChart.getLegend().setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
    }

    private void observeMonthlyData() {
        // Get start date for last 12 months
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, -12);
        long startDate = calendar.getTimeInMillis();

        transactionViewModel.getMonthlySummaries(startDate).observe(getViewLifecycleOwner(), monthlySummaries -> {
            if (monthlySummaries != null && !monthlySummaries.isEmpty()) {
                setupBarChartData(monthlySummaries);
            } else {
                barChart.clear();
                barChart.invalidate();
            }
        });
    }

    private void setupBarChartData(List<MonthlySummary> monthlySummaries) {
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Reverse the list to show oldest to newest
        List<MonthlySummary> reversedList = new ArrayList<>(monthlySummaries);
        java.util.Collections.reverse(reversedList);

        for (int i = 0; i < reversedList.size(); i++) {
            MonthlySummary summary = reversedList.get(i);
            incomeEntries.add(new BarEntry(i, (float) summary.totalIncome));
            expenseEntries.add(new BarEntry(i, (float) summary.totalExpense));
            
            // Format month label (e.g., "2024-01" -> "01/24")
            String[] parts = summary.monthYear.split("-");
            if (parts.length == 2) {
                labels.add(parts[1] + "/" + parts[0].substring(2));
            } else {
                labels.add(summary.monthYear);
            }
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Thu nhập");
        incomeDataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        incomeDataSet.setValueTextSize(10f);

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Chi tiêu");
        expenseDataSet.setColor(ColorTemplate.MATERIAL_COLORS[1]);
        expenseDataSet.setValueTextSize(10f);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);
        barData.setValueFormatter(new com.github.mikephil.charting.formatter.LargeValueFormatter());

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.invalidate();
    }
}