package com.read.schoolbook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.BorrowRecord;
import com.read.schoolbook.model.User;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView welcomeText;
    private TextView userCountText;
    private TextView bookCountText;
    private TextView categoryCountText;
    private TextView borrowRecordCountText;
    private MaterialButton viewUsersButton;
    private MaterialButton manageBooksButton;
    private MaterialButton manageCategoriesButton;
    private MaterialButton viewBorrowRecordsButton;
    private MaterialButton manageBorrowConfigButton;
    private MaterialButton logoutButton;
    private MaterialButton editProfileButton; // 新增的编辑资料按钮
    
    // 底部导航相关
    private BottomNavigationView bottomNavigationView;
    private LinearLayout userManagementLayout;
    private LinearLayout bookManagementLayout;
    private LinearLayout categoryManagementLayout;
    private LinearLayout borrowManagementLayout;
    private LinearLayout profileManagementLayout; // 新增的"我的"界面布局
    
    private User currentUser;
    private TextView adminNameText;
    private TextView adminEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置底部导航监听器
        setupBottomNavigation();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 更新界面数据
        updateUI();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        userCountText = findViewById(R.id.userCountText);
        bookCountText = findViewById(R.id.bookCountText);
        categoryCountText = findViewById(R.id.categoryCountText);
        borrowRecordCountText = findViewById(R.id.borrowRecordCountText);
        viewUsersButton = findViewById(R.id.viewUsersButton);
        manageBooksButton = findViewById(R.id.manageBooksButton);
        manageCategoriesButton = findViewById(R.id.manageCategoriesButton);
        viewBorrowRecordsButton = findViewById(R.id.viewBorrowRecordsButton);
        manageBorrowConfigButton = findViewById(R.id.manageBorrowConfigButton);
        logoutButton = findViewById(R.id.logoutButton);
        editProfileButton = findViewById(R.id.editProfileButton); // 初始化编辑资料按钮
        
        // 底部导航相关视图
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userManagementLayout = findViewById(R.id.userManagementLayout);
        bookManagementLayout = findViewById(R.id.bookManagementLayout);
        categoryManagementLayout = findViewById(R.id.categoryManagementLayout);
        borrowManagementLayout = findViewById(R.id.borrowManagementLayout);
        profileManagementLayout = findViewById(R.id.profileManagementLayout); // 初始化"我的"界面布局
        
        // 初始化"我的"界面中的个人信息显示
        adminNameText = findViewById(R.id.adminNameText);
        adminEmailText = findViewById(R.id.adminEmailText);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // 隐藏所有布局
            userManagementLayout.setVisibility(View.GONE);
            bookManagementLayout.setVisibility(View.GONE);
            categoryManagementLayout.setVisibility(View.GONE);
            borrowManagementLayout.setVisibility(View.GONE);
            profileManagementLayout.setVisibility(View.GONE); // 隐藏"我的"界面
            
            // 根据选中的菜单项显示对应的布局
            if (item.getItemId() == R.id.nav_users) {
                userManagementLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (item.getItemId() == R.id.nav_books) {
                bookManagementLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (item.getItemId() == R.id.nav_categories) {
                categoryManagementLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (item.getItemId() == R.id.nav_borrow) {
                borrowManagementLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) { // 处理"我的"菜单项
                profileManagementLayout.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
        
        // 默认选中用户管理
        bottomNavigationView.setSelectedItemId(R.id.nav_users);
    }

    private void setupClickListeners() {
        // 查看所有用户按钮
        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到用户管理界面
                Intent intent = new Intent(AdminActivity.this, UserListActivity.class);
                intent.putExtra("currentAdmin", currentUser);
                startActivity(intent);
            }
        });

        // 图书管理按钮
        manageBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到图书管理界面
                Intent intent = new Intent(AdminActivity.this, BookListActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        // 类目管理按钮
        manageCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到类目管理界面
                Intent intent = new Intent(AdminActivity.this, CategoryListActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        // 查看借阅记录按钮
        viewBorrowRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到借阅记录管理界面
                Intent intent = new Intent(AdminActivity.this, BorrowRecordListActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        // 借阅规则配置按钮
        manageBorrowConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到借阅配置管理界面
                Intent intent = new Intent(AdminActivity.this, BorrowConfigActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        // 退出登录按钮
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        
        // 编辑资料按钮
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void updateUI() {
        if (currentUser != null) {
            welcomeText.setText("欢迎，" + currentUser.getUsername());
            
            // 更新"我的"界面中的管理员信息
            adminNameText.setText("管理员：" + currentUser.getUsername());
            adminEmailText.setText("联系方式：" + currentUser.getContact());
        }
        
        // 获取用户总数
        List<User> userList = databaseHelper.getAllUsers();
        userCountText.setText("总用户数：" + userList.size());
        
        // 获取图书总数
        List<com.read.schoolbook.model.Book> bookList = databaseHelper.getAllBooks();
        bookCountText.setText("总图书数：" + bookList.size());
        
        // 获取类目总数
        List<com.read.schoolbook.model.Category> categoryList = databaseHelper.getAllCategories();
        categoryCountText.setText("总类目数：" + categoryList.size());
        
        // 获取借阅记录总数
        List<BorrowRecord> borrowRecordList = databaseHelper.getAllBorrowRecords();
        borrowRecordCountText.setText("总借阅记录：" + borrowRecordList.size());
    }

    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "用户信息异常", Toast.LENGTH_SHORT).show();
            return;
        }
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_edit, null);
        
        TextInputEditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        TextInputEditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        android.widget.RadioGroup genderRadioGroup = dialogView.findViewById(R.id.genderRadioGroup);
        android.widget.RadioButton maleRadioButton = dialogView.findViewById(R.id.maleRadioButton);
        android.widget.RadioButton femaleRadioButton = dialogView.findViewById(R.id.femaleRadioButton);
        TextInputEditText ageEditText = dialogView.findViewById(R.id.ageEditText);
        TextInputEditText contactEditText = dialogView.findViewById(R.id.contactEditText);
        TextInputEditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        
        // 填充当前管理员信息
        usernameEditText.setText(currentUser.getUsername());
        passwordEditText.setText(currentUser.getPassword());
        
        // 设置性别选择
        if ("男".equals(currentUser.getGender())) {
            maleRadioButton.setChecked(true);
        } else {
            femaleRadioButton.setChecked(true);
        }
        
        ageEditText.setText(String.valueOf(currentUser.getAge()));
        contactEditText.setText(currentUser.getContact());
        addressEditText.setText(currentUser.getAddress());
        
        // 管理员不能修改用户名
        usernameEditText.setEnabled(false);
        usernameEditText.setHint("管理员用户名不可修改");
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("编辑个人资料")
                .setView(dialogView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String username = usernameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        
                        // 获取选中的性别
                        String gender = "男";
                        if (femaleRadioButton.isChecked()) {
                            gender = "女";
                        }
                        
                        String ageStr = ageEditText.getText().toString().trim();
                        String contact = contactEditText.getText().toString().trim();
                        String address = addressEditText.getText().toString().trim();
                        
                        if (validateInput(username, password, gender, ageStr, contact, address)) {
                            int age = Integer.parseInt(ageStr);
                            User updatedUser = new User(username, password, gender, age, contact, address, currentUser.isAdmin() ? 1 : 0);
                            updatedUser.setId(currentUser.getId());
                            
                            boolean result = databaseHelper.updateUser(updatedUser);
                            if (result) {
                                Toast.makeText(AdminActivity.this, "个人信息更新成功", Toast.LENGTH_SHORT).show();
                                // 更新当前用户信息
                                currentUser = updatedUser;
                                updateUI();
                            } else {
                                Toast.makeText(AdminActivity.this, "个人信息更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        
        dialog.show();
    }

    private boolean validateInput(String username, String password, String gender, String ageStr, String contact, String address) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "请输入性别", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "请输入年龄", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) {
                Toast.makeText(this, "请输入有效的年龄", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "年龄必须是数字", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "请输入联系方式", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入住址", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void logout() {
        // 清除当前用户信息
        currentUser = null;
        
        // 返回到登录界面
        Intent intent = new Intent();
        intent.setClassName(this, "com.read.schoolbook.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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