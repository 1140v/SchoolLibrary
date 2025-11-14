package com.read.schoolbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.read.schoolbook.activity.AdminActivity;
import com.read.schoolbook.activity.RegisterActivity;
import com.read.schoolbook.activity.UserActivity;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private TextView registerTextView;
    private DatabaseHelper databaseHelper;

    // 注册Activity的结果启动器
    private ActivityResultLauncher<Intent> registerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // 获取注册返回的用户名和密码
                            String username = data.getStringExtra("username");
                            String password = data.getStringExtra("password");
                            
                            // 自动填充登录信息
                            usernameEditText.setText(username);
                            passwordEditText.setText(password);
                            
                            Toast.makeText(MainActivity.this, "注册成功，已自动填充登录信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
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
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
    }

    private void setupClickListeners() {
        // 登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // 注册链接点击事件
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        // 验证用户登录
        User user = databaseHelper.authenticateUser(username, password);
        
        if (user != null) {
            // 登录成功，根据权限跳转到不同界面
            navigateToUserInterface(user);
        } else {
            showErrorToast("账号或密码错误，请重新输入");
            passwordEditText.setText("");
            passwordEditText.requestFocus();
        }
    }

    private void navigateToUserInterface(User user) {
        Intent intent;
        
        if (user.isAdmin()) {
            // 跳转到管理员界面
            intent = new Intent(this, AdminActivity.class);
            Toast.makeText(this, "欢迎管理员 " + user.getUsername(), Toast.LENGTH_SHORT).show();
        } else {
            // 跳转到用户界面
            intent = new Intent(this, UserActivity.class);
            Toast.makeText(this, "登录成功，欢迎 " + user.getUsername(), Toast.LENGTH_SHORT).show();
        }
        
        // 传递当前用户信息
        intent.putExtra("currentUser", user);
        startActivity(intent);
        
        // 清空输入框
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccessDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> {
                    // 清空输入框
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                })
                .show();
    }

    private void navigateToRegister() {
        // 跳转到注册界面
        Intent intent = new Intent(this, RegisterActivity.class);
        registerLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}