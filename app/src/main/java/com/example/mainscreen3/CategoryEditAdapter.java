package com.example.mainscreen3;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryEditAdapter extends RecyclerView.Adapter<CategoryEditAdapter.CategoryViewHolder> {

    public interface OnCategoryItemClickListener {
        void onItemClicked(CategoryModel category);
    }
    private OnCategoryItemClickListener itemClickListener;
    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        this.itemClickListener = listener;
    }

    private List<CategoryModel> categoryList;

    public CategoryEditAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    public void updateList(List<CategoryModel> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_edit_list_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel item = categoryList.get(position);
        Context context = holder.itemView.getContext();

        holder.name.setText(item.getName());
        holder.icon.setImageResource(item.getIconResource());

        if (item.getColorResource() != 0) {
            try {
                int color = ContextCompat.getColor(context, item.getColorResource());

                holder.icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
            } catch (Resources.NotFoundException e) {
                int defaultColor = ContextCompat.getColor(context, R.color.colorPrimary);
                holder.icon.setColorFilter(defaultColor, android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            int defaultColor = ContextCompat.getColor(context, R.color.colorPrimary);
            holder.icon.setColorFilter(defaultColor, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_edit_icon);
            name = itemView.findViewById(R.id.category_edit_name);
        }
    }
}