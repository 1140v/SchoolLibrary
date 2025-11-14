package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.adapter.CategoryAdapter;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private TextView categoryCountText;
    private MaterialButton addCategoryButton;
    private ImageView backButton;
    private EditText searchEditText;
    private MaterialButton searchButton;
    
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 加载类目数据
        loadCategories();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.categoryRecyclerView);
        categoryCountText = findViewById(R.id.categoryCountText);
        addCategoryButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.backButton);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter();
        recyclerView.setAdapter(categoryAdapter);
        
        // 设置类目适配器的点击监听器
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Category category) {
                // 编辑类目
                editCategory(category);
            }
            
            @Override
            public void onDeleteClick(Category category) {
                // 删除类目
                deleteCategory(category);
            }
        });
    }

    private void setupClickListeners() {
        // 添加类目按钮
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加类目界面
                Intent intent = new Intent(CategoryListActivity.this, CategoryEditActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

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
                searchCategories();
            }
        });
        
        // 搜索输入框的搜索动作
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                searchCategories();
                return true;
            }
            return false;
        });
    }

    private void loadCategories() {
        List<Category> categoryList = databaseHelper.getAllCategories();
        categoryAdapter.setCategoryList(categoryList);
        categoryCountText.setText("总类目数：" + categoryList.size());
    }
    
    private void searchCategories() {
        String keyword = searchEditText.getText().toString().trim();
        if (keyword.isEmpty()) {
            loadCategories();
            return;
        }
        
        List<Category> searchResults = databaseHelper.searchCategories(keyword);
        categoryAdapter.setCategoryList(searchResults);
        categoryCountText.setText("搜索结果：" + searchResults.size() + " 个类目");
    }

    private void editCategory(Category category) {
        // 跳转到编辑类目界面
        Intent intent = new Intent(CategoryListActivity.this, CategoryEditActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void deleteCategory(Category category) {
        boolean success = databaseHelper.deleteCategory(category.getCategoryId());
        if (success) {
            Toast.makeText(this, "删除类目成功", Toast.LENGTH_SHORT).show();
            loadCategories(); // 重新加载数据
        } else {
            Toast.makeText(this, "删除类目失败，该类目下还有图书", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当从添加/编辑界面返回时，重新加载数据
        loadCategories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}