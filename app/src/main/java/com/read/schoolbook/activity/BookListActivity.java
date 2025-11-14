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
import com.read.schoolbook.adapter.BookAdapter;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Book;
import com.read.schoolbook.model.User;

import java.util.List;

public class BookListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private TextView bookCountText;
    private MaterialButton addBookButton;
    private ImageView backButton;
    private EditText searchEditText;
    private MaterialButton searchButton;
    
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // 获取传递的用户信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 加载图书数据
        loadBooks();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.bookRecyclerView);
        bookCountText = findViewById(R.id.bookCountText);
        addBookButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.backButton);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        
        // 用户端隐藏添加图书按钮（只有管理员有权限）
        if (currentUser != null && currentUser.isAdmin()) {
            addBookButton.setVisibility(View.VISIBLE);
        } else {
            addBookButton.setVisibility(View.GONE);
        }
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 根据用户角色设置适配器模式
        boolean isUserMode = currentUser != null && !currentUser.isAdmin();
        bookAdapter = new BookAdapter(isUserMode);
        recyclerView.setAdapter(bookAdapter);
        
        // 设置图书适配器的点击监听器
        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                // 查看图书详情
                viewBookDetail(book);
            }
            
            @Override
            public void onEditClick(Book book) {
                // 只有管理员可以编辑图书
                if (currentUser != null && currentUser.isAdmin()) {
                    editBook(book);
                }
            }
            
            @Override
            public void onDeleteClick(Book book) {
                // 只有管理员可以删除图书
                if (currentUser != null && currentUser.isAdmin()) {
                    deleteBook(book);
                }
            }
        });
    }

    private void setupClickListeners() {
        // 添加图书按钮
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加图书界面
                Intent intent = new Intent(BookListActivity.this, BookEditActivity.class);
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
                searchBooks();
            }
        });
        
        // 搜索输入框的搜索动作
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                searchBooks();
                return true;
            }
            return false;
        });
    }

    private void loadBooks() {
        List<Book> bookList = databaseHelper.getAllBooks();
        bookAdapter.setBookList(bookList);
        bookCountText.setText("总图书数：" + bookList.size());
    }
    
    private void searchBooks() {
        String keyword = searchEditText.getText().toString().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        
        List<Book> searchResults = databaseHelper.searchBooks(keyword);
        bookAdapter.setBookList(searchResults);
        bookCountText.setText("搜索结果：" + searchResults.size() + " 本图书");
    }

    private void viewBookDetail(Book book) {
        // 跳转到图书详情页面
        Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    private void editBook(Book book) {
        // 跳转到编辑图书界面
        Intent intent = new Intent(BookListActivity.this, BookEditActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    private void deleteBook(Book book) {
        boolean success = databaseHelper.deleteBook(book.getBookId());
        if (success) {
            Toast.makeText(this, "删除图书成功", Toast.LENGTH_SHORT).show();
            loadBooks(); // 重新加载数据
        } else {
            Toast.makeText(this, "删除图书失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当从添加/编辑界面返回时，重新加载数据
        loadBooks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}