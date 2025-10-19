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

public class EditTransactionActivity extends AppCompatActivity {

    private TransactionViewModel transactionViewModel;

    // Các view từ layout "activity_add_transaction.xml"
    private EditText inputDate; // Giao diện của bạn là inputA
    private EditText inputAmount; // Giao diện của bạn là inputB
    private EditText inputNote;   // Giao diện của bạn là editText1
    private Button btnSave;       // Giao diện của bạn là btnsave

    private Calendar myCalendar = Calendar.getInstance();

    // Biến để lưu giao dịch đang sửa
    private Transaction currentTransaction;
    private long transactionId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. TÁI SỬ DỤNG layout của màn hình Thêm
        setContentView(R.layout.activity_add_transaction);

        // 2. Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // 3. Ánh xạ các View
        inputDate = findViewById(R.id.inputA);
        inputAmount = findViewById(R.id.inputB);
        inputNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);

        // 4. Cài đặt DatePicker
        setupDatePicker();

        // 5. Lấy ID từ Intent
        transactionId = getIntent().getLongExtra("TRANSACTION_ID", -1);

        if (transactionId == -1) {
            // Nếu không có ID, đây là lỗi
            Toast.makeText(this, "Lỗi: Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 6. Lấy dữ liệu giao dịch từ DB và "Observe"
        transactionViewModel.getTransactionById(transactionId).observe(this, transaction -> {
            if (transaction != null) {
                currentTransaction = transaction;
                populateUi(transaction); // Đổ dữ liệu lên UI
            }
        });

        // 7. Gán sự kiện cho nút "Save" (giờ là nút Update)
        btnSave.setText("Update expense"); // Đổi tên nút
        btnSave.setOnClickListener(v -> updateTransaction());
    }

    /**
     * Hàm này đổ dữ liệu của Transaction lên các EditText
     */
    private void populateUi(Transaction transaction) {
        inputAmount.setText(String.valueOf(transaction.getAmount()));
        inputNote.setText(transaction.getDescription());

        // Chuyển long timestamp thành ngày tháng (String)
        myCalendar.setTimeInMillis(transaction.getTransactionDate());
        updateLabel();
    }

    private void updateTransaction() {
        String amountStr = inputAmount.getText().toString().trim();
        String note = inputNote.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // Lấy timestamp từ Calendar (đã được cập nhật bởi DatePicker)
            long timestamp = myCalendar.getTimeInMillis();

            // Cập nhật các trường của object
            currentTransaction.setAmount(amount);
            currentTransaction.setDescription(note);
            currentTransaction.setTransactionDate(timestamp);
            // (Chúng ta vẫn chưa sửa Category, nên cứ để nguyên)

            // Gọi ViewModel để cập nhật
            transactionViewModel.update(currentTransaction);

            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Hai hàm helper cho DatePicker (giống hệt AddTransactionActivity) ---

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        inputDate.setOnClickListener(v -> {
            new DatePickerDialog(EditTransactionActivity.this, dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        inputDate.setFocusable(false);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }
}