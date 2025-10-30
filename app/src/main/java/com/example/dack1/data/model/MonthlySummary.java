package com.example.dack1.data.model;

/**
 * POJO giữ tổng thu nhập và chi tiêu theo tháng.
 */
public class MonthlySummary {

    public String monthYear;
    public double totalIncome;
    public double totalExpense;

    public MonthlySummary() {}

    public MonthlySummary(String monthYear, double totalIncome, double totalExpense) {
        this.monthYear = monthYear;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }
}
