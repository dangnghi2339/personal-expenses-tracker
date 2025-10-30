package com.example.dack1.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.DailySummary;
import com.example.dack1.util.FormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private int selectedPosition = -1; // Vị trí ngày đang được chọn
    private Map<String, DailySummary> dailySummaries;
    private Calendar currentMonth;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    public void setDailySummaries(Map<String, DailySummary> dailySummaries) {
        this.dailySummaries = dailySummaries;
        notifyDataSetChanged();
    }

    public void setCurrentMonth(Calendar currentMonth) {
        this.currentMonth = currentMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_calendar_day, parent, false);
        // Đảm bảo chiều cao của mỗi ô vừa đủ (7 cột)
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = (int) (parent.getHeight() / 6.5); // Chia cho 6.5 hàng
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);

        // Đặt text là ngày thật
        holder.dayOfMonthTextView.setText(day);

        // Show today dot if it's today
        Calendar today = Calendar.getInstance();
        boolean isToday = !day.isEmpty() && currentMonth != null &&
                currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                Integer.parseInt(day) == today.get(Calendar.DAY_OF_MONTH);
        holder.todayDot.setVisibility(isToday ? View.VISIBLE : View.GONE);

        // Show daily summaries if available
        if (!day.isEmpty() && dailySummaries != null && currentMonth != null) {
            String dateKey = formatDateKey(currentMonth, Integer.parseInt(day));
            DailySummary summary = dailySummaries.get(dateKey);
            
            if (summary != null) {
                holder.revenueTextView.setText(FormatUtils.formatCurrency(summary.totalIncome));
                holder.expenditureTextView.setText(FormatUtils.formatCurrency(summary.totalExpense));
                holder.revenueTextView.setVisibility(View.VISIBLE);
                holder.expenditureTextView.setVisibility(View.VISIBLE);
            } else {
                holder.revenueTextView.setVisibility(View.GONE);
                holder.expenditureTextView.setVisibility(View.GONE);
            }
        } else {
            holder.revenueTextView.setVisibility(View.GONE);
            holder.expenditureTextView.setVisibility(View.GONE);
        }

        // Xử lý logic hiển thị
        if (day.isEmpty()) {
            holder.itemView.setClickable(false); // Ô trống, không cho bấm
        } else {
            holder.itemView.setClickable(true);

            // Xử lý khi người dùng click
            holder.itemView.setOnClickListener(v -> {
                onItemListener.onItemClick(day);
                // Cập nhật vị trí được chọn và vẽ lại
                notifyItemChanged(selectedPosition); // Bỏ chọn ô cũ
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition); // Chọn ô mới
            });
        }

        // Đổi màu nền của ô được chọn
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.rounded_blue_background);
            holder.dayOfMonthTextView.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.dayOfMonthTextView.setTextColor(Color.BLACK);
        }
    }

    private String formatDateKey(Calendar month, int day) {
        return String.format("%04d-%02d-%02d", 
                month.get(Calendar.YEAR), 
                month.get(Calendar.MONTH) + 1, 
                day);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size(); // Luôn là 42 ô
    }

    // Interface để Fragment lắng nghe sự kiện click
    public interface OnItemListener {
        void onItemClick(String day);
    }

    // ViewHolder

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        // ID này phải khớp với file item_calendar_day.xml
        final TextView dayOfMonthTextView;
        final TextView revenueTextView;
        final TextView expenditureTextView;
        final View todayDot;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            this.dayOfMonthTextView = itemView.findViewById(R.id.dayNumberTextView);
            this.revenueTextView = itemView.findViewById(R.id.revenueTextView);
            this.expenditureTextView = itemView.findViewById(R.id.expenditureTextView);
            this.todayDot = itemView.findViewById(R.id.todayDot);
        }
    }
}