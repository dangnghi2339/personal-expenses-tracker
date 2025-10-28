package com.example.mainscreen3.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainscreen3.data.local.model.ExpenseItem;
import com.example.mainscreen3.data.local.repository.ExpenseRepository;

import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {
    private final ExpenseRepository expenseRepository;

    private final MutableLiveData<List<ExpenseItem>> _expenseList = new MutableLiveData<>();
    private final MutableLiveData<String> _totalAmount = new MutableLiveData<>("0");
    private final MutableLiveData<String> _totalIncome = new MutableLiveData<>("Income\n0");
    private final MutableLiveData<String> _totalSpending = new MutableLiveData<>("Spending\n0");

    public LiveData<List<ExpenseItem>> expenseList = _expenseList;
    public LiveData<String> totalAmount = _totalAmount;
    public LiveData<String> totalIncome = _totalIncome;
    public LiveData<String> totalSpending = _totalSpending;

    public MainViewModel(@NonNull Application application) {
        super(application);
        expenseRepository = ExpenseRepository.getInstance();

        loadInitialExpenses();
    }

    private void loadInitialExpenses() {
        List<ExpenseItem> currentList = expenseRepository.getExpenses();

        _expenseList.setValue(currentList);

        recalculateSummary(currentList);
    }

    public void addExpense(ExpenseItem item) {

        expenseRepository.addExpense(item);

        List<ExpenseItem> newList = expenseRepository.getExpenses();

        _expenseList.setValue(newList);

        recalculateSummary(newList);
    }
    private void recalculateSummary(List<ExpenseItem> list) {
        double totalSpending = 0;
        double totalIncome = 0;

        for (ExpenseItem item : list) {
            if (ExpenseItem.TYPE_EXPENSE.equals(item.getType())) {
                totalSpending += item.getAmount();
            } else if (ExpenseItem.TYPE_REVENUE.equals(item.getType())) {
                totalIncome += item.getAmount();
            }
        }

        double totalAmount = totalIncome - totalSpending;

        _totalAmount.setValue(String.format(Locale.US, "%,.0f", totalAmount));
        _totalIncome.setValue(String.format(Locale.US, "Income\n%,.0f", totalIncome));
        _totalSpending.setValue(String.format(Locale.US, "Spending\n%,.0f", totalSpending));
    }

}
