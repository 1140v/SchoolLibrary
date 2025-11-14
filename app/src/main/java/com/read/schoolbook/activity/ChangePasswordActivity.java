package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private MaterialButton saveButton, backButton;
    
    private User currentUser;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
    }

    private void initializeViews() {
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        saveButton.setOnClickListener(v -> changePassword());
    }
    
    private void changePassword() {
        // 获取输入的值
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // 验证输入
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "请输入当前密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证当前密码是否正确
        if (!currentPassword.equals(currentUser.getPassword())) {
            Toast.makeText(this, "当前密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "新密码和确认密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证新密码长度（至少6位）
        if (newPassword.length() < 6) {
            Toast.makeText(this, "新密码长度至少为6位", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证新密码不能与当前密码相同
        if (newPassword.equals(currentPassword)) {
            Toast.makeText(this, "新密码不能与当前密码相同", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 更新密码
        currentUser.setPassword(newPassword);
        
        // 保存到数据库
        boolean success = databaseHelper.updateUser(currentUser);
        
        if (success) {
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            
            // 返回结果
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedUser", currentUser);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}