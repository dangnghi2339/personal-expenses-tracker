package com.example.mainscreen3.util;

import com.example.mainscreen3.data.local.model.CategoryModel;

public interface OnCategoryAddedListener {
    void onCategorySaved(CategoryModel newOrUpdatedCategory, CategoryModel oldCategory);
    void onCategoryDeleted(CategoryModel categoryToDelete);
}
