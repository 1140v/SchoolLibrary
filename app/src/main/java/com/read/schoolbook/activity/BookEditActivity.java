package com.read.schoolbook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.dao.DatabaseHelper;
import com.read.schoolbook.model.Book;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookEditActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText isbnEditText;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText descriptionEditText;
    private EditText publisherEditText;
    private EditText publishDateEditText;
    private EditText locationEditText;
    private Spinner categorySpinner;
    private ImageView bookImageView;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton selectImageButton;
    private MaterialButton removeImageButton;
    private ImageView backButton;
    
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";
    
    private User currentUser;
    private Book currentBook;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);

        // 获取传递的用户信息和图书信息
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        currentBook = (Book) getIntent().getSerializableExtra("book");
        
        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(this);
        
        // 初始化视图
        initializeViews();
        
        // 加载类目数据
        loadCategories();
        
        // 设置点击监听器
        setupClickListeners();
        
        // 如果是编辑模式，填充数据
        if (currentBook != null) {
            fillBookData();
        }
    }

    private void initializeViews() {
        isbnEditText = findViewById(R.id.isbnEditText);
        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        publisherEditText = findViewById(R.id.publisherEditText);
        publishDateEditText = findViewById(R.id.publishDateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        bookImageView = findViewById(R.id.bookImageView);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        removeImageButton = findViewById(R.id.removeImageButton);
        backButton = findViewById(R.id.backButton);
    }

    private void loadCategories() {
        categoryList = databaseHelper.getAllCategories();
        
        String[] categoryNames = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categoryNames[i] = categoryList.get(i).getCategoryName();
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        // 设置默认选择项
        categorySpinner.setSelection(0);
    }

    private void setupClickListeners() {
        // 保存按钮
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });

        // 取消按钮
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 选择图片按钮
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        // 移除图片按钮
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedImage();
            }
        });
        
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillBookData() {
        isbnEditText.setText(currentBook.getIsbn());
        titleEditText.setText(currentBook.getTitle());
        authorEditText.setText(currentBook.getAuthor());
        descriptionEditText.setText(currentBook.getDescription());
        publisherEditText.setText(currentBook.getPublisher());
        publishDateEditText.setText(currentBook.getPublishDate());
        locationEditText.setText(currentBook.getLocation());
        
        // 设置类目选择
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getCategoryId() == currentBook.getCategoryId()) {
                categorySpinner.setSelection(i);
                break;
            }
        }
        
        // 设置图片
        if (currentBook.getImagePath() != null && !currentBook.getImagePath().isEmpty()) {
            selectedImagePath = currentBook.getImagePath();
            loadImageIntoView(selectedImagePath);
        }
    }



    private boolean validateInput() {
        String isbn = isbnEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String publisher = publisherEditText.getText().toString().trim();
        String publishDate = publishDateEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        
        if (isbn.isEmpty()) {
            isbnEditText.setError("请输入ISBN");
            return false;
        }
        
        if (title.isEmpty()) {
            titleEditText.setError("请输入书名");
            return false;
        }
        
        if (author.isEmpty()) {
            authorEditText.setError("请输入作者");
            return false;
        }
        
        if (publisher.isEmpty()) {
            publisherEditText.setError("请输入出版社");
            return false;
        }
        
        if (publishDate.isEmpty()) {
            publishDateEditText.setError("请输入出版日期");
            return false;
        }
        
        if (location.isEmpty()) {
            locationEditText.setError("请输入馆藏位置");
            return false;
        }
        
        return true;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 从相册选择图片
     */
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }

    /**
     * 移除选中的图片
     */
    private void removeSelectedImage() {
        selectedImagePath = "";
        bookImageView.setImageResource(R.drawable.ic_book_placeholder);
        Toast.makeText(this, "图片已移除", Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载图片到ImageView
     */
    private void loadImageIntoView(String imagePath) {
        if (imagePath.startsWith("content://")) {
            // 如果是Content URI，直接使用Glide加载
            Glide.with(this)
                    .load(Uri.parse(imagePath))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(bookImageView);
        } else if (imagePath.startsWith("file://")) {
            // 如果是文件URI，直接使用Glide加载
            Glide.with(this)
                    .load(Uri.parse(imagePath))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(bookImageView);
        } else if (!imagePath.isEmpty()) {
            // 如果是文件路径，使用Glide加载
            Glide.with(this)
                    .load(Uri.fromFile(new java.io.File(imagePath)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(bookImageView);
        } else {
            // 如果没有图片路径，显示占位符
            bookImageView.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    /**
     * 处理图片选择结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // 获取图片的永久访问权限
                try {
                    getContentResolver().takePersistableUriPermission(selectedImageUri, 
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (SecurityException e) {
                    // 如果无法获取永久权限，尝试复制图片到应用私有目录
                    selectedImagePath = copyImageToPrivateStorage(selectedImageUri);
                }
                
                if (selectedImagePath == null || selectedImagePath.isEmpty()) {
                    selectedImagePath = selectedImageUri.toString();
                }
                
                loadImageIntoView(selectedImagePath);
                Toast.makeText(this, "图片选择成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 复制图片到应用私有目录
     */
    private String copyImageToPrivateStorage(Uri imageUri) {
        try {
            // 创建应用私有目录中的图片文件
            File privateDir = new File(getFilesDir(), "book_images");
            if (!privateDir.exists()) {
                privateDir.mkdirs();
            }
            
            String fileName = "book_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(privateDir, fileName);
            
            // 复制图片
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存图书时处理图片路径
     */
    private void saveBook() {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        String isbn = isbnEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String publisher = publisherEditText.getText().toString().trim();
        String publishDate = publishDateEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        
        // 获取选中的类目ID
        int selectedPosition = categorySpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            Toast.makeText(this, "请选择有效的类目", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int categoryId = categoryList.get(selectedPosition).getCategoryId();
        
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPublisher(publisher);
        book.setPublishDate(publishDate);
        book.setLocation(location);
        book.setCategoryId(categoryId);
        book.setStatus(0); // 默认未借出
        book.setImagePath(selectedImagePath); // 设置图片路径
        
        if (currentBook == null) {
            // 添加新图书
            book.setCreateTime(getCurrentTime());
            long result = databaseHelper.addBook(book);
            if (result != -1) {
                Toast.makeText(this, "添加图书成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "添加图书失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 更新现有图书
            book.setBookId(currentBook.getBookId());
            book.setCreateTime(currentBook.getCreateTime());
            boolean success = databaseHelper.updateBook(book);
            if (success) {
                Toast.makeText(this, "更新图书成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "更新图书失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}