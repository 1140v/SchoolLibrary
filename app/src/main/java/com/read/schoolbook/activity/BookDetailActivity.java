package com.read.schoolbook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Book;
import com.read.schoolbook.model.BorrowConfig;
import com.read.schoolbook.model.BorrowRecord;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

import java.io.File;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ImageView backButton;
    private ImageView bookImageView;
    private TextView titleText;
    private TextView authorText;
    private TextView isbnText;
    private TextView publisherText;
    private TextView publishDateText;
    private TextView categoryText;
    private TextView locationText;
    private TextView statusText;
    private TextView descriptionText;
    private MaterialButton borrowButton;
    
    private User currentUser;
    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // 获取传递的用户信息和图书信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        currentBook = (Book) getIntent().getSerializableExtra("book");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 填充图书数据
        fillBookData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        bookImageView = findViewById(R.id.bookImageView);
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        isbnText = findViewById(R.id.isbnText);
        publisherText = findViewById(R.id.publisherText);
        publishDateText = findViewById(R.id.publishDateText);
        categoryText = findViewById(R.id.categoryText);
        locationText = findViewById(R.id.locationText);
        statusText = findViewById(R.id.statusText);
        descriptionText = findViewById(R.id.descriptionText);
        borrowButton = findViewById(R.id.borrowButton);
    }

    private void setupClickListeners() {
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 借阅按钮
        borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrowBook();
            }
        });
    }

    private void fillBookData() {
        if (currentBook != null) {
            // 设置图书图片
            loadBookImage(currentBook, bookImageView);
            
            // 设置图书基本信息
            titleText.setText(currentBook.getTitle());
            authorText.setText("作者：" + currentBook.getAuthor());
            isbnText.setText("ISBN：" + currentBook.getIsbn());
            publisherText.setText("出版社：" + (currentBook.getPublisher() != null ? currentBook.getPublisher() : "未知"));
            publishDateText.setText("出版日期：" + (currentBook.getPublishDate() != null ? currentBook.getPublishDate() : "未知"));
            
            // 获取并设置类别名称
            Category category = databaseHelper.getCategoryById(currentBook.getCategoryId());
            categoryText.setText("类别：" + (category != null ? category.getCategoryName() : "未知"));
            
            locationText.setText("位置：" + currentBook.getLocation());
            statusText.setText("状态：" + (currentBook.isBorrowed() ? "已借出" : "未借出"));
            
            // 设置图书描述
            if (currentBook.getDescription() != null && !currentBook.getDescription().isEmpty()) {
                descriptionText.setText(currentBook.getDescription());
            } else {
                descriptionText.setText("暂无描述");
            }
            
            // 设置借阅按钮状态
            updateBorrowButton();
        }
    }
    
    private void updateBorrowButton() {
        if (currentBook.isBorrowed()) {
            borrowButton.setEnabled(false);
            borrowButton.setText("已借出");
        } else {
            borrowButton.setEnabled(true);
            borrowButton.setText("借阅");
        }
    }
    
    private void borrowBook() {
        // 检查用户是否已登录
        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查图书是否可借
        if (currentBook.isBorrowed()) {
            Toast.makeText(this, "该图书已被借出", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取借阅配置
        BorrowConfig config = databaseHelper.getBorrowConfig();
        if (config == null) {
            Toast.makeText(this, "系统配置错误，请联系管理员", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查用户当前借阅数量
        List<BorrowRecord> userRecords = databaseHelper.getBorrowRecordsByUsername(currentUser.getUsername());
        int currentBorrowCount = 0;
        for (BorrowRecord record : userRecords) {
            if (record.getStatus() == 1) { // 1表示进行中
                currentBorrowCount++;
            }
        }
        
        if (currentBorrowCount >= config.getMaxBorrowCount()) {
            Toast.makeText(this, "您已达到最大借书数量（" + config.getMaxBorrowCount() + "本）", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建借阅记录（设置所有必填字段，确保没有null值）
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBookId(currentBook.getBookId());
        borrowRecord.setIsbn(currentBook.getIsbn() != null ? currentBook.getIsbn() : "未知");
        borrowRecord.setTitle(currentBook.getTitle() != null ? currentBook.getTitle() : "未知");
        borrowRecord.setAuthor(currentBook.getAuthor() != null ? currentBook.getAuthor() : "未知");
        borrowRecord.setDescription(currentBook.getDescription());
        borrowRecord.setPublisher(currentBook.getPublisher() != null ? currentBook.getPublisher() : "未知");
        borrowRecord.setPublishDate(currentBook.getPublishDate() != null ? currentBook.getPublishDate() : "未知");
        borrowRecord.setImagePath(currentBook.getImagePath());
        borrowRecord.setLocation(currentBook.getLocation() != null ? currentBook.getLocation() : "未知");
        borrowRecord.setCategoryId(currentBook.getCategoryId());
        borrowRecord.setUsername(currentUser.getUsername());
        borrowRecord.setBorrowTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        borrowRecord.setExpectedReturnTime(getExpectedReturnTime(config.getMaxBorrowDays()));
        borrowRecord.setStatus(1); // 1表示进行中
        borrowRecord.setOverdueStatus("未逾期");
        borrowRecord.setOverdueDays(0);
        
        // 添加借阅记录
        long recordId = databaseHelper.addBorrowRecord(borrowRecord);
        boolean success = recordId != -1;
        
        if (success) {
            // 更新图书状态为已借出
            currentBook.setStatus(1);
            databaseHelper.updateBook(currentBook);
            
            Toast.makeText(this, "借阅成功", Toast.LENGTH_SHORT).show();
            updateBorrowButton();
            statusText.setText("状态：已借出");
        } else {
            Toast.makeText(this, "借阅失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getExpectedReturnTime(int borrowDays) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, borrowDays);
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }
    
    /**
     * 加载图书图片到ImageView
     */
    private void loadBookImage(Book book, ImageView imageView) {
        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            if (book.getImagePath().startsWith("content://")) {
                // 如果是Content URI，使用Uri.parse()处理
                Glide.with(this)
                        .load(Uri.parse(book.getImagePath()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_book_placeholder)
                        .error(R.drawable.ic_book_placeholder)
                        .into(imageView);
            } else if (book.getImagePath().startsWith("file://")) {
                // 如果是文件URI，使用Uri.parse()处理
                Glide.with(this)
                        .load(Uri.parse(book.getImagePath()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_book_placeholder)
                        .error(R.drawable.ic_book_placeholder)
                        .into(imageView);
            } else if (!book.getImagePath().isEmpty()) {
                // 如果是文件路径，使用File对象处理
                File imageFile = new File(book.getImagePath());
                if (imageFile.exists()) {
                    Glide.with(this)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}