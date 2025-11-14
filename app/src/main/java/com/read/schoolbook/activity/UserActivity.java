package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

public class UserActivity extends AppCompatActivity {

    private TextView welcomeTitle;
    private TextView userInfoText;
    private TextView usernameText;
    private TextView genderText;
    private TextView ageText;
    private TextView contactText;
    private TextView addressText;
    private TextView welcomeText;
    
    // 阅读统计控件
    private TextView totalBorrowCountText;
    private TextView currentBorrowCountText;
    private TextView returnedCountText;
    private TextView overdueCountText;
    private TextView recentBooksText;
    
    // 我的界面控件
    private TextView usernameText2;
    private TextView genderText2;
    private TextView ageText2;
    private TextView contactText2;
    private TextView addressText2;
    
    // 界面布局
    private LinearLayout homeLayout, booksLayout, borrowLayout, profileLayout;
    private BottomNavigationView bottomNavigationView;
    
    private User currentUser;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置底部导航
        setupBottomNavigation();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 更新界面数据
        updateUI();
    }

    private void initializeViews() {
        // 初始化布局
        homeLayout = findViewById(R.id.homeLayout);
        booksLayout = findViewById(R.id.booksLayout);
        borrowLayout = findViewById(R.id.borrowLayout);
        profileLayout = findViewById(R.id.profileLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        
        // 首页控件
        welcomeTitle = findViewById(R.id.welcomeTitle);
        userInfoText = findViewById(R.id.userInfoText);
        usernameText = findViewById(R.id.usernameText);
        genderText = findViewById(R.id.genderText);
        ageText = findViewById(R.id.ageText);
        contactText = findViewById(R.id.contactText);
        addressText = findViewById(R.id.addressText);
        welcomeText = findViewById(R.id.welcomeText);
        
        // 阅读统计控件
        totalBorrowCountText = findViewById(R.id.totalBorrowCountText);
        currentBorrowCountText = findViewById(R.id.currentBorrowCountText);
        returnedCountText = findViewById(R.id.returnedCountText);
        overdueCountText = findViewById(R.id.overdueCountText);
        recentBooksText = findViewById(R.id.recentBooksText);
        
        // 我的界面控件
        usernameText2 = findViewById(R.id.usernameText2);
        genderText2 = findViewById(R.id.genderText2);
        ageText2 = findViewById(R.id.ageText2);
        contactText2 = findViewById(R.id.contactText2);
        addressText2 = findViewById(R.id.addressText2);
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showHomeLayout();
                return true;
            } else if (itemId == R.id.nav_books) {
                showBooksLayout();
                return true;
            } else if (itemId == R.id.nav_borrow) {
                showBorrowLayout();
                return true;
            } else if (itemId == R.id.nav_profile) {
                showProfileLayout();
                return true;
            }
            return false;
        });
        
        // 默认显示首页
        showHomeLayout();
    }
    
    private void showHomeLayout() {
        homeLayout.setVisibility(View.VISIBLE);
        booksLayout.setVisibility(View.GONE);
        borrowLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
    }
    
    private void showBooksLayout() {
        homeLayout.setVisibility(View.GONE);
        booksLayout.setVisibility(View.VISIBLE);
        borrowLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
    }
    
    private void showBorrowLayout() {
        homeLayout.setVisibility(View.GONE);
        booksLayout.setVisibility(View.GONE);
        borrowLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
    }
    
    private void showProfileLayout() {
        homeLayout.setVisibility(View.GONE);
        booksLayout.setVisibility(View.GONE);
        borrowLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
        
        // 更新我的界面信息
        updateProfileInfo();
    }

    private void setupClickListeners() {
        // 首页按钮 - 只保留查看详细借阅记录按钮
        findViewById(R.id.myBorrowRecordsButton).setOnClickListener(v -> viewMyBorrowRecords());
        
        // 图书界面按钮
        findViewById(R.id.viewBooksButton2).setOnClickListener(v -> viewBooks());
        findViewById(R.id.searchByCategoryButton2).setOnClickListener(v -> searchByCategory());
        
        // 借阅界面按钮
        findViewById(R.id.myBorrowRecordsButton2).setOnClickListener(v -> viewMyBorrowRecords());
        
        // 我的界面按钮
        findViewById(R.id.editProfileButton2).setOnClickListener(v -> editProfile());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> changePassword());
        findViewById(R.id.logoutButton).setOnClickListener(v -> logout());
    }

    private void updateUI() {
        if (currentUser != null) {
            // 更新首页信息
            welcomeTitle.setText("欢迎回来！" + currentUser.getUsername());
            userInfoText.setText("您好，尊敬的" + (currentUser.getGender().equals("男") ? "先生" : "女士"));
            
            usernameText.setText(currentUser.getUsername());
            genderText.setText(currentUser.getGender());
            ageText.setText(String.valueOf(currentUser.getAge()));
            contactText.setText(currentUser.getContact());
            addressText.setText(currentUser.getAddress());
            
            // 更新阅读统计信息
            updateReadingStatistics();
            
            // 更新顶部欢迎文本
            welcomeText.setText("欢迎，" + currentUser.getUsername());
        }
    }
    
    private void updateReadingStatistics() {
        if (currentUser != null) {
            // 获取用户阅读统计信息
            int totalBorrowCount = databaseHelper.getUserTotalBorrowCount(currentUser.getUsername());
            int currentBorrowCount = databaseHelper.getUserCurrentBorrowCount(currentUser.getUsername());
            int returnedCount = databaseHelper.getUserReturnedCount(currentUser.getUsername());
            int overdueCount = databaseHelper.getUserOverdueCount(currentUser.getUsername());
            String recentBooks = databaseHelper.getUserRecentBorrows(currentUser.getUsername());
            
            // 更新统计信息显示
            totalBorrowCountText.setText(String.valueOf(totalBorrowCount));
            currentBorrowCountText.setText(String.valueOf(currentBorrowCount));
            returnedCountText.setText(String.valueOf(returnedCount));
            overdueCountText.setText(String.valueOf(overdueCount));
            recentBooksText.setText(recentBooks.isEmpty() ? "暂无借阅记录" : recentBooks);
        }
    }
    
    private void updateProfileInfo() {
        if (currentUser != null) {
            // 更新我的界面信息
            usernameText2.setText(currentUser.getUsername());
            genderText2.setText(currentUser.getGender());
            ageText2.setText(String.valueOf(currentUser.getAge()));
            contactText2.setText(currentUser.getContact());
            addressText2.setText(currentUser.getAddress());
        }
    }
    
    private void viewBooks() {
        // 跳转到图书列表界面
        Intent intent = new Intent(UserActivity.this, BookListActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("isUserMode", true); // 标记为用户模式
        startActivity(intent);
    }
    
    private void viewMyBorrowRecords() {
        // 跳转到我的借阅记录界面
        Intent intent = new Intent(UserActivity.this, BorrowRecordListActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("selectedUser", currentUser); // 查看当前用户的记录
        startActivity(intent);
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
    
    private void searchByCategory() {
        // 跳转到按类目查找图书界面
        Intent intent = new Intent(UserActivity.this, CategorySearchActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
    
    private void editProfile() {
        // 跳转到编辑个人信息界面
        Intent intent = new Intent(UserActivity.this, EditProfileActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
    
    private void changePassword() {
        // 跳转到修改密码界面
        Intent intent = new Intent(UserActivity.this, ChangePasswordActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载用户信息（可能在编辑界面修改了信息）
        if (currentUser != null) {
            // 使用authenticateUser方法来重新获取用户信息
            User updatedUser = databaseHelper.authenticateUser(currentUser.getUsername(), currentUser.getPassword());
            if (updatedUser != null) {
                currentUser = updatedUser;
                updateUI();
                updateProfileInfo();
            }
        }
    }
}