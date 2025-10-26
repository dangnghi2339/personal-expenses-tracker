package com.example.mainscreen3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final List<ExpenseItem> expenseList;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###", new java.text.DecimalFormatSymbols(Locale.US));

    public ExpenseAdapter(List<ExpenseItem> expenseList) {
        this.expenseList = expenseList;
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

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gắn layout expense_list_item.xml vào ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseItem currentItem = expenseList.get(position);
        Context context = holder.itemView.getContext();

        // === 1. Xử lý Amount và Dấu ===
        double amount = currentItem.getAmount();
        String formattedAmount = formatter.format(amount);

        if (ExpenseItem.TYPE_EXPENSE.equals(currentItem.getType())) {
            // --- EXPENSE ---
            formattedAmount = "-" + formattedAmount;
            holder.itemAmount.setText(formattedAmount);

            // Đổi màu nền của item (cần ID list_item_background trong expense_list_item)
            // Nếu bạn dùng CardView, bạn cần set background cho LinearLayout bên trong
            holder.itemView.findViewById(R.id.item_container).setBackgroundResource(R.drawable.list_item_background);

            // Màu chữ Amount/Category/Date là màu trắng (như bạn đã đặt)
            holder.itemAmount.setTextColor(context.getResources().getColor(android.R.color.white));

        } else if (ExpenseItem.TYPE_REVENUE.equals(currentItem.getType())) {
            // --- REVENUE (INCOME) ---
            holder.itemAmount.setText(formattedAmount); // Không có dấu trừ

            // Đổi màu nền sang Xanh Lá (Ví dụ: cần tạo @drawable/list_item_background_green)
            holder.itemView.findViewById(R.id.item_container).setBackgroundResource(R.drawable.list_item_background_green);

            // Màu chữ Amount/Category/Date là màu trắng (hoặc màu khác tùy ý)
            holder.itemAmount.setTextColor(context.getResources().getColor(android.R.color.white));
        }


        // === 3. Gán các trường khác ===
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemCategory.setTextColor(context.getResources().getColor(android.R.color.white));

        holder.itemDate.setText(currentItem.getDate());
        holder.itemDate.setTextColor(0xC8FFFFFF);

        // Gán Icon và Tint (Icon cho Revenue sẽ khác Expense)
        int iconResId = currentItem.getIconResource();
        holder.itemIcon.setImageResource(iconResId);

        int colorRes = currentItem.getColorResource();

        if (colorRes != 0) {
            int color = ContextCompat.getColor(context, colorRes);
            holder.itemIcon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.itemIcon.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}