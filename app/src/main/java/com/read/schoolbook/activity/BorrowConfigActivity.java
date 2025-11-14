package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.BorrowConfig;
import com.read.schoolbook.model.User;

public class BorrowConfigActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private User currentUser;
    
    private EditText borrowDaysEditText;
    private EditText maxBooksEditText;
    private TextView currentConfigText;
    private Button saveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_config);

        // 获取当前用户信息
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("currentUser")) {
            currentUser = (User) intent.getSerializableExtra("currentUser");
        }

        // 初始化数据库
        databaseHelper = new DatabaseHelper(this);

        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 加载当前配置
        loadCurrentConfig();
    }

    private void initializeViews() {
        borrowDaysEditText = findViewById(R.id.borrowDaysEditText);
        maxBooksEditText = findViewById(R.id.maxBooksEditText);
        currentConfigText = findViewById(R.id.currentConfigText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBorrowConfig();
            }
        });
    }

    private void loadCurrentConfig() {
        BorrowConfig config = databaseHelper.getBorrowConfig();
        if (config != null) {
            String configInfo = "当前配置：\n" +
                    "借阅天数：" + config.getMaxBorrowDays() + "天\n" +
                    "最大借书数量：" + config.getMaxBorrowCount() + "本\n" +
                    "最后更新时间：" + config.getUpdateTime();
            currentConfigText.setText(configInfo);
            
            // 设置当前值到输入框
            borrowDaysEditText.setText(String.valueOf(config.getMaxBorrowDays()));
            maxBooksEditText.setText(String.valueOf(config.getMaxBorrowCount()));
        } else {
            currentConfigText.setText("当前配置：暂无配置");
        }
    }

    private void saveBorrowConfig() {
        String borrowDaysStr = borrowDaysEditText.getText().toString().trim();
        String maxBooksStr = maxBooksEditText.getText().toString().trim();

        if (TextUtils.isEmpty(borrowDaysStr)) {
            Toast.makeText(this, "请输入借阅天数", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(maxBooksStr)) {
            Toast.makeText(this, "请输入最大借书数量", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int borrowDays = Integer.parseInt(borrowDaysStr);
            int maxBooks = Integer.parseInt(maxBooksStr);

            if (borrowDays <= 0) {
                Toast.makeText(this, "借阅天数必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (maxBooks <= 0) {
                Toast.makeText(this, "最大借书数量必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 更新配置
            boolean success = databaseHelper.updateBorrowConfig(borrowDays, maxBooks);
            
            if (success) {
                Toast.makeText(this, "配置保存成功", Toast.LENGTH_SHORT).show();
                loadCurrentConfig(); // 刷新显示
            } else {
                Toast.makeText(this, "配置保存失败", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
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