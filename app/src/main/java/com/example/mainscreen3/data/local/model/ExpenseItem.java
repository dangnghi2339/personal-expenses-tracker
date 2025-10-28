package com.example.mainscreen3.data.local.model;

import com.example.mainscreen3.R;

import java.io.Serializable;

public class ExpenseItem {
    public static final String TYPE_EXPENSE = "EXPENSE";
    public static final String TYPE_REVENUE = "REVENUE";

    private String category;
    private double amount;
    private String note;
    private String date;
    private String type;
    private int iconResource;
    private int colorResource;
    private boolean isCustomCategory;

    // Constructor
    public ExpenseItem(String category, double amount, String note, String date, String type, int iconResource, int colorResource, boolean isCustomCategory) {
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.type = type;
        this.iconResource = iconResource;
        this.colorResource = colorResource;
        this.isCustomCategory = isCustomCategory;
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public String getDate() { return date; }
    public String getType() { return type; }

    public int getIconResource() {

        if (this.iconResource != 0) {
            return this.iconResource;
        }

        if (TYPE_EXPENSE.equals(type)) {
            switch (category) {
                case "Market": return R.drawable.ic_market;
                case "Eat and drink": return R.drawable.ic_eat_drink;
                case "Shopping": return R.drawable.ic_shopping;
                case "Gasoline": return R.drawable.ic_gasoline;
                case "House": return R.drawable.ic_house;
                case "Electricity": return R.drawable.ic_electricity;
                case "Load phone": return R.drawable.ic_load_phone;
                case "School": return R.drawable.ic_school;
                case "Credit card": return R.drawable.ic_credit_card;
                default: return R.drawable.ic_default;
            }
        } else if (TYPE_REVENUE.equals(type)) {
            switch (category) {
                case "Salary": return R.drawable.ic_salary;
                case "Allowance": return R.drawable.ic_allowance;
                case "Bonus": return R.drawable.ic_bonus;
                case "External income": return R.drawable.ic_external_income;
                case "Invest": return R.drawable.ic_invest;
                default: return R.drawable.ic_default;
            }
        }
        return R.drawable.ic_default;
    }
    public int getColorResource() { return colorResource; }
    public boolean isCustomCategory() { return isCustomCategory; }
}
