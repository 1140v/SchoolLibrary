package com.read.schoolbook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.read.schoolbook.R;
import com.read.schoolbook.adapter.UserAdapter;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private DatabaseHelper databaseHelper;
    private RecyclerView userRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextInputEditText searchEditText;
    private MaterialButton searchButton;
    private MaterialButton addUserButton;
    private ImageView backButton;
    
    private UserAdapter userAdapter;
    private List<User> originalUserList;
    private List<User> filteredUserList;
    
    private User currentAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // 获取传递的管理员信息
        currentAdmin = (User) getIntent().getSerializableExtra("currentAdmin");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 初始化用户列表
        initializeUserList();
    }

    private void initializeViews() {
        userRecyclerView = findViewById(R.id.userRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        addUserButton = findViewById(R.id.addUserButton);
        backButton = findViewById(R.id.backButton);
        
        // 根据当前用户权限控制界面显示
        if (currentAdmin == null || !currentAdmin.isAdmin()) {
            // 普通用户：隐藏添加用户按钮
            addUserButton.setVisibility(View.GONE);
        }
        
        // 设置RecyclerView
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        // 返回按钮
        backButton.setOnClickListener(v -> finish());
        
        // 搜索按钮
        searchButton.setOnClickListener(v -> performSearch());
        
        // 添加用户按钮
        addUserButton.setOnClickListener(v -> showAddUserDialog());
        
        // 搜索框回车键监听
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void initializeUserList() {
        // 获取所有用户
        originalUserList = databaseHelper.getAllUsers();
        filteredUserList = new ArrayList<>(originalUserList);
        
        // 创建适配器，传递当前用户信息
        userAdapter = new UserAdapter(filteredUserList, this, currentAdmin);
        userRecyclerView.setAdapter(userAdapter);
        
        // 更新空状态显示
        updateEmptyState();
    }

    private void performSearch() {
        String searchText = searchEditText.getText().toString().trim();
        
        if (TextUtils.isEmpty(searchText)) {
            // 如果搜索文本为空，显示所有用户
            filteredUserList.clear();
            filteredUserList.addAll(originalUserList);
        } else {
            // 根据用户名搜索
            filteredUserList.clear();
            for (User user : originalUserList) {
                if (user.getUsername().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredUserList.add(user);
                }
            }
        }
        
        userAdapter.updateData(filteredUserList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredUserList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            userRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            userRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditUser(User user) {
        showEditUserDialog(user);
    }

    @Override
    public void onDeleteUser(User user) {
        showDeleteConfirmationDialog(user);
    }

    private void showAddUserDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_edit, null);
        
        TextInputEditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        TextInputEditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        RadioGroup genderRadioGroup = dialogView.findViewById(R.id.genderRadioGroup);
        RadioButton maleRadioButton = dialogView.findViewById(R.id.maleRadioButton);
        RadioButton femaleRadioButton = dialogView.findViewById(R.id.femaleRadioButton);
        TextInputEditText ageEditText = dialogView.findViewById(R.id.ageEditText);
        TextInputEditText contactEditText = dialogView.findViewById(R.id.contactEditText);
        TextInputEditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        
        // 默认选择男性
        maleRadioButton.setChecked(true);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("添加用户")
                .setView(dialogView)
                .setPositiveButton("添加", (dialogInterface, which) -> {
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
                        User newUser = new User(username, password, gender, age, contact, address, 0);
                        
                        if (databaseHelper.isUsernameExists(username)) {
                            showMessage("用户名已存在");
                            return;
                        }
                        
                        long result = databaseHelper.addUser(newUser);
                        if (result != -1) {
                            showMessage("用户添加成功");
                            refreshUserList();
                        } else {
                            showMessage("用户添加失败");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        
        dialog.show();
    }

    private void showEditUserDialog(User user) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_edit, null);
        
        TextInputEditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        TextInputEditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        RadioGroup genderRadioGroup = dialogView.findViewById(R.id.genderRadioGroup);
        RadioButton maleRadioButton = dialogView.findViewById(R.id.maleRadioButton);
        RadioButton femaleRadioButton = dialogView.findViewById(R.id.femaleRadioButton);
        TextInputEditText ageEditText = dialogView.findViewById(R.id.ageEditText);
        TextInputEditText contactEditText = dialogView.findViewById(R.id.contactEditText);
        TextInputEditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        
        // 填充现有用户数据
        usernameEditText.setText(user.getUsername());
        passwordEditText.setText(user.getPassword());
        
        // 设置性别选择
        if ("男".equals(user.getGender())) {
            maleRadioButton.setChecked(true);
        } else {
            femaleRadioButton.setChecked(true);
        }
        
        ageEditText.setText(String.valueOf(user.getAge()));
        contactEditText.setText(user.getContact());
        addressEditText.setText(user.getAddress());
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("编辑用户")
                .setView(dialogView)
                .setPositiveButton("保存", (dialogInterface, which) -> {
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
                        User updatedUser = new User(username, password, gender, age, contact, address, user.isAdmin() ? 1 : 0);
                        updatedUser.setId(user.getId());
                        
                        // 检查用户名是否已存在（排除当前用户）
                        if (!username.equals(user.getUsername()) && databaseHelper.isUsernameExists(username)) {
                            showMessage("用户名已存在");
                            return;
                        }
                        
                        boolean result = databaseHelper.updateUser(updatedUser);
                        if (result) {
                            showMessage("用户信息更新成功");
                            refreshUserList();
                        } else {
                            showMessage("用户信息更新失败");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        
        dialog.show();
    }

    private void showDeleteConfirmationDialog(User user) {
        // 不能删除当前登录的管理员
        if (currentAdmin != null && user.getId() == currentAdmin.getId()) {
            showMessage("不能删除当前登录的管理员账户");
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除用户 " + user.getUsername() + " 吗？此操作不可恢复。")
                .setPositiveButton("删除", (dialog, which) -> {
                    boolean result = databaseHelper.deleteUser(user.getId());
                    if (result) {
                        showMessage("用户删除成功");
                        refreshUserList();
                    } else {
                        showMessage("用户删除失败");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private boolean validateInput(String username, String password, String gender, String ageStr, String contact, String address) {
        if (TextUtils.isEmpty(username)) {
            showMessage("请输入用户名");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showMessage("请输入密码");
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            showMessage("请输入性别");
            return false;
        }
        if (TextUtils.isEmpty(ageStr)) {
            showMessage("请输入年龄");
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) {
                showMessage("请输入有效的年龄");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("年龄必须是数字");
            return false;
        }
        if (TextUtils.isEmpty(contact)) {
            showMessage("请输入联系方式");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            showMessage("请输入住址");
            return false;
        }
        return true;
    }

    private void refreshUserList() {
        originalUserList = databaseHelper.getAllUsers();
        performSearch(); // 重新执行搜索以更新显示
    }

    private void showMessage(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}