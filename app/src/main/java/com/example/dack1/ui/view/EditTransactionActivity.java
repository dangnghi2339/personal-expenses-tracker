package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import Log
import android.widget.ArrayAdapter; // Import ArrayAdapter
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;      // Import Spinner
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dack1.R;
import com.example.dack1.data.model.Category; // Import Category
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.viewmodel.CategoryViewModel; // Import CategoryViewModel
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;   // Import ArrayList
import java.util.Calendar;
import java.util.List;        // Import List
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel; // ViewModel cho danh mục

    // Views từ layout "activity_add_transaction.xml" (tái sử dụng)
    private EditText inputDate;
    private EditText inputAmount;
    private EditText inputNote;
    private Button btnSave; // Sẽ đổi tên thành "Update"
    private Spinner spinnerCategory; // Spinner cho danh mục

    private Calendar myCalendar = Calendar.getInstance();

    // Biến xử lý dữ liệu
    private Transaction currentTransaction; // Giao dịch đang được sửa
    private long transactionId = -1;
    private List<Category> categoryList = new ArrayList<>(); // Danh sách các danh mục có sẵn
    private ArrayAdapter<String> categoryAdapter; // Adapter cho spinner
    private boolean isDataLoaded = false; // Cờ để tránh load dữ liệu nhiều lần

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout từ AddTransactionActivity
        setContentView(R.layout.activity_add_transaction);

        // Khởi tạo ViewModels
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Ánh xạ Views
        inputDate = findViewById(R.id.inputA);
        inputAmount = findViewById(R.id.inputB);
        inputNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);
        spinnerCategory = findViewById(R.id.spinner_category); // Ánh xạ Spinner

        // Cài đặt các thành phần UI
        setupDatePicker();
        setupCategorySpinner(); // Cài đặt adapter cho spinner

        // Lấy Transaction ID từ Intent
        transactionId = getIntent().getLongExtra("TRANSACTION_ID", -1);

        // Kiểm tra ID hợp lệ
        if (transactionId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
            return;
        }

        // Observe danh sách Category trước để đổ dữ liệu vào spinner
        observeCategories();

        // Đổi chữ nút Save thành Update và gán sự kiện click
        btnSave.setText("Update expense");
        btnSave.setOnClickListener(v -> updateTransaction());
    }

    /**
     * Cài đặt ArrayAdapter cho Spinner danh mục.
     */
    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    /**
     * Lắng nghe LiveData danh sách Category từ ViewModel và cập nhật Spinner.
     * Sau khi danh sách Category được tải, gọi hàm để tải dữ liệu Transaction.
     */
    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                categoryList = categories; // Lưu lại danh sách Category đầy đủ
                List<String> categoryNames = new ArrayList<>();
                for (Category cat : categories) {
                    categoryNames.add(cat.getName()); // Chỉ lấy tên để hiển thị
                }
                // Cập nhật Spinner
                categoryAdapter.clear();
                categoryAdapter.addAll(categoryNames);
                categoryAdapter.notifyDataSetChanged();

                // Quan trọng: Chỉ gọi loadTransactionData SAU KHI spinner đã có dữ liệu category
                // để đảm bảo có thể setSelection chính xác trong populateUi
                loadTransactionData();
            }
        });
    }

    /**
     * Lắng nghe LiveData của Transaction cần sửa từ ViewModel.
     * Chỉ gọi populateUi một lần khi dữ liệu hợp lệ được trả về.
     */
    private void loadTransactionData() {
        transactionViewModel.getTransactionById(transactionId).observe(this, transaction -> {
            // Chỉ đổ dữ liệu lên UI lần đầu tiên và khi transaction khác null
            if (transaction != null && !isDataLoaded) {
                currentTransaction = transaction;
                populateUi(transaction); // Đổ dữ liệu lên các trường input
                isDataLoaded = true; // Đánh dấu đã load xong để tránh gọi lại populateUi
            } else if (transaction == null && !isDataLoaded) {
                // Xử lý trường hợp không tìm thấy transaction ngay lần đầu
                Log.e("EditTransactionActivity", "Transaction với ID " + transactionId + " không tồn tại.");
                Toast.makeText(this, "Lỗi: Không tải được chi tiết giao dịch", Toast.LENGTH_SHORT).show();
                finish(); // Đóng nếu không tìm thấy
            }
            // Không cần làm gì nếu transaction là null sau khi isDataLoaded=true (có thể do bị xóa)
        });
    }


    /**
     * Đổ dữ liệu từ Transaction đã tải lên các trường EditText và Spinner.
     */
    private void populateUi(Transaction transaction) {
        if (transaction == null) return; // Kiểm tra an toàn

        // Đổ dữ liệu vào EditTexts
        inputAmount.setText(String.valueOf(transaction.getAmount()));
        inputNote.setText(transaction.getDescription());

        // Cập nhật Calendar và hiển thị ngày tháng
        myCalendar.setTimeInMillis(transaction.getTransactionDate());
        updateLabel(); // Cập nhật EditText ngày

        // Tìm và chọn Category cũ trong Spinner
        long oldCategoryId = transaction.getCategoryId();
        int spinnerPosition = -1;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == oldCategoryId) {
                spinnerPosition = i;
                break;
            }
        }
        // Set selection cho Spinner nếu tìm thấy vị trí
        if (spinnerPosition != -1) {
            spinnerCategory.setSelection(spinnerPosition);
        } else {
            // Xử lý nếu Category cũ không còn tồn tại trong danh sách
            Log.w("EditTransactionActivity", "Không tìm thấy Category cũ ID: " + oldCategoryId + " trong Spinner. Kích thước list: " + categoryList.size());
            // Chọn item đầu tiên làm mặc định nếu danh sách không rỗng
            if (!categoryList.isEmpty()) {
                spinnerCategory.setSelection(0);
                Toast.makeText(this, "Danh mục cũ không còn, chọn mặc định.", Toast.LENGTH_SHORT).show();
            } else {
                // Trường hợp không có category nào trong DB
                Toast.makeText(this, "Lỗi: Không có danh mục nào để chọn.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Xử lý logic khi bấm nút "Update expense".
     * Lấy dữ liệu mới, validate, cập nhật đối tượng currentTransaction và gọi ViewModel.
     */
    private void updateTransaction() {
        // Kiểm tra xem dữ liệu gốc đã được load chưa
        if (currentTransaction == null) {
            Toast.makeText(this, "Lỗi: Dữ liệu giao dịch gốc chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu mới từ các trường input
        String amountStr = inputAmount.getText().toString().trim();
        String note = inputNote.getText().toString().trim();
        int selectedPosition = spinnerCategory.getSelectedItemPosition();

        // --- VALIDATION ---
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra Spinner hợp lệ
        if (selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            Toast.makeText(this, "Vui lòng chọn một danh mục hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- TRY UPDATING ---
        try {
            // Chuyển đổi dữ liệu
            double amount = Double.parseDouble(amountStr);
            long timestamp = myCalendar.getTimeInMillis(); // Lấy timestamp đã được cập nhật (nếu người dùng đổi ngày)
            long selectedCategoryId = categoryList.get(selectedPosition).getId(); // Lấy ID của Category mới

            // Cập nhật đối tượng currentTransaction với dữ liệu mới
            currentTransaction.setAmount(amount);
            currentTransaction.setDescription(note);
            currentTransaction.setTransactionDate(timestamp);
            currentTransaction.setCategoryId(selectedCategoryId); // <-- Cập nhật Category ID mới
            // currentTransaction.setType(...); // Cập nhật Type nếu bạn thêm UI cho nó

            // Gọi ViewModel để cập nhật vào database
            transactionViewModel.update(currentTransaction);

            // Thông báo và đóng Activity
            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            // Phòng trường hợp lỗi khi lấy category từ list
            Toast.makeText(this, "Lỗi: Không thể lấy thông tin danh mục đã chọn.", Toast.LENGTH_SHORT).show();
            Log.e("EditTransactionActivity", "Lỗi IndexOutOfBoundsException khi lấy category đã chọn", e);
        }
    }

    // --- Các hàm Helper cho DatePicker (Giống AddTransactionActivity) ---

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
        // Ngăn người dùng gõ tay vào ô ngày tháng
        inputDate.setFocusable(false);
        inputDate.setFocusableInTouchMode(false);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Định dạng ngày tháng
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }
}