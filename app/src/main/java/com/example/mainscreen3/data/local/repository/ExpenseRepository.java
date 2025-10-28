package com.example.mainscreen3.data.local.repository;

import com.example.mainscreen3.data.local.model.ExpenseItem;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private static volatile ExpenseRepository INSTANCE;

    private final List<ExpenseItem> inMemoryExpenseList = new ArrayList<>();

    public ExpenseRepository() {
    }

    public static ExpenseRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (ExpenseRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExpenseRepository();
                }
            }
        }
        return INSTANCE;
    }

    public List<ExpenseItem> getExpenses() {
        return new ArrayList<>(inMemoryExpenseList);
    }

    public void addExpense(ExpenseItem item) {
        inMemoryExpenseList.add(0, item);
    }


}
