package com.example.dack1.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho bảng 'budgets'.
 * Lớp này sẽ lưu trữ các ngân sách mà người dùng thiết lập,
 * có thể là ngân sách tổng hoặc ngân sách cho một danh mục cụ thể.
 */
@Entity(tableName = "budgets",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL))
public class Budget {

    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * Hạn mức của ngân sách (số tiền tối đa được chi).
     */
    @ColumnInfo(name = "amount")
    public double amount;

    /**
     * Ngày bắt đầu áp dụng ngân sách (dạng Unix Timestamp).
     */
    @ColumnInfo(name = "start_date")
    public long startDate;

    /**
     * Ngày kết thúc áp dụng ngân sách (dạng Unix Timestamp).
     */
    @ColumnInfo(name = "end_date")
    public long endDate;

    /**
     * Khóa ngoại, liên kết đến danh mục.
     * Quan trọng: Ngân sách có thể áp dụng cho TẤT CẢ chi tiêu (ngân sách tổng)
     * hoặc chỉ cho MỘT danh mục cụ thể (ví dụ: ngân sách cho "Ăn uống").
     * Do đó, cột này CÓ THỂ CÓ GIÁ TRỊ NULL.
     * Chúng ta dùng kiểu Long (chữ L viết hoa) để cho phép giá trị null.
     */
    @ColumnInfo(name = "category_id", index = true)
    public Long categoryId;

    public Budget() {
    }

    public Budget(long id, double amount, long startDate, long endDate, Long categoryId) {
        this.id = id;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // Constructor, Getters và Setters
    // ...
}