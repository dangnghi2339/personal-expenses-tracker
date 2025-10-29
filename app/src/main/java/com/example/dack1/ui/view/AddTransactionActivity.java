package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.adapter.CategoryGridAdapter;
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ArrayAdapter; // Cho Spinner
import android.widget.RadioGroup;
import android.widget.Spinner;      // Cho Spinner
import com.example.dack1.data.model.Category; // Model Category
import com.example.dack1.ui.viewmodel.CategoryViewModel; // ViewModel Category
import java.util.ArrayList;   // Cho list tên Category
import java.util.List;        // Cho list tên Category
import com.example.dack1.ui.view.BaseActivity;

public class AddTransactionActivity extends BaseActivity {
    private CategoryViewModel categoryViewModel; // ViewModel mới
       // Spinner mới
    private List<Category> categoryList = new ArrayList<>(); // List để giữ Category objects
    private List<Category> allCategories = new ArrayList<>(); // List tất cả categories
    private RecyclerView rvCategoryGrid;
    private CategoryGridAdapter categoryGridAdapter;
    private long selectedCategoryIdFromGrid = -1; // Biến lưu ID category được chọn
    private Button btnEditCategories; // Nút Edit mới
    private TransactionViewModel transactionViewModel;
    private RadioGroup rgTransactionType;
    private RadioButton rbExpense, rbIncome;
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
        rvCategoryGrid = findViewById(R.id.rv_category_grid);
        btnEditCategories = findViewById(R.id.btn_edit_categories); // Ánh xạ nút Edit


        rgTransactionType = findViewById(R.id.rg_transaction_type);
        rbExpense = findViewById(R.id.rb_expense);
        rbIncome = findViewById(R.id.rb_income);
        // 4. Cài đặt DatePicker cho ô "Time" (inputA)
        setupDatePicker();
        setupCategoryGrid();
        setupTransactionTypeListener();
        observeCategories();
        // 5. Gán sự kiện cho nút "Save expense"
        btnSave.setOnClickListener(v -> saveTransaction());

        btnEditCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, CostalActivity.class));
            // Không finish() vì người dùng có thể quay lại
        });
        // Cập nhật Placeholder
        inputAmount.setHint("Enter the amount");
        inputNote.setHint("Enter notes");
        inputDate.setHint("Select date");
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



        // --- VALIDATION ---
        // Kiểm tra ngày và số tiền
        if (selectedCategoryIdFromGrid == -1) {
            Toast.makeText(this, "Vui lòng chọn một danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Ngày và Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem có Category nào được chọn không
        // AdapterView.INVALID_POSITION thường là -1


        // --- TRY SAVING ---
        try {
            // Chuyển đổi số tiền
            double amount = Double.parseDouble(amountStr);

            // Lấy timestamp từ Calendar đã chọn
            long timestamp = myCalendar.getTimeInMillis();

            // Lấy categoryId từ list Category dựa vào vị trí chọn trong Spinner
            long selectedCategoryId = selectedCategoryIdFromGrid;

            // Lấy loại giao dịch từ RadioGroup
            String transactionType = getSelectedTransactionType();

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
    private void setupCategoryGrid() {
        categoryGridAdapter = new CategoryGridAdapter();

        // Lắng nghe khi người dùng chọn category trên lưới
        categoryGridAdapter.setOnCategorySelectedListener(categoryId -> {
            selectedCategoryIdFromGrid = categoryId;
        });

        // Thiết lập GridLayoutManager với 3 cột
        rvCategoryGrid.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategoryGrid.setAdapter(categoryGridAdapter);
    }
    private void setupTransactionTypeListener() {
        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            // Lấy màu từ resources
            int selectedColor = ContextCompat.getColor(this, R.color.colorPrimary); // Màu xanh đậm
            int unselectedColorBg = Color.parseColor("#D5E7FE"); // Màu xanh nhạt
            int whiteColor = Color.WHITE; // Màu trắng

            if (checkedId == R.id.rb_income) {
                // Khi chọn Income
                rbIncome.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                rbIncome.setTextColor(whiteColor);

                rbExpense.setBackgroundTintList(ColorStateList.valueOf(unselectedColorBg));
                rbExpense.setTextColor(selectedColor);

                // Cập nhật text nút Save (Tùy chọn)
                btnSave.setText("Save Income"); // Hoặc dùng R.string...

            } else { // checkedId == R.id.rb_expense
                // Khi chọn Expense
                rbExpense.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                rbExpense.setTextColor(whiteColor);

                rbIncome.setBackgroundTintList(ColorStateList.valueOf(unselectedColorBg));
                rbIncome.setTextColor(selectedColor);

                // Cập nhật text nút Save (Tùy chọn)
                btnSave.setText("Save expense"); // Hoặc dùng R.string...
            }

            // *** SỬA Ở ĐÂY: Gọi trực tiếp filterCategoriesByType ***
            // Thay vì gọi lại observeCategories(), hãy gọi filterCategoriesByType()
            // để lọc danh sách đã có (allCategories) theo type mới được chọn.
            filterCategoriesByType();

        });
    }
    private String getSelectedTransactionType() {
        int checkedId = rgTransactionType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_income) {
            return "income";
        } else {
            return "expense";
        }
    }

    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                allCategories = categories; // Lưu tất cả categories
                filterCategoriesByType(); // Filter based on selected type
            }
        });
    }

    private void filterCategoriesByType() {
        String selectedType = getSelectedTransactionType();
        categoryList.clear(); // Vẫn dùng list tạm này

        for (Category cat : allCategories) {
            if (selectedType.equalsIgnoreCase(cat.getType())) {
                categoryList.add(cat);
            }
        }

        // Submit danh sách đã lọc cho Adapter của RecyclerView
        categoryGridAdapter.submitList(new ArrayList<>(categoryList)); // Submit bản sao

        // Reset lựa chọn category khi đổi type
        selectedCategoryIdFromGrid = -1;
        categoryGridAdapter.setSelectedCategoryId(-1); // Gọi hàm mới trong adapter
    }
}