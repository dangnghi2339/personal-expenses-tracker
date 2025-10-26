package com.example.dack1.ui.view;

import android.app.DatePickerDialog;
import android.content.Intent; // Thêm import Intent
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager; // Thêm import GridLayoutManager
import androidx.recyclerview.widget.RecyclerView;     // Thêm import RecyclerView

import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.ui.adapter.CategoryGridAdapter; // Thêm import CategoryGridAdapter
import com.example.dack1.ui.viewmodel.CategoryViewModel;
import com.example.dack1.ui.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;

    // --- Views ---
    private EditText inputDate;
    private EditText inputAmount;
    private EditText inputNote;
    private Button btnSave;
    private RadioGroup rgTransactionType;
    private RadioButton rbExpense, rbIncome;
    private RecyclerView rvCategoryGrid;      // Thay Spinner bằng RecyclerView
    private Button btnEditCategories;       // Nút Edit mới

    private Calendar myCalendar = Calendar.getInstance();

    // --- Adapter & Data ---
    private Transaction currentTransaction;
    private long transactionId = -1;
    private List<Category> categoryList = new ArrayList<>(); // List danh mục đã lọc theo type
    private List<Category> allCategories = new ArrayList<>();  // List tất cả danh mục
    private CategoryGridAdapter categoryGridAdapter; // Adapter mới cho RecyclerView
    private long selectedCategoryIdFromGrid = -1; // ID category được chọn từ lưới
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout đã được cập nhật
        setContentView(R.layout.activity_add_transaction);

        // Khởi tạo ViewModels
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Ánh xạ Views
        inputDate = findViewById(R.id.inputA);
        inputAmount = findViewById(R.id.inputB);
        inputNote = findViewById(R.id.editText1);
        btnSave = findViewById(R.id.btnsave);
        rgTransactionType = findViewById(R.id.rg_transaction_type);
        rbExpense = findViewById(R.id.rb_expense);
        rbIncome = findViewById(R.id.rb_income);
        rvCategoryGrid = findViewById(R.id.rv_category_grid); // Ánh xạ RecyclerView
        btnEditCategories = findViewById(R.id.btn_edit_categories); // Ánh xạ nút Edit mới

        // Cài đặt các thành phần UI
        setupDatePicker();
        setupCategoryGrid(); // Cài đặt RecyclerView thay vì Spinner
        setupTransactionTypeListener(); // Giữ nguyên listener đổi màu và lọc

        // Cập nhật Placeholder
        inputAmount.setHint("Enter the amount");
        inputNote.setHint("Enter notes");
        inputDate.setHint("Select date");

        // Lấy Transaction ID từ Intent
        transactionId = getIntent().getLongExtra("TRANSACTION_ID", -1);

        if (transactionId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observe danh sách Category trước
        observeCategories();

        // Gán sự kiện cho nút Edit Categories
        btnEditCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, CostalActivity.class));
            // Không finish()
        });

        // Gán sự kiện click cho nút Update
        // Chữ trên nút sẽ được cập nhật trong setupTransactionTypeListener
        btnSave.setOnClickListener(v -> updateTransaction());
    }

    /**
     * Cài đặt CategoryGridAdapter cho RecyclerView.
     */
    private void setupCategoryGrid() {
        categoryGridAdapter = new CategoryGridAdapter();

        // Lắng nghe khi người dùng chọn category trên lưới
        categoryGridAdapter.setOnCategorySelectedListener(categoryId -> {
            selectedCategoryIdFromGrid = categoryId;
            Log.d("EditTransactionActivity", "Category selected: " + categoryId); // Log để kiểm tra
        });

        // Thiết lập GridLayoutManager với 3 cột (hoặc số khác tùy ý)
        rvCategoryGrid.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategoryGrid.setAdapter(categoryGridAdapter);
        rvCategoryGrid.setNestedScrollingEnabled(false); // Có thể cần nếu layout phức tạp
    }


    /**
     * Lắng nghe LiveData danh sách Category từ ViewModel.
     * Khi có dữ liệu, lưu lại và gọi loadTransactionData.
     */
    private void observeCategories() {
        // Chỉ observe một lần duy nhất
        if (categoryViewModel.getAllCategories().hasObservers()) return;

        categoryViewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                Log.d("EditTransactionActivity", "Categories observed: " + categories.size());
                allCategories = categories;
                // Quan trọng: Chỉ gọi loadTransactionData SAU KHI đã có danh sách categories
                // Đồng thời phải đảm bảo chỉ load 1 lần duy nhất
                if (!isDataLoaded) {
                    loadTransactionData();
                }
            } else {
                Log.w("EditTransactionActivity", "Observed categories list is null");
            }
        });
    }

    /**
     * Lắng nghe LiveData của Transaction cần sửa từ ViewModel.
     * Chỉ gọi populateUi một lần khi dữ liệu hợp lệ được trả về.
     */
    private void loadTransactionData() {
        // Chỉ observe một lần duy nhất
        if (transactionViewModel.getTransactionById(transactionId).hasObservers()) return;

        transactionViewModel.getTransactionById(transactionId).observe(this, transaction -> {
            // Chỉ đổ dữ liệu lên UI lần đầu tiên và khi transaction khác null
            if (transaction != null && !isDataLoaded) {
                Log.d("EditTransactionActivity", "Transaction data loaded: ID " + transaction.getId());
                currentTransaction = transaction;
                populateUi(transaction); // Đổ dữ liệu lên các trường input
                isDataLoaded = true; // Đánh dấu đã load xong
            } else if (transaction == null && !isDataLoaded) {
                // Xử lý trường hợp không tìm thấy transaction ngay lần đầu
                Log.e("EditTransactionActivity", "Transaction với ID " + transactionId + " không tồn tại.");
                Toast.makeText(this, "Lỗi: Không tải được chi tiết giao dịch", Toast.LENGTH_SHORT).show();
                finish(); // Đóng nếu không tìm thấy
            }
            // Bỏ observe sau khi đã load thành công hoặc xác nhận không tìm thấy
            if (isDataLoaded || transaction == null) {
                transactionViewModel.getTransactionById(transactionId).removeObservers(this);
            }
        });
    }

    /**
     * Đổ dữ liệu từ Transaction đã tải lên các trường EditText và RecyclerView.
     */
    private void populateUi(Transaction transaction) {
        if (transaction == null) {
            Log.e("EditTransactionActivity", "populateUi called with null transaction");
            return; // Kiểm tra an toàn
        }
        Log.d("EditTransactionActivity", "Populating UI for transaction ID: " + transaction.getId());

        // Đổ dữ liệu vào EditTexts
        inputAmount.setText(String.valueOf(transaction.getAmount()));
        inputNote.setText(transaction.getDescription());

        // Cập nhật Calendar và hiển thị ngày tháng
        myCalendar.setTimeInMillis(transaction.getTransactionDate());
        updateLabel(); // Cập nhật EditText ngày

        // --- QUAN TRỌNG: Set RadioGroup TRƯỚC khi gọi filter ---
        // Set transaction type in RadioGroup (sẽ trigger listener và gọi filter)
        if ("income".equalsIgnoreCase(transaction.getType())) {
            rgTransactionType.check(R.id.rb_income);
        } else {
            rgTransactionType.check(R.id.rb_expense);
        }
        // Listener của RadioGroup SẼ TỰ ĐỘNG gọi filterCategoriesByType()


        // --- Highlight Category cũ ---
        // Đoạn code này cần chạy SAU KHI filterCategoriesByType() đã chạy (do listener trigger)
        // Dùng post để đảm bảo RecyclerView đã cập nhật xong danh sách
        rvCategoryGrid.post(() -> {
            long oldCategoryId = transaction.getCategoryId();
            selectedCategoryIdFromGrid = oldCategoryId; // Cập nhật biến lưu ID
            categoryGridAdapter.setSelectedCategoryId(oldCategoryId); // Yêu cầu Adapter highlight
            Log.d("EditTransactionActivity", "Attempting to highlight category ID: " + oldCategoryId);
            // Kiểm tra xem ID có tồn tại trong list không
            boolean found = false;
            for(Category cat : categoryList) {
                if(cat.getId() == oldCategoryId) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                Log.w("EditTransactionActivity", "Old category ID " + oldCategoryId + " not found in the filtered list.");
                // Có thể hiển thị Toast hoặc chọn mặc định item đầu tiên nếu muốn
                // selectedCategoryIdFromGrid = -1; // Reset nếu không tìm thấy
                // categoryGridAdapter.setSelectedCategoryId(-1);
            }
        });
    }

    /**
     * Lọc danh sách category dựa trên loại giao dịch đang được chọn (Income/Expense)
     * và cập nhật RecyclerView.
     */
    private void filterCategoriesByType() {
        String selectedType = getSelectedTransactionType();
        categoryList.clear(); // Xóa list tạm thời

        Log.d("EditTransactionActivity", "Filtering categories for type: " + selectedType + ". Total categories: " + allCategories.size());

        for (Category cat : allCategories) {
            if (selectedType.equalsIgnoreCase(cat.getType())) {
                categoryList.add(cat);
            }
        }
        Log.d("EditTransactionActivity", "Filtered list size: " + categoryList.size());

        // Submit danh sách đã lọc cho Adapter của RecyclerView
        // Tạo bản sao để tránh ListAdapter sửa đổi list gốc
        categoryGridAdapter.submitList(new ArrayList<>(categoryList));

        // Reset lựa chọn category trong Adapter (UI highlight) khi đổi type
        // Biến selectedCategoryIdFromGrid sẽ được cập nhật lại khi người dùng bấm chọn
        // Hoặc trong populateUi khi load dữ liệu cũ
        categoryGridAdapter.setSelectedCategoryId(-1);
        selectedCategoryIdFromGrid = -1; // Cũng reset biến lưu trữ của Activity
    }

    /**
     * Xử lý logic khi bấm nút "Update".
     */
    private void updateTransaction() {
        if (currentTransaction == null) {
            Toast.makeText(this, "Lỗi: Dữ liệu giao dịch gốc chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = inputAmount.getText().toString().trim();
        String note = inputNote.getText().toString().trim();

        // --- VALIDATION ---
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập Số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem category đã được chọn từ lưới chưa
        if (selectedCategoryIdFromGrid == -1) {
            Toast.makeText(this, "Vui lòng chọn một danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- TRY UPDATING ---
        try {
            double amount = Double.parseDouble(amountStr);
            long timestamp = myCalendar.getTimeInMillis();
            long categoryIdToSave = selectedCategoryIdFromGrid; // Lấy ID từ biến đã lưu
            String transactionType = getSelectedTransactionType();

            // Cập nhật đối tượng currentTransaction
            currentTransaction.setAmount(amount);
            currentTransaction.setDescription(note);
            currentTransaction.setTransactionDate(timestamp);
            currentTransaction.setCategoryId(categoryIdToSave); // Cập nhật Category ID mới
            currentTransaction.setType(transactionType); // Cập nhật Type

            // Gọi ViewModel để cập nhật
            transactionViewModel.update(currentTransaction);

            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }


    // --- Listener cho RadioGroup (Đã bao gồm đổi màu) ---
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
                btnSave.setText("Update Income"); // Cập nhật text nút
            } else { // checkedId == R.id.rb_expense
                // Khi chọn Expense
                rbExpense.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                rbExpense.setTextColor(whiteColor);
                rbIncome.setBackgroundTintList(ColorStateList.valueOf(unselectedColorBg));
                rbIncome.setTextColor(selectedColor);
                btnSave.setText("Update expense"); // Cập nhật text nút
            }

            // Trigger lọc danh mục KHI CÓ DỮ LIỆU categories
            if (!allCategories.isEmpty()) {
                filterCategoriesByType();
            } else {
                Log.w("EditTransactionActivity","Cannot filter yet, allCategories is empty.");
                // Nếu chưa có allCategories, observeCategories sẽ tự gọi filter sau
            }
        });
    }

    // --- Hàm lấy loại giao dịch đã chọn ---
    private String getSelectedTransactionType() {
        int checkedId = rgTransactionType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_income) {
            return "income";
        } else {
            return "expense";
        }
    }


    // --- Các hàm Helper cho DatePicker (Giữ nguyên) ---
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
        inputDate.setFocusableInTouchMode(false);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }
}