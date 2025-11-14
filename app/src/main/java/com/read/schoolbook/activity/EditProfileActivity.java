package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, ageEditText, contactEditText, addressEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private MaterialButton saveButton, backButton;
    
    private User currentUser;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 填充用户信息
        populateUserInfo();
    }

    private void initializeViews() {
        // 初始化输入框
        usernameEditText = findViewById(R.id.usernameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        contactEditText = findViewById(R.id.contactEditText);
        addressEditText = findViewById(R.id.addressEditText);
        
        // 初始化性别单选框
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        
        // 初始化按钮
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // 返回按钮点击事件
        backButton.setOnClickListener(v -> finish());
        
        // 保存按钮点击事件
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void populateUserInfo() {
        if (currentUser != null) {
            usernameEditText.setText(currentUser.getUsername());
            ageEditText.setText(String.valueOf(currentUser.getAge()));
            contactEditText.setText(currentUser.getContact());
            addressEditText.setText(currentUser.getAddress());
            
            // 设置性别选择
            if ("男".equals(currentUser.getGender())) {
                maleRadioButton.setChecked(true);
            } else if ("女".equals(currentUser.getGender())) {
                femaleRadioButton.setChecked(true);
            }
        }
    }

    private void saveProfile() {
        // 获取输入的值
        String username = usernameEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        
        // 获取性别选择
        String gender = "";
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.maleRadioButton) {
            gender = "男";
        } else if (selectedId == R.id.femaleRadioButton) {
            gender = "女";
        }
        
        // 验证输入
        if (TextUtils.isEmpty(username)) {
            showError("用户名不能为空");
            return;
        }
        
        if (TextUtils.isEmpty(gender)) {
            showError("请选择性别");
            return;
        }
        
        if (TextUtils.isEmpty(ageStr)) {
            showError("年龄不能为空");
            return;
        }
        
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                showError("年龄必须在1-120之间");
                return;
            }
        } catch (NumberFormatException e) {
            showError("年龄必须是有效数字");
            return;
        }
        
        if (TextUtils.isEmpty(contact)) {
            showError("联系方式不能为空");
            return;
        }
        
        if (TextUtils.isEmpty(address)) {
            showError("地址不能为空");
            return;
        }
        
        // 检查用户名是否已存在（如果修改了用户名）
        if (!username.equals(currentUser.getUsername())) {
            if (databaseHelper.isUsernameExists(username)) {
                showError("用户名已存在，请选择其他用户名");
                return;
            }
        }
        
        // 更新用户信息 - 使用现有的updateUser(User user)方法
        currentUser.setUsername(username);
        currentUser.setGender(gender);
        currentUser.setAge(age);
        currentUser.setContact(contact);
        currentUser.setAddress(address);
        
        boolean success = databaseHelper.updateUser(currentUser);
        
        if (success) {
            Toast.makeText(this, "个人信息更新成功", Toast.LENGTH_SHORT).show();
            
            // 创建返回结果
            Intent resultIntent = new Intent();
            
            // 使用authenticateUser方法重新获取更新后的用户信息
            User updatedUser = databaseHelper.authenticateUser(username, currentUser.getPassword());
            if (updatedUser != null) {
                resultIntent.putExtra("updatedUser", updatedUser);
            }
            
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            showError("更新失败，请重试");
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}