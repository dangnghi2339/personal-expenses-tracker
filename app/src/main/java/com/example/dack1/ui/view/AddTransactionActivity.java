package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.ArrayAdapter; // Cho Spinner
import android.widget.Spinner;      // Cho Spinner
import com.example.dack1.data.model.Category; // Model Category
import com.example.dack1.ui.viewmodel.CategoryViewModel; // ViewModel Category
import java.util.ArrayList;   // Cho list tên Category
import java.util.List;        // Cho list tên Category
public class AddTransactionActivity extends AppCompatActivity {
    private CategoryViewModel categoryViewModel; // ViewModel mới
    private Spinner spinnerCategory;             // Spinner mới
    private List<Category> categoryList = new ArrayList<>(); // List để giữ Category objects
    private ArrayAdapter<String> categoryAdapter; // Adapter cho Spinner
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
        // Đặt gần chỗ khởi tạo transactionViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        // 3. Ánh xạ các View
        // Tôi dùng ID từ file layout bạn gửi
        inputDate = findViewById(R.id.inputA);
        inputAmount = findViewById(R.id.inputB);
        inputNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);
        spinnerCategory = findViewById(R.id.spinner_category);
        // 4. Cài đặt DatePicker cho ô "Time" (inputA)
        setupDatePicker();
        setupCategorySpinner();
        observeCategories();
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

        // Lấy vị trí item được chọn trong Spinner
        int selectedPosition = spinnerCategory.getSelectedItemPosition();

        // --- VALIDATION ---
        // Kiểm tra ngày và số tiền
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Ngày và Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem có Category nào được chọn không
        // AdapterView.INVALID_POSITION thường là -1
        if (selectedPosition < 0 || spinnerCategory.getSelectedItem() == null || selectedPosition >= categoryList.size() ) {
            Toast.makeText(this, "Vui lòng chọn một danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- TRY SAVING ---
        try {
            // Chuyển đổi số tiền
            double amount = Double.parseDouble(amountStr);

            // Lấy timestamp từ Calendar đã chọn
            long timestamp = myCalendar.getTimeInMillis();

            // Lấy categoryId từ list Category dựa vào vị trí chọn trong Spinner
            long selectedCategoryId = categoryList.get(selectedPosition).getId();

            // Chọn loại giao dịch (tạm thời hardcode là 'expense')
            // Trong tương lai, bạn có thể thêm RadioButton để chọn Thu/Chi
            String transactionType = "expense";

            // Tạo đối tượng Transaction mới với dữ liệu chính xác
            Transaction newTransaction = new Transaction(amount, transactionType, timestamp, note, selectedCategoryId);

            // Dùng ViewModel để lưu vào database
            transactionViewModel.insert(newTransaction);

            // Thông báo thành công và đóng Activity
            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
            finish(); // Tự động quay về màn hình danh sách

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            // Xử lý trường hợp không lấy được category (dù đã check ở trên)
            Toast.makeText(this, "Lỗi: Không thể lấy thông tin danh mục", Toast.LENGTH_SHORT).show();
            Log.e("AddTransactionActivity", "Lỗi IndexOutOfBoundsException khi lấy category", e);
        }
    }
    private void setupCategorySpinner() {
        // Adapter này sẽ hiển thị tên Category trong Spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                categoryList = categories; // Lưu lại list Category objects
                List<String> categoryNames = new ArrayList<>();
                for (Category cat : categories) {
                    categoryNames.add(cat.getName()); // Chỉ lấy tên để hiển thị
                }
                // Cập nhật dữ liệu cho Spinner Adapter
                categoryAdapter.clear();
                categoryAdapter.addAll(categoryNames);
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }
}