package com.example.mainscreen3.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainscreen3.R;
import com.example.mainscreen3.data.local.model.ExpenseItem;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends ListAdapter<ExpenseItem, ExpenseAdapter.ExpenseViewHolder> {

    private final DecimalFormat formatter = new DecimalFormat("#,###,###", new java.text.DecimalFormatSymbols(Locale.US));

    public ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final DiffUtil.ItemCallback<ExpenseItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExpenseItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ExpenseItem oldItem, @NonNull ExpenseItem newItem) {
            return oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getDate().equals(newItem.getDate());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExpenseItem oldItem, @NonNull ExpenseItem newItem) {
            return oldItem.getNote().equals(newItem.getNote()) &&
                    oldItem.getType().equals(newItem.getType());
        }
    };

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        // Lấy item bằng getItem() thay vì từ list
        ExpenseItem currentItem = getItem(position);
        Context context = holder.itemView.getContext();

        // === 1. Xử lý Amount và Dấu ===
        double amount = currentItem.getAmount();
        String formattedAmount = formatter.format(amount);

        if (ExpenseItem.TYPE_EXPENSE.equals(currentItem.getType())) {
            formattedAmount = "-" + formattedAmount;
            holder.itemAmount.setText(formattedAmount);
            holder.itemView.findViewById(R.id.item_container).setBackgroundResource(R.drawable.list_item_background);
            holder.itemAmount.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if (ExpenseItem.TYPE_REVENUE.equals(currentItem.getType())) {
            holder.itemAmount.setText(formattedAmount);
            holder.itemView.findViewById(R.id.item_container).setBackgroundResource(R.drawable.list_item_background_green); //
            holder.itemAmount.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        // === 3. Gán các trường khác ===
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemCategory.setTextColor(context.getResources().getColor(android.R.color.white));
        holder.itemDate.setText(currentItem.getDate());
        holder.itemDate.setTextColor(0xC8FFFFFF);

        int iconResId = currentItem.getIconResource();
        holder.itemIcon.setImageResource(iconResId);

        if (currentItem.isCustomCategory() && currentItem.getColorResource() != 0) {
            try {
                int color = ContextCompat.getColor(context, currentItem.getColorResource());
                holder.itemIcon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
            } catch (Resources.NotFoundException e) {
                Log.e("ExpenseAdapter", "Invalid color resource ID: " + currentItem.getColorResource());
                holder.itemIcon.setColorFilter(null); // Bỏ filter nếu có lỗi
            }
        } else {
            // Đối với category mặc định hoặc không có màu, bỏ filter
            holder.itemIcon.setColorFilter(null);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIcon;
        TextView itemCategory;
        TextView itemAmount;
        TextView itemDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemCategory = itemView.findViewById(R.id.item_category);
            itemAmount = itemView.findViewById(R.id.item_amount);
            itemDate = itemView.findViewById(R.id.item_date);
        }
    }
}