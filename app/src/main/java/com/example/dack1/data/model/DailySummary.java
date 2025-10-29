package com.example.dack1.data.model;

/**
 * POJO giữ tổng thu nhập và chi tiêu theo ngày.
 */
public class DailySummary {

    public double totalIncome;
    public double totalExpense;

    public DailySummary() {}

    public DailySummary(double totalIncome, double totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }
}
