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
import com.example.dack1.data.model.CategorySum;
import com.example.dack1.ui.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private TransactionViewModel transactionViewModel;
    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gắn layout "fragment_chart.xml"
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ PieChart từ layout
        pieChart = view.findViewById(R.id.pie_chart);

        // 2. Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // 3. Cấu hình biểu đồ
        setupPieChart();

        // 4. Lắng nghe dữ liệu từ ViewModel
        observeChartData();
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
        transactionViewModel.getExpenseSumByCategory().observe(getViewLifecycleOwner(), categorySums -> {
            Log.d("ChartFragment", "Observer được gọi! Số lượng categorySums: " + (categorySums != null ? categorySums.size() : "null")); // <-- Thêm dòng log
            if (categorySums != null && !categorySums.isEmpty()) {
                // 5. Chuyển đổi List<CategorySum> thành List<PieEntry>
                List<PieEntry> entries = new ArrayList<>();
                for (CategorySum sum : categorySums) {
                    // Nợ kỹ thuật: Chúng ta chưa có tên Category
                    // Tạm thời hiển thị "DM: 1" (Danh mục 1) thay vì "Ăn uống"
                    String label = "DM: " + sum.categoryId;
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
                pieChart.setCenterText("Không có dữ liệu chi tiêu");
                pieChart.invalidate();
            }
        });
    }
}