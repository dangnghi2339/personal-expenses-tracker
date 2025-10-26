package com.example.mainscreen3;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseEntryFragment extends DialogFragment implements CategoryEditFragment.OnCategoryRelayListener{

    private TextView tvSelectedDate;
    private EditText etAmount, etNote;
    private String selectedCategory;
    private int selectedIconResId = 0;
    private int selectedColorResId = 0;
    private Calendar currentCalendar;
    private View currentlySelectedCategoryView;

    private Button btnExpenditure, btnRevenue;
    private Button btnEditCategories;
    private View layoutExpenseCategories, layoutRevenueCategories;
    private String currentTransactionType = ExpenseItem.TYPE_EXPENSE;
    private List<LinearLayout> expenseCategoryViews;
    private List<LinearLayout> revenueCategoryViews;
    private Button btnSave;

    public interface OnExpenseSavedListener {
        void onExpenseSaved(ExpenseItem item);
    }

    private OnExpenseSavedListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        currentCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        etAmount = view.findViewById(R.id.et_amount);
        etNote = view.findViewById(R.id.et_note);

        btnSave = view.findViewById(R.id.btn_save_expenses);

        ImageButton btnPrevDay = view.findViewById(R.id.btn_prev_day);
        ImageButton btnNextDay = view.findViewById(R.id.btn_next_day);
        btnExpenditure = view.findViewById(R.id.btn_expenditure);
        btnRevenue = view.findViewById(R.id.btn_revenue);
        btnEditCategories = view.findViewById(R.id.btn_edit_categories);

        layoutExpenseCategories = view.findViewById(R.id.layout_expense_categories);
        layoutRevenueCategories = view.findViewById(R.id.layout_revenue_categories);

        expenseCategoryViews = findCategoryViews(layoutExpenseCategories);
        revenueCategoryViews = findCategoryViews(layoutRevenueCategories);

        loadCustomCategories();

        setupCategoryListeners(expenseCategoryViews);
        setupCategoryListeners(revenueCategoryViews);

        switchTransactionType(currentTransactionType);

        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        btnPrevDay.setOnClickListener(v -> { /* ... */ });
        btnNextDay.setOnClickListener(v -> { /* ... */ });

        btnExpenditure.setOnClickListener(v -> switchTransactionType(ExpenseItem.TYPE_EXPENSE));
        btnRevenue.setOnClickListener(v -> switchTransactionType(ExpenseItem.TYPE_REVENUE));

        btnSave.setOnClickListener(v -> saveExpense());
        btnEditCategories.setOnClickListener(v -> openCategoryEditScreen());
    }

    private void loadCustomCategories() {
        if (getContext() == null) return;

        // Tải danh sách đã lưu
        List<CategoryModel> customCategories = CategoryStorageHelper.loadCategories(getContext());

        for (CategoryModel model : customCategories) {
            // Tạo View mới (dùng hàm bạn đã có)
            LinearLayout newCategoryView = createNewCategoryView(model);

            // Thêm View vào Layout tương ứng
            if (model.getType().equals(ExpenseItem.TYPE_EXPENSE)) {
                if (layoutExpenseCategories instanceof ViewGroup) {
                    ((ViewGroup) layoutExpenseCategories).addView(newCategoryView);
                    expenseCategoryViews.add(newCategoryView); // Quan trọng: Thêm vào danh sách theo dõi
                }
            } else { // REVENUE
                if (layoutRevenueCategories instanceof ViewGroup) {
                    ((ViewGroup) layoutRevenueCategories).addView(newCategoryView);
                    revenueCategoryViews.add(newCategoryView); // Quan trọng: Thêm vào danh sách theo dõi
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExpenseSavedListener) {
            listener = (OnExpenseSavedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnExpenseSavedListener");
        }
    }

    @Override
    public void onNewCategoryCreated(CategoryModel newCategory) {
        onNewCategoryAdded(newCategory);
    }

    @Override
    public void onCategoryDeleted(CategoryModel deletedCategory) {
        if (!isAdded()) return;

        View viewToDelete = null;

        // Tìm View cần xóa trong danh sách Chi
        for (LinearLayout view : expenseCategoryViews) {
            Object tag = view.getTag();
            if (tag instanceof CategoryModel && ((CategoryModel)tag).equals(deletedCategory)) {
                viewToDelete = view;
                break;
            }
        }

        // Nếu không thấy, tìm trong danh sách Thu
        if (viewToDelete == null) {
            for (LinearLayout view : revenueCategoryViews) {
                Object tag = view.getTag();
                if (tag instanceof CategoryModel && ((CategoryModel)tag).equals(deletedCategory)) {
                    viewToDelete = view;
                    break;
                }
            }
        }

        // Nếu tìm thấy View, hãy xóa nó
        if (viewToDelete != null) {
            // Xóa khỏi layout
            ((ViewGroup) viewToDelete.getParent()).removeView(viewToDelete);

            // Xóa khỏi danh sách theo dõi
            expenseCategoryViews.remove(viewToDelete);
            revenueCategoryViews.remove(viewToDelete);

            // Nếu nó đang được chọn, reset về cái đầu tiên
            if (currentlySelectedCategoryView == viewToDelete) {
                switchTransactionType(currentTransactionType);
            }
        }
    }

    @Override
    public void onCategoryEdited(CategoryModel updatedCategory, CategoryModel oldCategory) {
        // Cách đơn giản nhất: Xóa cái cũ đi và Thêm cái mới vào

        // 1. Xóa View cũ
        onCategoryDeleted(oldCategory);

        // 2. Thêm View mới
        onNewCategoryAdded(updatedCategory);
    }

    private void openCategoryEditScreen() {
        if (getParentFragmentManager() == null || getParentFragmentManager().isStateSaved()) {
            return;
        }

        CategoryEditFragment categoryEditFragment = new CategoryEditFragment();

        categoryEditFragment.setOnCategoryRelayListener(this);

        try {
            categoryEditFragment.show(getParentFragmentManager(), CategoryEditFragment.TAG);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    currentCalendar.set(year, monthOfYear, dayOfMonth);
                    updateDateDisplay();
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tvSelectedDate.setText(sdf.format(currentCalendar.getTime()));
    }

    private void switchTransactionType(String type) {
        currentTransactionType = type;

        if (ExpenseItem.TYPE_EXPENSE.equals(type)) {
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            btnExpenditure.setTextColor(getResources().getColor(android.R.color.white));
            btnRevenue.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
            btnRevenue.setTextColor(getResources().getColor(R.color.colorPrimary));

            layoutExpenseCategories.setVisibility(View.VISIBLE);
            layoutRevenueCategories.setVisibility(View.GONE);
            btnSave.setText("Save expenses");

            if (!expenseCategoryViews.isEmpty()) {
                View firstCategory = expenseCategoryViews.get(0);
                Object tag = firstCategory.getTag();

                if (tag instanceof CategoryModel) {
                    CategoryModel model = (CategoryModel) tag;
                    handleCategorySelection(firstCategory, model.getName(), model.getIconResource(), model.getColorResource());
                } else if (tag instanceof String) {
                    String name = (String) tag;
                    handleCategorySelection(firstCategory, name, 0, 0);
                }
            }
        } else {

            btnRevenue.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            btnRevenue.setTextColor(getResources().getColor(android.R.color.white));
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
            btnExpenditure.setTextColor(getResources().getColor(R.color.colorPrimary));

            layoutExpenseCategories.setVisibility(View.GONE);
            layoutRevenueCategories.setVisibility(View.VISIBLE);
            btnSave.setText("Save the revenue");

            if (!revenueCategoryViews.isEmpty()) {
                View firstCategory = revenueCategoryViews.get(0);
                Object tag = firstCategory.getTag();

                if (tag instanceof CategoryModel) {
                    CategoryModel model = (CategoryModel) tag;
                    handleCategorySelection(firstCategory, model.getName(), model.getIconResource(), model.getColorResource());
                } else if (tag instanceof String) {
                    String name = (String) tag;
                    handleCategorySelection(firstCategory, name, 0, 0);
                }
            }
        }
    }

    private void handleCategorySelection(View newSelectionView, String categoryName, int iconResId, int colorResId) {
        unselectAllCategoryViews(expenseCategoryViews);
        unselectAllCategoryViews(revenueCategoryViews);

        newSelectionView.setSelected(true);
        currentlySelectedCategoryView = newSelectionView;
        selectedCategory = categoryName;
        selectedIconResId = iconResId;
        selectedColorResId = colorResId;
    }

    private void unselectAllCategoryViews(List<LinearLayout> categoryViews) {
        for (LinearLayout view : categoryViews) {
            view.setSelected(false);
        }
    }

    private List<LinearLayout> findCategoryViews(View parentLayout) {
        List<LinearLayout> categoryViews = new ArrayList<>();

        if (parentLayout == null) {
            return categoryViews;
        }

        if (parentLayout instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) parentLayout;

            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View child = gridLayout.getChildAt(i);
                if (child instanceof LinearLayout) {
                    categoryViews.add((LinearLayout) child);
                }
            }
            return categoryViews;

        } else if (parentLayout instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parentLayout;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof GridLayout) {
                    return findCategoryViews(child);
                }
            }
        }
        return categoryViews;
    }
    private void setupCategoryListeners(List<LinearLayout> categoryViews) {
        for (LinearLayout categoryView : categoryViews) {
            categoryView.setOnClickListener(v -> {

                Object tag = v.getTag();

                if (tag instanceof CategoryModel) {
                    CategoryModel model = (CategoryModel) tag;
                    handleCategorySelection(v, model.getName(), model.getIconResource(), model.getColorResource());

                } else if (tag instanceof String) {
                    String categoryName = (String) tag;
                    handleCategorySelection(v, categoryName, 0, 0);
                }
            });
        }
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString();
        String note = etNote.getText().toString();
        String date = tvSelectedDate.getText().toString();

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount required");
            return;
        }

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn một Category", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            ExpenseItem newItem = new ExpenseItem(
                    selectedCategory,
                    amount,
                    note,
                    date,
                    currentTransactionType,
                    selectedIconResId,
                    selectedColorResId
            );

            if (listener != null) {
                listener.onExpenseSaved(newItem);
            }

            dismiss();
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount format");
        }
    }

    public void onNewCategoryAdded(CategoryModel newCategory) {
        if (isAdded()) {
            LinearLayout newCategoryView = createNewCategoryView(newCategory);

            if (newCategory.getType().equals(ExpenseItem.TYPE_EXPENSE)) {
                if (layoutExpenseCategories instanceof ViewGroup) {
                    ((ViewGroup) layoutExpenseCategories).addView(newCategoryView);
                    expenseCategoryViews.add(newCategoryView);
                }
            } else {
                if (layoutRevenueCategories instanceof ViewGroup) {
                    ((ViewGroup) layoutRevenueCategories).addView(newCategoryView);
                    revenueCategoryViews.add(newCategoryView);
                }
            }

            newCategoryView.setOnClickListener(v -> {
                CategoryModel model = (CategoryModel) v.getTag();
                handleCategorySelection(v, model.getName(), model.getIconResource(), model.getColorResource());
            });

            handleCategorySelection(newCategoryView, newCategory.getName(), newCategory.getIconResource(), newCategory.getColorResource());
        }
    }

    private LinearLayout createNewCategoryView(CategoryModel model) {
        Context context = requireContext();
        LinearLayout layout = new LinearLayout(context);

        layout.setTag(model);

        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridParams.width = 0;
        gridParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        gridParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        int marginDp = (int) (getResources().getDisplayMetrics().density * 8);
        gridParams.setMargins(marginDp, marginDp, marginDp, marginDp);
        layout.setLayoutParams(gridParams);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundResource(R.drawable.category_button_bg);

        ImageView icon = new ImageView(context);
        icon.setImageResource(model.getIconResource());

        icon.setColorFilter(
                ContextCompat.getColor(context, model.getColorResource()),
                android.graphics.PorterDuff.Mode.SRC_ATOP
        );

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (getResources().getDisplayMetrics().density * 48), // 48dp
                (int) (getResources().getDisplayMetrics().density * 48)  // 48dp
        );
        icon.setLayoutParams(iconParams);

        TextView name = new TextView(context);
        name.setText(model.getName());
        name.setSingleLine(true);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        name.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.topMargin = (int) (getResources().getDisplayMetrics().density * 4); // 4dp margin
        name.setLayoutParams(nameParams);

        layout.addView(icon);
        layout.addView(name);

        return layout;
    }
}