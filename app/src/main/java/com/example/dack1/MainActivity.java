package com.example.dack1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timeTextView; // Giả sử đây là TextView/EditText dùng để hiển thị ngày tháng
    private Calendar calendar; // Dùng để lưu trữ ngày tháng hiện tại và sau khi chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Khởi tạo đối tượng Calendar
        calendar = Calendar.getInstance();

        // 2. Tham chiếu đến View hiển thị thời gian (Time)
        // **Lưu ý:** Bạn cần thay thế R.id.time_display_view bằng ID thực tế trong layout XML của bạn.
        timeTextView = findViewById(R.id.time_display_view);

        // 3. Hiển thị ngày hiện tại ban đầu (Tùy chọn)
        updateDateTextView();

        // 4. Thiết lập sự kiện click cho TextView/View 'Time'
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    /**
     * Hàm hiển thị DatePickerDialog
     */
    private void showDatePickerDialog() {
        // Lấy ngày, tháng, năm hiện tại từ đối tượng Calendar
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, // Context
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // Khi người dùng chọn xong ngày tháng
                        // Cập nhật lại đối tượng Calendar
                        calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                        // Cập nhật hiển thị lên TextView
                        updateDateTextView();
                    }
                },
                year, month, day // Thiết lập ngày tháng ban đầu cho lịch
        );
        datePickerDialog.show();
    }

    /**
     * Hàm cập nhật TextView với ngày tháng đã chọn
     */
    private void updateDateTextView() {
        // Định dạng ngày tháng theo yêu cầu, ví dụ: "August 12, 2024"
        // Locale.US hoặc Locale.getDefault() (tùy thuộc vào định dạng muốn hiển thị)
        String dateFormat = "MMMM dd, yyyy"; // Định dạng như trong hình ảnh
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat, Locale.US);

        timeTextView.setText(sdf.format(calendar.getTime()));
    }
}