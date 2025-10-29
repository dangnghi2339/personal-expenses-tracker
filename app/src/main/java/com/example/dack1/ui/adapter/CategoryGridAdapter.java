package com.example.dack1.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.util.FormatUtils;

public class CategoryGridAdapter extends ListAdapter<Category, CategoryGridAdapter.CategoryViewHolder> {

    private long selectedCategoryId = -1;
    private OnCategorySelectedListener listener;

    public CategoryGridAdapter() {
        super(DIFF_CALLBACK);
    }

    // --- Interface để báo cho Activity biết Category nào được chọn ---
    public interface OnCategorySelectedListener {
        void onCategorySelected(long categoryId);
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    // --- Hàm để Activity set ID cần highlight (khi sửa) hoặc reset ---
    public void setSelectedCategoryId(long categoryId) {
        long previousSelectedId = selectedCategoryId;
        selectedCategoryId = categoryId;
        // Thông báo thay đổi cho item cũ và mới để cập nhật highlight
        if (previousSelectedId != -1) {
            notifyItemChanged(findPositionById(previousSelectedId));
        }
        if (selectedCategoryId != -1) {
            notifyItemChanged(findPositionById(selectedCategoryId));
        }
        // notifyDataSetChanged(); // Dùng cách này nếu khó tìm position
    }

    // --- Hàm lấy ID đang được chọn ---
    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_grid, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = getItem(position);
        holder.bind(currentCategory);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImageView;
        private final TextView nameTextView;
        // SỬA: Thay CardView thành MaterialCardView
        private final MaterialCardView cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iv_category_icon);
            nameTextView = itemView.findViewById(R.id.tv_category_name);
            // SỬA: Ép kiểu sang MaterialCardView
            cardView = (MaterialCardView) itemView;

            // Sự kiện click (giữ nguyên)
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Category clickedCategory = getItem(position);
                    long clickedId = clickedCategory.getId();
                    setSelectedCategoryId(clickedId);
                    if (listener != null) {
                        listener.onCategorySelected(clickedId);
                    }
                }
            });
        }

        // Hàm bind dữ liệu và xử lý highlight (BÂY GIỜ SẼ HOẠT ĐỘNG)
        void bind(Category category) {
            nameTextView.setText(category.getName());

            Context context = itemView.getContext();
            int iconRes = FormatUtils.getDrawableIdByName(context, category.getIconName());
            if (iconRes != 0) {
                iconImageView.setImageResource(iconRes);
            } else {
                iconImageView.setImageResource(R.drawable.ic_profile);
            }

            // Set màu icon (giữ nguyên)
            try {
                if (category.getColor() != null && !category.getColor().isEmpty()) {
                    int color = FormatUtils.parseColorSafe(category.getColor());
                    iconImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                } else {
                    iconImageView.clearColorFilter();
                }
            } catch (Exception e) {
                iconImageView.clearColorFilter();
            }

            // Xử lý highlight viền xanh khi được chọn (Bây giờ sẽ không lỗi)
            if (category.getId() == selectedCategoryId) {
                cardView.setStrokeWidth(4); // Độ dày viền
                cardView.setStrokeColor(ContextCompat.getColor(context, R.color.colorPrimary)); // Màu xanh
            } else {
                cardView.setStrokeWidth(0); // Bỏ viền
                // cardView.setStrokeColor(Color.TRANSPARENT); // Không cần dòng này khi width = 0
            }
        }
    }

    // --- DiffUtil Callback ---
    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getIconName().equals(newItem.getIconName()) &&
                    oldItem.getColor().equals(newItem.getColor());
        }
    };

    // --- Helper tìm vị trí item theo ID ---
    private int findPositionById(long categoryId) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getId() == categoryId) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }
}