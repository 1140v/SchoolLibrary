package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.read.schoolbook.R;
import com.read.schoolbook.adapter.BorrowRecordAdapter;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.BorrowRecord;
import com.read.schoolbook.model.User;

import java.util.ArrayList;
import java.util.List;

public class BorrowRecordListActivity extends AppCompatActivity implements BorrowRecordAdapter.OnBorrowRecordActionListener {

    private DatabaseHelper databaseHelper;
    private RecyclerView borrowRecordRecyclerView;
    private LinearLayoutManager layoutManager;
    private TextView emptyStateText;
    private TextInputEditText searchEditText;
    private MaterialButton searchButton;
    private MaterialButton backButton;
    
    private BorrowRecordAdapter borrowRecordAdapter;
    private List<BorrowRecord> originalBorrowRecordList;
    private List<BorrowRecord> filteredBorrowRecordList;
    
    private User currentUser;
    private User selectedUser; // 用于查看特定用户的借阅记录
    private String mode; // 模式："all"查看所有记录，"my"查看当前用户记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_record_list);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");
        mode = getIntent().getStringExtra("mode");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 初始化借阅记录列表
        initializeBorrowRecordList();
    }

    private void initializeViews() {
        borrowRecordRecyclerView = findViewById(R.id.borrowRecordRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        
        // 根据模式更新标题
        TextView titleText = findViewById(R.id.titleText);
        if (selectedUser != null) {
            titleText.setText("用户 " + selectedUser.getUsername() + " 的借阅记录");
        } else if ("my".equals(mode) && currentUser != null) {
            titleText.setText("我的借阅记录");
        } else {
            titleText.setText("借阅记录管理");
        }
        
        // 设置RecyclerView
        layoutManager = new LinearLayoutManager(this);
        borrowRecordRecyclerView.setLayoutManager(layoutManager);
    }

    private void setupClickListeners() {
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 搜索按钮
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void initializeBorrowRecordList() {
        // 获取借阅记录（根据模式）
        if (selectedUser != null) {
            originalBorrowRecordList = databaseHelper.getBorrowRecordsByUsername(selectedUser.getUsername());
        } else if ("my".equals(mode) && currentUser != null) {
            originalBorrowRecordList = databaseHelper.getBorrowRecordsByUsername(currentUser.getUsername());
        } else {
            originalBorrowRecordList = databaseHelper.getAllBorrowRecords();
        }
        filteredBorrowRecordList = new ArrayList<>(originalBorrowRecordList);
        
        // 创建适配器（用户模式下隐藏删除按钮）
        boolean isUserMode = "my".equals(mode);
        String currentUsername = currentUser != null ? currentUser.getUsername() : "";
        borrowRecordAdapter = new BorrowRecordAdapter(filteredBorrowRecordList, this, isUserMode, currentUsername);
        borrowRecordRecyclerView.setAdapter(borrowRecordAdapter);
        
        // 更新空状态显示
        updateEmptyState();
    }

    private void performSearch() {
        String keyword = searchEditText.getText().toString().trim();
        
        if (keyword.isEmpty()) {
            // 如果搜索关键词为空，显示所有记录
            filteredBorrowRecordList.clear();
            filteredBorrowRecordList.addAll(originalBorrowRecordList);
        } else {
            // 根据关键词过滤记录
            filteredBorrowRecordList.clear();
            for (BorrowRecord record : originalBorrowRecordList) {
                if (record.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    record.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                    record.getIsbn().toLowerCase().contains(keyword.toLowerCase()) ||
                    record.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredBorrowRecordList.add(record);
                }
            }
        }
        
        // 通知适配器数据已更改
        borrowRecordAdapter.notifyDataSetChanged();
        
        // 更新空状态显示
        updateEmptyState();
        
        // 显示搜索结果数量
        Toast.makeText(this, "找到 " + filteredBorrowRecordList.size() + " 条记录", Toast.LENGTH_SHORT).show();
    }

    private void updateEmptyState() {
        if (filteredBorrowRecordList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            borrowRecordRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            borrowRecordRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBorrowRecordClick(BorrowRecord borrowRecord) {
        // 处理借阅记录点击事件
        Intent intent = new Intent(this, BorrowRecordDetailActivity.class);
        intent.putExtra("borrowRecord", borrowRecord);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    @Override
    public void onReturnBookClick(BorrowRecord borrowRecord) {
        // 处理还书操作
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
            // 刷新列表
            initializeBorrowRecordList();
        } else {
            Toast.makeText(this, "还书失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteRecordClick(BorrowRecord borrowRecord) {
        // 处理删除借阅记录操作
        boolean success = databaseHelper.deleteBorrowRecord(borrowRecord.getRecordId());
        
        if (success) {
            Toast.makeText(this, "删除记录成功", Toast.LENGTH_SHORT).show();
            // 刷新列表
            initializeBorrowRecordList();
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