package com.example.mainscreen3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ExpenseEntryFragment.OnExpenseSavedListener {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<ExpenseItem> expenseList;
    private TextView tvTotalAmount, tvSpending, tvIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSpending = findViewById(R.id.tv_spending);
        fabAdd = findViewById(R.id.fab_add);

        expenseList = new ArrayList<>();

        recyclerView = findViewById(R.id.expense_recycler_view);
        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            ExpenseEntryFragment fragment = new ExpenseEntryFragment();
            fragment.show(getSupportFragmentManager(), "ExpenseEntryDialog");
        });
        updateSummary();
    }

    @Override
    public void onExpenseSaved(ExpenseItem item) {
        expenseList.add(0, item);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
        updateSummary();
    }

    private void updateSummary() {
        double totalSpending = 0;
        double totalIncome = 0;

        for (ExpenseItem item : expenseList) {
            if (ExpenseItem.TYPE_EXPENSE.equals(item.getType())) {
                totalSpending += item.getAmount();
            } else if (ExpenseItem.TYPE_REVENUE.equals(item.getType())) {
                totalIncome += item.getAmount();
            }
        }

        double totalAmount = totalIncome - totalSpending;

        tvTotalAmount.setText(String.format(Locale.US, "%,.0f", totalAmount));

        tvIncome = findViewById(R.id.tv_income);
        tvSpending = findViewById(R.id.tv_spending);

        tvIncome.setText(String.format(Locale.US, "Income\n%,.0f", totalIncome));

        tvSpending.setText(String.format(Locale.US, "Spending\n%,.0f", totalSpending));
    }
}