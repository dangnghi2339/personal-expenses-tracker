package com.example.dack1.data.model;

/**
 * POJO để giữ kết quả query cho BarChart ở màn hình chi tiết danh mục.
 */
public class MonthlyCategorySummary {

    // Khớp với 'strftime(...) AS monthYear'
    public String monthYear;

    // Khớp với 'SUM(amount) AS totalAmount'
    public double totalAmount;

    public MonthlyCategorySummary() {}
}