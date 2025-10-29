package com.example.dack1.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

import com.example.dack1.R;
import com.example.dack1.data.model.CategoryNameSum;
import com.example.dack1.util.FormatUtils; // Import tiện ích của bạn

import java.util.List;

public class CategoryReportAdapter extends ListAdapter<CategoryNameSum, CategoryReportAdapter.CategoryReportViewHolder> {

    private String currentType = "expense"; // Mặc định là chi tiêu
    private double grandTotal = 1.0; // Tổng số tiền (để tính %)
    private OnItemClickListener listener;
    private Context context; // Cần context để lấy màu và icon

    public CategoryReportAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil để RecyclerView biết item nào thay đổi
    private static final DiffUtil.ItemCallback<CategoryNameSum> DIFF_CALLBACK = new DiffUtil.ItemCallback<CategoryNameSum>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryNameSum oldItem, @NonNull CategoryNameSum newItem) {
            return oldItem.categoryId == newItem.categoryId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryNameSum oldItem, @NonNull CategoryNameSum newItem) {
            return oldItem.totalAmount == newItem.totalAmount &&
                    oldItem.categoryName.equals(newItem.categoryName);
        }
    };

    @NonNull
    @Override
    public CategoryReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lấy context từ parent
        this.context = parent.getContext();
        // Inflate layout item_category_report.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_report, parent, false);
        return new CategoryReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryReportViewHolder holder, int position) {
        CategoryNameSum item = getItem(position);

        holder.tvCategoryName.setText(item.categoryName);

        // --- Logic tính toán và định dạng ---

        // 1. Tính toán tỷ lệ phần trăm
        double percentage = (item.totalAmount / grandTotal) * 100;
        holder.tvCategoryPercentage.setText(String.format("%.1f%%", percentage));

        // 2. Định dạng tiền tệ
        holder.tvCategoryAmount.setText(FormatUtils.formatCurrency(item.totalAmount));

        // 3. Logic Đổi màu (như bạn yêu cầu)
        if (currentType.equals("income")) {
            holder.tvCategoryAmount.setTextColor(ContextCompat.getColor(context, R.color.green)); // Màu xanh
        } else {
            holder.tvCategoryAmount.setTextColor(ContextCompat.getColor(context, R.color.red)); // Màu đỏ
        }

        // 4. Set Icon (dùng hàm tiện ích của bạn)
        int iconResId = FormatUtils.getDrawableIdByName(context, item.categoryIcon);
        holder.ivCategoryIcon.setImageResource(iconResId);

        // 5. Set màu nền cho Icon
        try {
            int color = Color.parseColor(item.categoryColor);
            Drawable background = holder.ivCategoryIcon.getBackground();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(color);
            } else {
                // Nếu background không phải là shape, tạo một shape mới
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(12); // Đặt bo góc (tùy chỉnh)
                shape.setColor(color);
                holder.ivCategoryIcon.setBackground(shape);
            }
            holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            holder.ivCategoryIcon.setPadding(8, 8, 8, 8); // Đảm bảo icon có padding
        } catch (Exception e) {
            // Xử lý nếu màu bị lỗi, dùng màu mặc định
            holder.ivCategoryIcon.setBackgroundColor(Color.LTGRAY);
        }

        // Gắn listener cho Giai đoạn 5
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(getItem(position));
            }
        });
    }

    // --- Các hàm Public để Fragment gọi ---

    /**
     * Fragment gọi hàm này khi đổi tab Thu/Chi
     */
    public void setTransactionType(String type) {
        this.currentType = type;
    }

    /**
     * Fragment gọi hàm này khi có dữ liệu mới để tính toán %
     */
    public void setGrandTotal(double total) {
        this.grandTotal = (total == 0) ? 1.0 : total; // Tránh chia cho 0
    }

    // --- Interface cho Click Listener (Giai đoạn 5) ---
    public interface OnItemClickListener {
        void onItemClick(CategoryNameSum item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // --- ViewHolder ---
    static class CategoryReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvCategoryPercentage, tvCategoryAmount;

        public CategoryReportViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ view từ item_category_report.xml
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryPercentage = itemView.findViewById(R.id.tvCategoryPercentage);
            tvCategoryAmount = itemView.findViewById(R.id.tvCategoryAmount);
        }
    }
}