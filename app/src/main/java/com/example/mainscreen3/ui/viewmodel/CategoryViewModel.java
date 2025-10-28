package com.example.mainscreen3.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainscreen3.data.local.model.CategoryModel;
import com.example.mainscreen3.data.local.model.ExpenseItem;
import com.example.mainscreen3.data.local.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;

    private final MutableLiveData<List<CategoryModel>> _filteredCategories = new MutableLiveData<>();
    public LiveData<List<CategoryModel>> filteredCategories = _filteredCategories;

    // LiveData để giữ trạng thái loại category đang chọn (EXPENSE/REVENUE)
    private final MutableLiveData<String> _currentCategoryType = new MutableLiveData<>(ExpenseItem.TYPE_EXPENSE); // Mặc định là Expense
    public LiveData<String> currentCategoryType = _currentCategoryType;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        // Khởi tạo Repository
        categoryRepository = CategoryRepository.getInstance(application);
        // Tải danh sách ban đầu theo type mặc định
        loadCategoriesByType(_currentCategoryType.getValue());
    }

    public void loadCategoriesByType(String type) {
        // Cập nhật trạng thái type hiện tại
        _currentCategoryType.setValue(type);
        // Lấy danh sách từ Repository
        List<CategoryModel> categories = categoryRepository.getCategoriesByType(type);
        // Cập nhật LiveData
        _filteredCategories.setValue(categories);
    }

    public void saveCategory(CategoryModel newOrUpdatedCategory, CategoryModel oldCategory) {
        categoryRepository.saveCategory(newOrUpdatedCategory, oldCategory);
        // Sau khi lưu, tải lại danh sách cho type hiện tại để cập nhật UI
        loadCategoriesByType(_currentCategoryType.getValue());
    }

    public void deleteCategory(CategoryModel categoryToDelete) {
        categoryRepository.deleteCategory(categoryToDelete);
        loadCategoriesByType(_currentCategoryType.getValue());
    }
}
