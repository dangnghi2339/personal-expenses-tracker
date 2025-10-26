package com.example.mainscreen3;

public interface OnCategoryAddedListener {
    void onCategorySaved(CategoryModel newOrUpdatedCategory, CategoryModel oldCategory);
    void onCategoryDeleted(CategoryModel categoryToDelete);
}
