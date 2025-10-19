package com.example.dack1.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private int selectedPosition = -1; // Vị trí ngày đang được chọn

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_calendar_day, parent, false);
        // Đảm bảo chiều cao của mỗi ô vừa đủ (7 cột)
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() / 6.5); // Chia cho 6.5 hàng
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);

        // ĐÂY LÀ CHỖ SỬA BUG "SỐ 12":
        // Đặt text là ngày thật, đè lên số 12 hardcoded
        holder.dayOfMonthTextView.setText(day);

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

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            // ĐÃ SỬA: trỏ đến R.id.dayNumberTextView
            this.dayOfMonthTextView = itemView.findViewById(R.id.dayNumberTextView);
        }
    }
}