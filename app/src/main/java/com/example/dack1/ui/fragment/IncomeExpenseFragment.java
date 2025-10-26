package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Thêm Toast để test

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction; // Đảm bảo import đúng model
import com.example.dack1.ui.adapter.TransactionAdapter;
import com.example.dack1.ui.view.AddTransactionActivity; // Import màn hình Add
import com.example.dack1.ui.view.EditTransactionActivity;
import com.example.dack1.ui.viewmodel.CategoryViewModel;
import com.example.dack1.ui.viewmodel.TransactionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. Implement 2 interface mới từ Adapter
public class IncomeExpenseFragment extends Fragment implements TransactionAdapter.OnItemClickListener, TransactionAdapter.OnDeleteClickListener {

    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionAdapter transactionAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTransaction;
    private TextView tvEmptyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_transactions);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);
        tvEmptyList = view.findViewById(R.id.tv_empty_list);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupRecyclerView();
        observeTransactions();
        observeCategories();

        // Gán sự kiện click cho nút "+" (OK)
        fabAddTransaction.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddTransactionActivity.class));
        });
    }

    private void setupRecyclerView() {
        // 2. Dùng constructor rỗng của ListAdapter
        transactionAdapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        // 3. Gán listener cho adapter
        transactionAdapter.setOnItemClickListener(this);
        transactionAdapter.setOnDeleteClickListener(this);
    }

    private void observeTransactions() {
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            // 4. Dùng "submitList" thay vì "setTransactions"
            transactionAdapter.submitList(transactions);
            
            // Show/hide empty state
            if (transactions == null || transactions.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyList.setVisibility(View.GONE);
            }
        });
    }

    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                // Create a map of category ID to Category object
                Map<Long, Category> categoryMap = new HashMap<>();
                for (Category category : categories) {
                    categoryMap.put(category.getId(), category);
                }
                // Pass the map to the adapter
                transactionAdapter.setCategoryMap(categoryMap);
            }
        });
    }

    // 5. Implement 2 hàm click
    @Override
    public void onItemClick(Transaction transaction) {
        // Xử lý Sửa (Edit)
        // Bỏ comment và trỏ đến EditTransactionActivity
        Intent intent = new Intent(getActivity(), EditTransactionActivity.class);

        // Gửi ID của giao dịch qua Intent
        intent.putExtra("TRANSACTION_ID", transaction.getId());

        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Transaction transaction) {
        // Xử lý Xóa (Delete)
        transactionViewModel.delete(transaction);
                    Toast.makeText(getContext(), getString(R.string.transaction_deleted, transaction.description), Toast.LENGTH_SHORT).show();
    }
}