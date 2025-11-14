package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.BorrowRecord;
import com.read.schoolbook.model.User;

public class BorrowRecordDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private User currentUser;
    private BorrowRecord borrowRecord;
    
    private ImageView backButton;
    private TextView titleText;
    private TextView authorText;
    private TextView isbnText;
    private TextView publisherText;
    private TextView publishDateText;
    private TextView locationText;
    private TextView usernameText;
    private TextView borrowTimeText;
    private TextView returnTimeText;
    private TextView statusText;
    private TextView overdueStatusText;
    private TextView overdueDaysText;
    private Button returnButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_record_detail);

        // 获取传递的借阅记录和用户信息
        Intent intent = getIntent();
        if (intent != null) {
            borrowRecord = (BorrowRecord) intent.getSerializableExtra("borrowRecord");
            currentUser = (User) intent.getSerializableExtra("currentUser");
        }

        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 填充借阅记录数据
        fillBorrowRecordData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        isbnText = findViewById(R.id.isbnText);
        publisherText = findViewById(R.id.publisherText);
        publishDateText = findViewById(R.id.publishDateText);
        locationText = findViewById(R.id.locationText);
        usernameText = findViewById(R.id.usernameText);
        borrowTimeText = findViewById(R.id.borrowTimeText);
        returnTimeText = findViewById(R.id.returnTimeText);
        statusText = findViewById(R.id.statusText);
        overdueStatusText = findViewById(R.id.overdueStatusText);
        overdueDaysText = findViewById(R.id.overdueDaysText);
        returnButton = findViewById(R.id.returnButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void setupClickListeners() {
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 还书按钮
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBook();
            }
        });
        
        // 删除按钮
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
            }
        });
    }

    private void fillBorrowRecordData() {
        if (borrowRecord != null) {
            titleText.setText(borrowRecord.getTitle());
            authorText.setText("作者：" + borrowRecord.getAuthor());
            isbnText.setText("ISBN：" + borrowRecord.getIsbn());
            publisherText.setText("出版社：" + borrowRecord.getPublisher());
            publishDateText.setText("出版日期：" + borrowRecord.getPublishDate());
            locationText.setText("位置：" + borrowRecord.getLocation());
            usernameText.setText("借阅人：" + borrowRecord.getUsername());
            borrowTimeText.setText("借阅时间：" + borrowRecord.getBorrowTime());
            
            // 设置归还时间（如果已归还）
            if (borrowRecord.getReturnTime() != null && !borrowRecord.getReturnTime().isEmpty()) {
                returnTimeText.setText("归还时间：" + borrowRecord.getReturnTime());
                returnTimeText.setVisibility(View.VISIBLE);
            } else {
                returnTimeText.setVisibility(View.GONE);
            }
            
            statusText.setText("状态：" + borrowRecord.getStatusText());
            overdueStatusText.setText("逾期状态：" + borrowRecord.getOverdueStatus());
            
            // 设置逾期天数（如果有逾期）
            if (borrowRecord.getOverdueDays() > 0) {
                overdueDaysText.setText("逾期天数：" + borrowRecord.getOverdueDays() + "天");
                overdueDaysText.setVisibility(View.VISIBLE);
            } else {
                overdueDaysText.setVisibility(View.GONE);
            }
            
            // 根据状态设置还书按钮的可见性
            if (borrowRecord.getStatus() == 1) { // 进行中
                returnButton.setVisibility(View.VISIBLE);
            } else {
                returnButton.setVisibility(View.GONE);
            }
            
            // 根据当前用户权限控制删除按钮显示
            if (currentUser != null && currentUser.isAdmin()) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    private void returnBook() {
        if (borrowRecord == null) {
            return;
        }
        
        // 获取当前时间
        String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        
        // 更新借阅记录状态为已归还
        boolean success = databaseHelper.updateBorrowRecordStatus(
            borrowRecord.getRecordId(), 
            2, // 已归还状态
            currentTime, 
            "正常", 
            0
        );
        
        if (success) {
            Toast.makeText(this, "还书成功", Toast.LENGTH_SHORT).show();
            
            // 更新图书状态为未借出
            databaseHelper.updateBookStatus(borrowRecord.getBookId(), 0);
            
            // 关闭当前活动
            finish();
        } else {
            Toast.makeText(this, "还书失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecord() {
        if (borrowRecord == null) {
            return;
        }
        
        // 删除借阅记录
        boolean success = databaseHelper.deleteBorrowRecord(borrowRecord.getRecordId());
        
        if (success) {
            Toast.makeText(this, "删除记录成功", Toast.LENGTH_SHORT).show();
            
            // 关闭当前活动
            finish();
        } else {
            Toast.makeText(this, "删除记录失败，请重试", Toast.LENGTH_SHORT).show();
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