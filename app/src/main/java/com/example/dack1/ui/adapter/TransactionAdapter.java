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
import com.example.dack1.data.model.Transaction;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

    // 1. ĐỊNH NGHĨA BIẾN LISTENER
    private OnItemClickListener itemClickListener;
    private OnDeleteClickListener deleteClickListener;

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
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
        String amountString = String.format("%,.0f VND", currentTransaction.amount);
        holder.amountTextView.setText(amountString);
    }

    // 2. NÂNG CẤP VIEWHOLDER ĐỂ GÁN CLICK
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView descriptionTextView;
        private final TextView amountTextView;
        private final ImageButton deleteButton;
        // TODO: Thêm nút xóa từ layout (ví dụ: iv_delete)
        // private final ImageButton deleteButton;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            amountTextView = itemView.findViewById(R.id.textViewAmount);

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