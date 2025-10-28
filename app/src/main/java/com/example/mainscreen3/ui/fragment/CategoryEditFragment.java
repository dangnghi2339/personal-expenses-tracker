package com.example.mainscreen3.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainscreen3.ui.adapter.CategoryEditAdapter;
import com.example.mainscreen3.data.local.prefs.CategoryStorageHelper;
import com.example.mainscreen3.ui.viewmodel.CategoryViewModel;
import com.example.mainscreen3.util.OnCategoryAddedListener;
import com.example.mainscreen3.R;
import com.example.mainscreen3.data.local.model.CategoryModel;
import com.example.mainscreen3.data.local.model.ExpenseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryEditFragment extends DialogFragment implements CategoryEditAdapter.OnCategoryItemClickListener {

    public static final String TAG = "CategoryEditFragment";

    private RecyclerView rvCategories;
    private CategoryEditAdapter adapter;
    private Button btnExpenditure, btnRevenue;
    private ImageButton btnBack;
    private String currentCategoryType = ExpenseItem.TYPE_EXPENSE;
    private CategoryViewModel categoryViewModel;

    public CategoryEditFragment() {
        // Constructor rỗng bắt buộc
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
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
        adapter = new CategoryEditAdapter(new ArrayList<>());
        adapter.setOnCategoryItemClickListener(this);
        rvCategories.setAdapter(adapter);

        // --- Xử lý sự kiện Tabs ---
        btnExpenditure.setOnClickListener(v -> categoryViewModel.loadCategoriesByType(ExpenseItem.TYPE_EXPENSE));
        btnRevenue.setOnClickListener(v -> categoryViewModel.loadCategoriesByType(ExpenseItem.TYPE_REVENUE));


        // Xử lý sự kiện Back
        btnBack.setOnClickListener(v -> dismiss());
        btnAddCategory.setOnClickListener(v -> openAddCategoryScreen(null));

        setupObservers();
    }

    private void setupObservers() {
        categoryViewModel.filteredCategories.observe(getViewLifecycleOwner(), categories -> {
            adapter.updateList(categories);
        });

        categoryViewModel.currentCategoryType.observe(getViewLifecycleOwner(), type -> {
            updateTabUI(type);
        });
    }

    private void updateTabUI(String type) {
        if (getContext() == null) return; // Kiểm tra context
        if (ExpenseItem.TYPE_EXPENSE.equals(type)) {
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getContext().getTheme()));
            btnExpenditure.setTextColor(getResources().getColor(android.R.color.white, getContext().getTheme()));
            btnRevenue.setBackgroundTintList(getResources().getColorStateList(android.R.color.white, getContext().getTheme()));
            btnRevenue.setTextColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
        } else {
            btnRevenue.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getContext().getTheme()));
            btnRevenue.setTextColor(getResources().getColor(android.R.color.white, getContext().getTheme()));
            btnExpenditure.setBackgroundTintList(getResources().getColorStateList(android.R.color.white, getContext().getTheme()));
            btnExpenditure.setTextColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
        }
    }

    private void openAddCategoryScreen(CategoryModel categoryToEdit) {
        AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
        Bundle args = new Bundle();

        if (categoryToEdit != null) {
            args.putSerializable("EDIT_CATEGORY_MODEL", categoryToEdit);
        } else {
            args.putString("CATEGORY_TYPE", categoryViewModel.currentCategoryType.getValue());
        }
        addCategoryFragment.setArguments(args);

        if (getChildFragmentManager() != null && !getChildFragmentManager().isStateSaved()) {
            try {

                addCategoryFragment.show(getChildFragmentManager(), AddCategoryFragment.TAG);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClicked(CategoryModel category) {
        openAddCategoryScreen(category);
    }
}