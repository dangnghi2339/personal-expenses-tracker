package com.example.dack1.data.model;

/**
 * POJO (Plain Old Java Object) để giữ kết quả từ câu query
 * getCategorySumsByDateRange.
 * Tên các biến phải khớp chính xác với tên alias trong câu @Query.
 */
public class CategoryNameSum {

    // Khớp với 't.category_id as categoryId'
    public long categoryId;

    // Khớp với 'c.name as categoryName'
    public String categoryName;

    // Khớp với 'c.icon_name as categoryIcon'
    public String categoryIcon;

    // Khớp với 'c.color as categoryColor'
    public String categoryColor;

    // Khớp với 'SUM(t.amount) as totalAmount'
    public double totalAmount;

    /**
     * Constructor rỗng (Room cần).
     */
    public CategoryNameSum() {}
}