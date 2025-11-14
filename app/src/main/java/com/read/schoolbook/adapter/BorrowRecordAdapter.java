package com.read.schoolbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.model.BorrowRecord;

import java.util.List;

public class BorrowRecordAdapter extends RecyclerView.Adapter<BorrowRecordAdapter.BorrowRecordViewHolder> {

    private List<BorrowRecord> borrowRecordList;
    private OnBorrowRecordActionListener listener;
    private boolean isUserMode; // 是否为用户模式
    private String currentUsername; // 当前登录用户名

    public interface OnBorrowRecordActionListener {
        void onBorrowRecordClick(BorrowRecord borrowRecord);
        void onReturnBookClick(BorrowRecord borrowRecord);
        void onDeleteRecordClick(BorrowRecord borrowRecord);
    }

    public BorrowRecordAdapter(List<BorrowRecord> borrowRecordList, OnBorrowRecordActionListener listener) {
        this.borrowRecordList = borrowRecordList;
        this.listener = listener;
        this.isUserMode = false; // 默认为管理员模式
        this.currentUsername = null;
    }

    public BorrowRecordAdapter(List<BorrowRecord> borrowRecordList, OnBorrowRecordActionListener listener, boolean isUserMode) {
        this.borrowRecordList = borrowRecordList;
        this.listener = listener;
        this.isUserMode = isUserMode;
        this.currentUsername = null;
    }

    public BorrowRecordAdapter(List<BorrowRecord> borrowRecordList, OnBorrowRecordActionListener listener, boolean isUserMode, String currentUsername) {
        this.borrowRecordList = borrowRecordList;
        this.listener = listener;
        this.isUserMode = isUserMode;
        this.currentUsername = currentUsername;
    }

    @NonNull
    @Override
    public BorrowRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow_record, parent, false);
        return new BorrowRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowRecordViewHolder holder, int position) {
        BorrowRecord borrowRecord = borrowRecordList.get(position);
        
        // 设置借阅记录信息
        holder.titleText.setText(borrowRecord.getTitle());
        holder.authorText.setText("作者：" + borrowRecord.getAuthor());
        holder.usernameText.setText("借阅人：" + borrowRecord.getUsername());
        holder.borrowTimeText.setText("借阅时间：" + borrowRecord.getBorrowTime());
        holder.statusText.setText("状态：" + borrowRecord.getStatusText());
        holder.overdueStatusText.setText("逾期状态：" + borrowRecord.getOverdueStatus());
        
        // 设置还书时间（如果已归还）
        if (borrowRecord.getReturnTime() != null && !borrowRecord.getReturnTime().isEmpty()) {
            holder.returnTimeText.setText("归还时间：" + borrowRecord.getReturnTime());
            holder.returnTimeText.setVisibility(View.VISIBLE);
        } else {
            holder.returnTimeText.setVisibility(View.GONE);
        }
        
        // 设置逾期天数（如果有逾期）
        if (borrowRecord.getOverdueDays() > 0) {
            holder.overdueDaysText.setText("逾期天数：" + borrowRecord.getOverdueDays() + "天");
            holder.overdueDaysText.setVisibility(View.VISIBLE);
        } else {
            holder.overdueDaysText.setVisibility(View.GONE);
        }
        
        // 根据状态设置还书按钮的可见性
        if (borrowRecord.getStatus() == 1) { // 进行中
            holder.returnButton.setVisibility(View.VISIBLE);
            holder.returnButton.setText("还书");
        } else {
            holder.returnButton.setVisibility(View.GONE);
        }
        
        // 设置删除按钮的可见性
        // 用户模式下完全隐藏删除按钮
        // 管理员模式下，只能删除其他用户的记录，不能删除自己的记录
        if (isUserMode) {
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            // 管理员模式下，检查是否是当前用户的记录
            if (currentUsername != null && currentUsername.equals(borrowRecord.getUsername())) {
                // 这是管理员自己的记录，不允许删除
                holder.deleteButton.setVisibility(View.GONE);
            } else {
                // 这是其他用户的记录，允许管理员删除
                holder.deleteButton.setVisibility(View.VISIBLE);
            }
        }
        
        // 设置点击监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBorrowRecordClick(borrowRecord);
                }
            }
        });
        
        // 还书按钮点击事件
        holder.returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReturnBookClick(borrowRecord);
                }
            }
        });
        
        // 删除按钮点击事件
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteRecordClick(borrowRecord);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowRecordList.size();
    }

    public void updateData(List<BorrowRecord> newBorrowRecordList) {
        this.borrowRecordList = newBorrowRecordList;
        notifyDataSetChanged();
    }

    static class BorrowRecordViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView authorText;
        TextView usernameText;
        TextView borrowTimeText;
        TextView returnTimeText;
        TextView statusText;
        TextView overdueStatusText;
        TextView overdueDaysText;
        MaterialButton returnButton;
        MaterialButton deleteButton;

        public BorrowRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            
            titleText = itemView.findViewById(R.id.titleText);
            authorText = itemView.findViewById(R.id.authorText);
            usernameText = itemView.findViewById(R.id.usernameText);
            borrowTimeText = itemView.findViewById(R.id.borrowTimeText);
            returnTimeText = itemView.findViewById(R.id.returnTimeText);
            statusText = itemView.findViewById(R.id.statusText);
            overdueStatusText = itemView.findViewById(R.id.overdueStatusText);
            overdueDaysText = itemView.findViewById(R.id.overdueDaysText);
            returnButton = itemView.findViewById(R.id.returnButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}