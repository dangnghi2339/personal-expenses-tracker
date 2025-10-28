package com.example.mainscreen3.data.local.repository;

import android.content.Context;

import com.example.mainscreen3.data.local.prefs.CategoryStorageHelper;
import com.example.mainscreen3.R;
import com.example.mainscreen3.data.local.model.CategoryModel;
import com.example.mainscreen3.data.local.model.ExpenseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryRepository {
    private static volatile CategoryRepository INSTANCE;
    private final CategoryStorageHelper storageHelper;
    private final Context appContext;

    // Constructor này có thể bị thay đổi khi merge 'main'
    public CategoryRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.storageHelper = new CategoryStorageHelper(); // Khởi tạo helper
    }

    public static CategoryRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CategoryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CategoryRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> customCategories = storageHelper.loadCategories(appContext); //
        List<CategoryModel> allCategories = new ArrayList<>(getDefaultCategories());
        allCategories.addAll(customCategories);
        return allCategories;
    }

    public List<CategoryModel> getCategoriesByType(String type) {
        return getAllCategories().stream()
                .filter(c -> c.getType().equals(type))
                .collect(Collectors.toList());
    }

    public void saveCategory(CategoryModel newOrUpdatedCategory, CategoryModel oldCategory) {
        List<CategoryModel> customCategories = storageHelper.loadCategories(appContext);
        if (oldCategory != null) {
            int index = customCategories.indexOf(oldCategory);
            if (index != -1) customCategories.set(index, newOrUpdatedCategory);
        } else {
            customCategories.add(newOrUpdatedCategory);
        }
        storageHelper.saveCategories(appContext, customCategories); //
    }

    public void deleteCategory(CategoryModel categoryToDelete) {
        List<CategoryModel> customCategories = storageHelper.loadCategories(appContext);
        customCategories.remove(categoryToDelete);
        storageHelper.saveCategories(appContext, customCategories);
    }

    private List<CategoryModel> getDefaultCategories() {
        List<CategoryModel> defaultCategories = new ArrayList<>();
        final int DEFAULT_EXPENSE_COLOR = R.color.colorPrimary;
        final int DEFAULT_REVENUE_COLOR = R.color.colorPrimary;

        // Dùng constructor 5 tham số để đánh dấu 'isCustom = false'
        //
        defaultCategories.add(new CategoryModel("Market", R.drawable.ic_market, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR, false));
        defaultCategories.add(new CategoryModel("Eat and drink", R.drawable.ic_eat_drink, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR, false));
        defaultCategories.add(new CategoryModel("Shopping", R.drawable.ic_shopping, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR, false));
        defaultCategories.add(new CategoryModel("Gasoline", R.drawable.ic_gasoline, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR, false));
        defaultCategories.add(new CategoryModel("House", R.drawable.ic_house, ExpenseItem.TYPE_EXPENSE, DEFAULT_EXPENSE_COLOR, false));
        defaultCategories.add(new CategoryModel("Salary", R.drawable.ic_salary, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR, false));
        defaultCategories.add(new CategoryModel("Bonus", R.drawable.ic_bonus, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR, false));
        defaultCategories.add(new CategoryModel("Invest", R.drawable.ic_invest, ExpenseItem.TYPE_REVENUE, DEFAULT_REVENUE_COLOR, false));

        return defaultCategories;
    }
}
