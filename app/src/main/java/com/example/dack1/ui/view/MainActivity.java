package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ViewModel để xử lý logic
    private TransactionViewModel transactionViewModel;

    // Các thành phần UI
    private EditText editTextTime, editTextAmount, editTextNote;
    private Button btnSave;
    private ImageButton selectedCategoryButton = null; // Lưu lại nút danh mục đang được chọn

    // Biến để lưu trữ dữ liệu người dùng chọn
    private Calendar selectedDate = Calendar.getInstance();
    private long selectedCategoryId = -1; // -1 nghĩa là chưa có danh mục nào được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Ánh xạ các View từ layout
        initViews();

        // Cập nhật ngày hiện tại lên EditText Time
        updateDateInView();

        // Thiết lập các sự kiện lắng nghe cho các nút bấm
        setupEventListeners();
    }

    private void initViews() {
        editTextTime = findViewById(R.id.inputA); // Sửa lại ID nếu cần
        editTextAmount = findViewById(R.id.inputB);
        editTextNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);
        // Ngăn người dùng gõ chữ vào ô Time
        editTextTime.setFocusable(false);
    }

    private void setupEventListeners() {
        // Sự kiện khi nhấn vào ô Time
        editTextTime.setOnClickListener(v -> showDatePickerDialog());

        // Sự kiện khi nhấn vào nút Save
        btnSave.setOnClickListener(v -> saveTransaction());

        // Sự kiện cho các nút danh mục
        setupCategoryClickListeners();
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateInView();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editTextTime.setText(sdf.format(selectedDate.getTime()));
    }

    private void setupCategoryClickListeners() {
        // Tạo một listener chung cho tất cả các nút danh mục
        View.OnClickListener categoryClickListener = view -> {
            // 1. Đặt lại màu nền của nút đã chọn trước đó (nếu có)
            if (selectedCategoryButton != null) {
                selectedCategoryButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_imagebutton));
            }

            // 2. Cập nhật nút được chọn mới và thay đổi màu nền của nó
            selectedCategoryButton = (ImageButton) view;
            selectedCategoryButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Dùng màu xanh

            // 3. Lấy ID của danh mục tương ứng
            // **Quan trọng**: Chúng ta sẽ gán ID danh mục cứng ở đây để test.
            // Sau này, chúng ta sẽ lấy ID này từ CSDL.
            if (view.getId() == R.id.imageButton2) {
                selectedCategoryId = 1; // Market
            } else if (view.getId() == R.id.imageButton3) {
                selectedCategoryId = 2; // Eat and drink
            } else if (view.getId() == R.id.imageButton4) {
                selectedCategoryId = 3; // Shopping
            }
            // ... thêm các else if cho các nút còn lại
        };

        // Gán listener này cho tất cả các ImageButton
        findViewById(R.id.imageButton2).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton3).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton4).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton5).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton6).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton7).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton8).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton9).setOnClickListener(categoryClickListener);
        findViewById(R.id.imageButton10).setOnClickListener(categoryClickListener);
    }

    private void saveTransaction() {
        String amountStr = editTextAmount.getText().toString();

        // --- BƯỚC KIỂM TRA DỮ LIỆU (VALIDATION) ---
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng chọn một danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- BƯỚC TẠO ĐỐI TƯỢNG ---
        Transaction newTransaction = new Transaction();
        newTransaction.amount = Double.parseDouble(amountStr);
        newTransaction.description = editTextNote.getText().toString();
        newTransaction.transactionDate = selectedDate.getTimeInMillis();
        newTransaction.categoryId = selectedCategoryId;
        newTransaction.type = "EXPENSE"; // Tạm thời mặc định là chi tiêu

        // --- BƯỚC GỌI VIEWMODEL ĐỂ LƯU ---
        transactionViewModel.insert(newTransaction);

        // --- BƯỚC PHẢN HỒI VÀ DỌN DẸP ---
        Toast.makeText(this, "Đã lưu giao dịch thành công!", Toast.LENGTH_SHORT).show();
        clearInputFields();
    }

    private void clearInputFields() {
        editTextAmount.setText("");
        editTextNote.setText("");
        // Đặt lại lựa chọn danh mục
        if (selectedCategoryButton != null) {
            selectedCategoryButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_imagebutton));
        }
        selectedCategoryId = -1;
        selectedCategoryButton = null;
    }
}