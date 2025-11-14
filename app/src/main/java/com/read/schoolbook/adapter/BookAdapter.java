package com.read.schoolbook.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.read.schoolbook.R;
import com.read.schoolbook.model.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnItemClickListener onItemClickListener;
    private boolean isUserMode = false; // 默认为管理员模式

    public BookAdapter() {
        this.bookList = new ArrayList<>();
    }
    
    // 新增构造函数支持用户模式
    public BookAdapter(boolean isUserMode) {
        this.bookList = new ArrayList<>();
        this.isUserMode = isUserMode;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view, isUserMode);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(book);
            }
        });
        
        // 用户模式下隐藏编辑和删除按钮
        if (!isUserMode) {
            holder.editButton.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onEditClick(book);
                }
            });
            
            holder.deleteButton.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onDeleteClick(book);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
        void onEditClick(Book book);
        void onDeleteClick(Book book);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView authorText;
        private TextView isbnText;
        private TextView statusText;
        private TextView categoryText;
        private TextView locationText;
        private ImageView bookImage;
        private ImageView editButton;
        private ImageView deleteButton;

        public BookViewHolder(@NonNull View itemView, boolean isUserMode) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            authorText = itemView.findViewById(R.id.authorText);
            isbnText = itemView.findViewById(R.id.isbnText);
            statusText = itemView.findViewById(R.id.statusText);
            categoryText = itemView.findViewById(R.id.categoryText);
            locationText = itemView.findViewById(R.id.locationText);
            bookImage = itemView.findViewById(R.id.bookImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            
            // 用户模式下隐藏编辑和删除按钮
            if (isUserMode) {
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }
        }

        public void bind(Book book) {
            titleText.setText(book.getTitle());
            authorText.setText("作者：" + book.getAuthor());
            isbnText.setText("ISBN：" + book.getIsbn());
            statusText.setText(book.isBorrowed() ? "已借出" : "未借出");
            statusText.setTextColor(itemView.getContext().getColor(
                    book.isBorrowed() ? android.R.color.holo_red_dark : android.R.color.holo_blue_dark));
            categoryText.setText("类别：" + book.getCategoryId());
            locationText.setText("位置：" + book.getLocation());
            
            // 加载图书图片
            loadBookImage(book, bookImage);
        }
        
        /**
         * 加载图书图片到ImageView
         */
        private void loadBookImage(Book book, ImageView imageView) {
            if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
                if (book.getImagePath().startsWith("content://")) {
                    // 如果是Content URI，使用Uri.parse()处理
                    Glide.with(imageView.getContext())
                            .load(Uri.parse(book.getImagePath()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_book_placeholder)
                            .error(R.drawable.ic_book_placeholder)
                            .into(imageView);
                } else if (book.getImagePath().startsWith("file://")) {
                    // 如果是文件URI，使用Uri.parse()处理
                    Glide.with(imageView.getContext())
                            .load(Uri.parse(book.getImagePath()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_book_placeholder)
                            .error(R.drawable.ic_book_placeholder)
                            .into(imageView);
                } else if (!book.getImagePath().isEmpty()) {
                    // 如果是文件路径，使用File对象处理
                    File imageFile = new File(book.getImagePath());
                    if (imageFile.exists()) {
                        Glide.with(imageView.getContext())
                                .load(imageFile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_book_placeholder)
                                .error(R.drawable.ic_book_placeholder)
                                .into(imageView);
                    } else {
                        // 如果文件不存在，显示占位符
                        imageView.setImageResource(R.drawable.ic_book_placeholder);
                    }
                } else {
                    // 如果没有图片路径，显示占位符
                    imageView.setImageResource(R.drawable.ic_book_placeholder);
                }
            } else {
                // 如果没有图片路径，显示占位符
                imageView.setImageResource(R.drawable.ic_book_placeholder);
            }
        }
    }
}