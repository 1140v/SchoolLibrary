package com.read.schoolbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.read.schoolbook.R;
import com.read.schoolbook.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnItemClickListener onItemClickListener;

    public CategoryAdapter() {
        this.categoryList = new ArrayList<>();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
        
        // 设置点击事件
        holder.editButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(category);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView idText;
        private ImageView editButton;
        private ImageView deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            idText = itemView.findViewById(R.id.idText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Category category) {
            nameText.setText(category.getCategoryName());
            idText.setText("ID: " + category.getCategoryId());
        }
    }
}