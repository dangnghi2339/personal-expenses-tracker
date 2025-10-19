package com.example.dack1.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private OnCategoryActionListener listener;

    public CategoryAdapter(@NonNull OnCategoryActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = getItem(position);
        holder.tvCategoryName.setText(currentCategory.getName());
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private ImageButton ivEdit, ivDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            ivEdit = itemView.findViewById(R.id.iv_edit_category);
            ivDelete = itemView.findViewById(R.id.iv_delete_category);

            ivEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });

            ivDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });
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
            return oldItem.getName().equals(newItem.getName());
        }
    };

    // --- Interface for actions ---
    public interface OnCategoryActionListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }
}