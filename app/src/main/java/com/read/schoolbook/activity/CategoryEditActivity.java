package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

public class CategoryEditActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText nameEditText;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private ImageView backButton;
    
    private User currentUser;
    private Category currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        // 获取传递的用户信息和类目信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        currentCategory = (Category) getIntent().getSerializableExtra("category");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 如果是编辑模式，填充数据
        if (currentCategory != null) {
            fillCategoryData();
        }
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // 保存按钮
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        // 取消按钮
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillCategoryData() {
        nameEditText.setText(currentCategory.getCategoryName());
    }

    private void saveCategory() {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        String name = nameEditText.getText().toString().trim();
        
        Category category = new Category();
        category.setCategoryName(name);
        
        if (currentCategory == null) {
            // 添加新类目
            long result = databaseHelper.addCategory(category);
            if (result != -1) {
                Toast.makeText(this, "添加类目成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "添加类目失败，可能已存在同名类目", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 更新现有类目
            category.setCategoryId(currentCategory.getCategoryId());
            boolean success = databaseHelper.updateCategory(category);
            if (success) {
                Toast.makeText(this, "更新类目成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "更新类目失败，可能已存在同名类目", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        String name = nameEditText.getText().toString().trim();
        
        if (name.isEmpty()) {
            nameEditText.setError("请输入类目名称");
            return false;
        }
        
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}