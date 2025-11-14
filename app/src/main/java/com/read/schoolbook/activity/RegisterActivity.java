package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private MaterialButtonToggleGroup genderToggleGroup;
    private EditText ageEditText;
    private EditText contactEditText;
    private EditText addressEditText;
    private MaterialButton registerButton;
    private TextView loginTextView;
    private ImageView backButton;
    
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);

        // 初始化视图
        initializeViews();

        // 设置点击监听器
        setupClickListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        genderToggleGroup = findViewById(R.id.genderToggleGroup);
        ageEditText = findViewById(R.id.ageEditText);
        contactEditText = findViewById(R.id.contactEditText);
        addressEditText = findViewById(R.id.addressEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // 注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        // 登录链接点击事件
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void attemptRegister() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // 获取选中的性别
        String gender = getSelectedGender();

        // 验证输入
        if (username.isEmpty()) {
            showErrorToast("账号不能为空，请输入您的账号");
            usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showErrorToast("密码不能为空，请输入您的密码");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showErrorToast("密码长度至少需要6位，请重新输入");
            passwordEditText.requestFocus();
            return;
        }

        if (gender == null) {
            showErrorToast("请选择您的性别");
            return;
        }

        if (ageStr.isEmpty()) {
            showErrorToast("年龄不能为空，请输入您的年龄");
            ageEditText.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) {
                showErrorToast("请输入有效的年龄（1-150岁）");
                ageEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showErrorToast("请输入有效的数字年龄");
            ageEditText.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            showErrorToast("联系方式不能为空，请输入您的手机号码");
            contactEditText.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            showErrorToast("家庭住址不能为空，请输入您的详细地址");
            addressEditText.requestFocus();
            return;
        }

        // 检查用户名是否已存在
        if (databaseHelper.isUsernameExists(username)) {
            showErrorToast("该用户名已被注册，请选择其他用户名");
            usernameEditText.requestFocus();
            return;
        }

        // 创建用户对象（默认权限为0-普通用户）
        User user = new User(username, password, gender, age, contact, address, 0);
        
        // 保存用户到数据库
        long result = databaseHelper.addUser(user);
        
        if (result != -1) {
            showSuccessDialog("注册成功", "恭喜您注册成功！将自动返回登录页面");
        } else {
            showErrorToast("注册失败，请稍后重试");
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getSelectedGender() {
        int selectedId = genderToggleGroup.getCheckedButtonId();
        if (selectedId == -1) {
            return null;
        }
        
        MaterialButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    private void showSuccessDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> {
                    // 注册成功后自动登录，返回登录界面并填充账号密码
                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("username", username);
                    resultIntent.putExtra("password", password);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .show();
    }

    private void navigateToLogin() {
        // 返回到登录界面
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}