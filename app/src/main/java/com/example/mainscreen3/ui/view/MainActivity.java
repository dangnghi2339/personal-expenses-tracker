package com.example.mainscreen3.ui.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainscreen3.ui.adapter.ExpenseAdapter;
import com.example.mainscreen3.ui.fragment.ExpenseEntryFragment;
import com.example.mainscreen3.R;
import com.example.mainscreen3.data.local.model.ExpenseItem;
import com.example.mainscreen3.ui.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private TextView tvTotalAmount, tvSpending, tvIncome;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSpending = findViewById(R.id.tv_spending);
        tvIncome = findViewById(R.id.tv_income);
        fabAdd = findViewById(R.id.fab_add);
        recyclerView = findViewById(R.id.expense_recycler_view);

        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            ExpenseEntryFragment fragment = new ExpenseEntryFragment();
            fragment.show(getSupportFragmentManager(), "ExpenseEntryDialog");
        });

        setupObservers();
    }

    private void setupObservers() {
        mainViewModel.expenseList.observe(this, newExpenseList -> {
            adapter.submitList(newExpenseList);
        });

        // Quan sát tổng tiền
        mainViewModel.totalAmount.observe(this, total -> {
            tvTotalAmount.setText(total);
        });

        // Quan sát tổng thu nhập
        mainViewModel.totalIncome.observe(this, income -> {
            tvIncome.setText(income);
        });

        // Quan sát tổng chi tiêu
        mainViewModel.totalSpending.observe(this, spending -> {
            tvSpending.setText(spending);
        });
    }
}