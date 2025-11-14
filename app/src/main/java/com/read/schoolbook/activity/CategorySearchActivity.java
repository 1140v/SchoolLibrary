package com.read.schoolbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.adapter.BookAdapter;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Book;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorySearchActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private MaterialButton searchButton;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private DatabaseHelper databaseHelper;
    private User currentUser;
    
    private List<Book> allBooks;
    private List<Book> filteredBooks;
    private List<Category> allCategories;
    private Map<Integer, String> categoryMap; // 类别ID到类别名称的映射

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        
        // 初始化数据库助手
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 加载数据
        loadData();
    }

    private void initializeViews() {
        categorySpinner = findViewById(R.id.categorySpinner);
        searchButton = findViewById(R.id.searchButton);
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        
        // 设置RecyclerView
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(true); // 用户模式
        bookAdapter.setBookList(filteredBooks);
        booksRecyclerView.setAdapter(bookAdapter);
    }

    private void setupClickListeners() {
        // 返回按钮
        MaterialButton backButton = findViewById(R.id.backButton);
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
                searchBooksByCategory();
            }
        });
        
        // 图书点击事件
        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                // 跳转到图书详情页面
                Intent intent = new Intent(CategorySearchActivity.this, BookDetailActivity.class);
                intent.putExtra("book", book);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }

            @Override
            public void onEditClick(Book book) {
                // 用户模式下不需要编辑功能
            }

            @Override
            public void onDeleteClick(Book book) {
                // 用户模式下不需要删除功能
            }
        });
    }

    private void loadData() {
        // 获取所有图书
        allBooks = databaseHelper.getAllBooks();
        
        // 获取所有类别
        allCategories = databaseHelper.getAllCategories();
        
        // 创建类别映射
        categoryMap = new HashMap<>();
        for (Category category : allCategories) {
            categoryMap.put(category.getCategoryId(), category.getCategoryName());
        }
        
        // 设置类目下拉框
        setupCategorySpinner();
    }

    private void setupCategorySpinner() {
        // 创建类别名称列表
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("全部类目"); // 添加默认选项
        
        for (Category category : allCategories) {
            categoryNames.add(category.getCategoryName());
        }
        
        // 设置下拉框适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        // 默认选择"全部类目"
        categorySpinner.setSelection(0);
        
        // 显示所有图书
        filteredBooks.clear();
        filteredBooks.addAll(allBooks);
        bookAdapter.notifyDataSetChanged();
    }

    private void searchBooksByCategory() {
        String selectedCategoryName = categorySpinner.getSelectedItem().toString();
        
        filteredBooks.clear();
        
        if (selectedCategoryName.equals("全部类目")) {
            // 显示所有图书
            filteredBooks.addAll(allBooks);
        } else {
            // 找到对应的类别ID
            int selectedCategoryId = -1;
            for (Category category : allCategories) {
                if (category.getCategoryName().equals(selectedCategoryName)) {
                    selectedCategoryId = category.getCategoryId();
                    break;
                }
            }
            
            // 按类别ID筛选图书
            if (selectedCategoryId != -1) {
                for (Book book : allBooks) {
                    if (book.getCategoryId() == selectedCategoryId) {
                        filteredBooks.add(book);
                    }
                }
            }
        }
        
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}