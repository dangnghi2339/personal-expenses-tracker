package com.example.mainscreen3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryEditFragment extends DialogFragment implements OnCategoryAddedListener, CategoryEditAdapter.OnCategoryItemClickListener {

    public static final String TAG = "CategoryEditFragment";

    private RecyclerView rvCategories;
    private CategoryEditAdapter adapter;
    private Button btnExpenditure, btnRevenue;
    private ImageButton btnBack;
    private String currentCategoryType = ExpenseItem.TYPE_EXPENSE;

    // Dữ liệu mẫu
    private List<CategoryModel> allCategories;

    // Khởi tạo dữ liệu mẫu
    private void initializeMockData() {

        List<CategoryModel> customCategories = CategoryStorageHelper.loadCategories(requireContext());
        List<CategoryModel> defaultCategories = new ArrayList<>();

        final int DEFAULT_EXPENSE_COLOR = R.color.colorPrimary;
        final int DEFAULT_REVENUE_COLOR = R.color.colorPrimary;

        // --- EXPENSE MOCK DATA ---
        defaultCategories.add(new CategoryModel("Market", R.drawable.ic_market, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR));
        defaultCategories.add(new CategoryModel("Eat and drink", R.drawable.ic_eat_drink, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR));
        defaultCategories.add(new CategoryModel("Shopping", R.drawable.ic_shopping, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR));
        defaultCategories.add(new CategoryModel("Gasoline", R.drawable.ic_gasoline, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR));
        defaultCategories.add(new CategoryModel("House", R.drawable.ic_house, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR));

        // --- REVENUE MOCK DATA ---
        defaultCategories.add(new CategoryModel("Salary", R.drawable.ic_salary, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR));
        defaultCategories.add(new CategoryModel("Bonus", R.drawable.ic_bonus, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR));
        defaultCategories.add(new CategoryModel("Invest", R.drawable.ic_invest, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR));

        allCategories = new ArrayList<>();
        allCategories.addAll(defaultCategories);
        allCategories.addAll(customCategories);
    }

    public CategoryEditFragment() {
        // Constructor rỗng bắt buộc
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        initializeMockData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back);
        btnExpenditure = view.findViewById(R.id.btn_expenditure_edit);
        btnRevenue = view.findViewById(R.id.btn_revenue_edit);
        rvCategories = view.findViewById(R.id.rv_categories);
        View btnAddCategory = view.findViewById(R.id.btn_add_category);

        // --- Thiết lập RecyclerView ---
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách ban đầu (Expense)
        List<CategoryModel> initialList = filterCategoriesByType(currentCategoryType);
        adapter = new CategoryEditAdapter(initialList);
        adapter.setOnCategoryItemClickListener(this);
        rvCategories.setAdapter(adapter);

        // --- Xử lý sự kiện Tabs ---
        btnExpenditure.setOnClickListener(v -> switchCategoryType(ExpenseItem.TYPE_EXPENSE));
        btnRevenue.setOnClickListener(v -> switchCategoryType(ExpenseItem.TYPE_REVENUE));

        // Thiết lập trạng thái ban đầu
        switchCategoryType(currentCategoryType);

        // Xử lý sự kiện Back
        btnBack.setOnClickListener(v -> dismiss());
        btnAddCategory.setOnClickListener(v -> openAddCategoryScreen(null));
    }

    private void openAddCategoryScreen(CategoryModel categoryToEdit) {
        AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
        Bundle args = new Bundle();

        if (categoryToEdit != null) {
            // CHẾ ĐỘ SỬA
            args.putSerializable("EDIT_CATEGORY_MODEL", categoryToEdit);
        } else {
            // CHẾ ĐỘ THÊM MỚI
            args.putString("CATEGORY_TYPE", currentCategoryType);
        }

        addCategoryFragment.setArguments(args);
        addCategoryFragment.setOnCategoryAddedListener(this); // Gán listener

        // === BỔ SUNG PHẦN BỊ THIẾU ===
        if (getParentFragmentManager() != null && !getParentFragmentManager().isStateSaved()) {
            try {
                // Đây chính là lệnh để hiển thị fragment
                addCategoryFragment.show(getParentFragmentManager(), AddCategoryFragment.TAG);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClicked(CategoryModel category) {
        // Mở màn hình Sửa/Fix
        openAddCategoryScreen(category);
    }

    // 4. IMPLEMENT HÀM LƯU (từ AddCategoryFragment)
    @Override
    public void onCategorySaved(CategoryModel newOrUpdatedCategory, CategoryModel oldCategory) {
        List<CategoryModel> customCategories = CategoryStorageHelper.loadCategories(requireContext());

        if (oldCategory != null) {
            // --- CHẾ ĐỘ SỬA ---
            // 1. Thay thế trong SharedPreferences
            int index = customCategories.indexOf(oldCategory);
            if (index != -1) {
                customCategories.set(index, newOrUpdatedCategory);
            }

            // 2. Thay thế trong danh sách runtime
            int runtimeIndex = allCategories.indexOf(oldCategory);
            if (runtimeIndex != -1) {
                allCategories.set(runtimeIndex, newOrUpdatedCategory);
            }
        } else {
            // --- CHẾ ĐỘ THÊM MỚI ---
            customCategories.add(newOrUpdatedCategory);
            allCategories.add(newOrUpdatedCategory);
        }

        // 5. Lưu lại SharedPreferences
        CategoryStorageHelper.saveCategories(requireContext(), customCategories);

        // 6. Cập nhật RecyclerView
        switchCategoryType(currentCategoryType);

        // 7. Báo cho ExpenseEntryFragment
        if (relayListener != null) {
            if (oldCategory != null) {
                relayListener.onCategoryEdited(newOrUpdatedCategory, oldCategory);
            } else {
                relayListener.onNewCategoryCreated(newOrUpdatedCategory);
            }
        }
    }

    // 5. IMPLEMENT HÀM XÓA (từ AddCategoryFragment)
    @Override
    public void onCategoryDeleted(CategoryModel categoryToDelete) {
        // 1. Xóa khỏi danh sách runtime
        allCategories.remove(categoryToDelete);

        // 2. Xóa khỏi SharedPreferences
        List<CategoryModel> customCategories = CategoryStorageHelper.loadCategories(requireContext());
        customCategories.remove(categoryToDelete);
        CategoryStorageHelper.saveCategories(requireContext(), customCategories);

        // 3. Cập nhật lại RecyclerView
        switchCategoryType(currentCategoryType);

        // 4. Báo cho ExpenseEntryFragment
        if (relayListener != null) {
            relayListener.onCategoryDeleted(categoryToDelete);
        }
    }

    private List<CategoryModel> filterCategoriesByType(String type) {
        return allCategories.stream()
                .filter(c -> c.getType().equals(type))
                .collect(Collectors.toList());
    }

    private void switchCategoryType(String type) {
        currentCategoryType = type;

        // 1. Cập nhật giao diện Tabs
        if (ExpenseItem.TYPE_EXPENSE.equals(type)) {
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            btnExpenditure.setTextColor(getResources().getColor(android.R.color.white));
            btnRevenue.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
            btnRevenue.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btnRevenue.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            btnRevenue.setTextColor(getResources().getColor(android.R.color.white));
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
            btnExpenditure.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        // 2. Cập nhật dữ liệu RecyclerView
        List<CategoryModel> filteredList = filterCategoriesByType(type);
        adapter.updateList(filteredList);
    }

    // 3. Hàm gửi dữ liệu về ExpenseEntryFragment (cha)
    private void notifyParentFragment(CategoryModel newCategory) {
        if (relayListener != null) {
            relayListener.onNewCategoryCreated(newCategory);
        }
    }
    public interface OnCategoryRelayListener {
        void onNewCategoryCreated(CategoryModel newCategory);
        void onCategoryDeleted(CategoryModel deletedCategory);
        void onCategoryEdited(CategoryModel updatedCategory, CategoryModel oldCategory);
    }

    private OnCategoryRelayListener relayListener;

    public void setOnCategoryRelayListener(OnCategoryRelayListener listener) {
        this.relayListener = listener;
    }
}