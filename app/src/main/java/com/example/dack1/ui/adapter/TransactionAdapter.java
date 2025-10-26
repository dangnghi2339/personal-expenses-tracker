package com.example.dack1.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.data.model.Transaction;
import com.example.dack1.util.FormatUtils;
import java.util.HashMap;
import java.util.Map;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

	// 1. ĐỊNH NGHĨA BIẾN LISTENER
	private OnItemClickListener itemClickListener;
	private OnDeleteClickListener deleteClickListener;

	private Map<Long, Category> categoryIdToCategory = new HashMap<>();

	public TransactionAdapter() {
		super(DIFF_CALLBACK);
	}

	public void setCategoryMap(Map<Long, Category> categoryMap) {
		if (categoryMap != null) {
			this.categoryIdToCategory = categoryMap;
			notifyDataSetChanged();
		}
	}

	private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
		@Override
		public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
			return oldItem.id == newItem.id;
		}

		@Override
		public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
			// So sánh thêm các trường khác nếu cần
			return oldItem.description.equals(newItem.description) &&
					oldItem.amount == newItem.amount;
		}
	};

	@NonNull
	@Override
	public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.recyclerview_item, parent, false);
		return new TransactionViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
		Transaction currentTransaction = getItem(position);
		holder.descriptionTextView.setText(currentTransaction.description);
		String amountString = FormatUtils.formatCurrency(currentTransaction.amount);
		holder.amountTextView.setText(amountString);

		Context ctx = holder.itemView.getContext();
		if ("income".equalsIgnoreCase(currentTransaction.type)) {
			holder.amountTextView.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_green_dark));
		} else {
			holder.amountTextView.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_red_dark));
		}

		Category category = categoryIdToCategory.get(currentTransaction.categoryId);
		if (category != null) {
			holder.categoryNameTextView.setText(category.getName());
			int iconRes = FormatUtils.getDrawableIdByName(ctx, category.getIconName());
			if (iconRes != 0) {
				holder.iconImageView.setImageResource(iconRes);
			} else {
				holder.iconImageView.setImageResource(R.drawable.ic_profile);
			}
			try {
				if (category.getColor() != null && !category.getColor().isEmpty()) {
					int color = FormatUtils.parseColorSafe(category.getColor());
					holder.iconImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
				} else {
					holder.iconImageView.clearColorFilter();
				}
			} catch (Exception e) {
				holder.iconImageView.clearColorFilter();
			}
		} else {
			holder.categoryNameTextView.setText(R.string.unknown_category);
			holder.iconImageView.setImageResource(R.drawable.ic_profile);
			holder.iconImageView.clearColorFilter();
		}
	}

	// 2. NÂNG CẤP VIEWHOLDER ĐỂ GÁN CLICK
	class TransactionViewHolder extends RecyclerView.ViewHolder {
		private final TextView descriptionTextView;
		private final TextView amountTextView;
		private final TextView categoryNameTextView;
		private final ImageView iconImageView;
		private final ImageButton deleteButton;
		// TODO: Thêm nút xóa từ layout (ví dụ: iv_delete)
		// private final ImageButton deleteButton;

		public TransactionViewHolder(@NonNull View itemView) {
			super(itemView);
			descriptionTextView = itemView.findViewById(R.id.textViewDescription);
			amountTextView = itemView.findViewById(R.id.textViewAmount);
			categoryNameTextView = itemView.findViewById(R.id.tv_item_category_name);
			iconImageView = itemView.findViewById(R.id.iv_item_category_icon);

			deleteButton = itemView.findViewById(R.id.iv_delete); // Ánh xạ nút xóa

			// 3. GÁN SỰ KIỆN CLICK CHO TOÀN BỘ ITEM (ĐỂ SỬA)
			itemView.setOnClickListener(v -> {
				int position = getAdapterPosition();
				if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
					itemClickListener.onItemClick(getItem(position));
				}
			});

			deleteButton.setOnClickListener(v -> {
				int position = getAdapterPosition();
				if (deleteClickListener != null && position != RecyclerView.NO_POSITION) {
					// Gọi listener trong Fragment, truyền Transaction cần xóa
					deleteClickListener.onDeleteClick(getItem(position));
				}
			});
		}
	}

	// 5. ĐỊNH NGHĨA INTERFACE VÀ HÀM SETTER
	public interface OnItemClickListener {
		void onItemClick(Transaction transaction);
	}

	public interface OnDeleteClickListener {
		void onDeleteClick(Transaction transaction);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}

	public void setOnDeleteClickListener(OnDeleteClickListener listener) {
		this.deleteClickListener = listener;
	}
}