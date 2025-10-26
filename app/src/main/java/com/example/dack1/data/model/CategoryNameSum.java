package com.example.dack1.data.model;

/**
 * POJO giữ tổng chi tiêu theo tên danh mục.
 */
public class CategoryNameSum {

    public String categoryName;
    public double total;

    public CategoryNameSum() {}

    public CategoryNameSum(String categoryName, double total) {
        this.categoryName = categoryName;
        this.total = total;
    }
}


