package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private TransactionViewModel transactionViewModel;

    // Đây là các view từ layout "activity_add_transaction.xml" của bạn
    private EditText inputDate; // Giao diện của bạn là inputA
    private EditText inputAmount; // Giao diện của bạn là inputB
    private EditText inputNote;   // Giao diện của bạn là editText1
    private Button btnSave;       // Giao diện của bạn là btnsave

    // Biến cho DatePicker
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Gắn layout "Thêm Giao Dịch" (mà chúng ta đã di dời)
        setContentView(R.layout.activity_add_transaction);

        // 2. Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // 3. Ánh xạ các View
        // Tôi dùng ID từ file layout bạn gửi
        inputDate = findViewById(R.id.inputA);
        inputAmount = findViewById(R.id.inputB);
        inputNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);

        // 4. Cài đặt DatePicker cho ô "Time" (inputA)
        setupDatePicker();

        // 5. Gán sự kiện cho nút "Save expense"
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupDatePicker() {
        // Tạo một listener
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(); // Cập nhật text cho EditText
        };

        // Gán sự kiện click cho EditText "Time"
        inputDate.setOnClickListener(v -> {
            new DatePickerDialog(AddTransactionActivity.this, dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Tắt focus để người dùng không gõ tay vào được
        inputDate.setFocusable(false);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Định dạng ngày
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void saveTransaction() {
        // Lấy dữ liệu từ các ô input
        String date = inputDate.getText().toString().trim();
        String amountStr = inputAmount.getText().toString().trim();
        String note = inputNote.getText().toString().trim();

        // Kiểm tra dữ liệu (đơn giản)
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Ngày và Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // ---- SỬA LỖI Ở ĐÂY ----

            // 1. LẤY DỮ LIỆU ĐÚNG:
            // Lấy timestamp (kiểu long) từ Calendar (đã có sẵn trong 'myCalendar')
            long timestamp = myCalendar.getTimeInMillis();

            // 2. Hardcode một categoryId (kiểu long)
            // (Theo phương châm "chạy đủ", ta hardcode là 1L, giả sử 1L là "Ăn uống")
            long categoryId = 1L;

            // 3. GỌI CONSTRUCTOR ĐÚNG (mới thêm ở Bước 1)
            // (amount, type, transactionDate, description, categoryId)
            Transaction newTransaction = new Transaction(amount, "expense", timestamp, note, categoryId);

            // 4. Dùng ViewModel để lưu vào database
            transactionViewModel.insert(newTransaction);

            // 5. Thông báo thành công và đóng Activity
            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
            finish(); // Tự động quay về màn hình danh sách

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}