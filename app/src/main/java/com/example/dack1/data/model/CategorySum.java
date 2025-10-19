package com.example.dack1.data.model;

// Đây không phải là một @Entity, nó chỉ là một class
// để giữ kết quả trả về từ một câu query đặc biệt.
public class CategorySum {

    // Tên biến phải khớp với tên cột trả về từ query
    public long categoryId;
    public double total;

    // Constructor rỗng (không bắt buộc nhưng nên có)
    public CategorySum() {}

    // Constructor đầy đủ
    public CategorySum(long categoryId, double total) {
        this.categoryId = categoryId;
        this.total = total;
    }
}