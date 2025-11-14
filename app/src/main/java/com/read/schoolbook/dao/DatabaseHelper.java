package com.read.schoolbook.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.read.schoolbook.model.Book;
import com.read.schoolbook.model.BorrowConfig;
import com.read.schoolbook.model.BorrowRecord;
import com.read.schoolbook.model.Category;
import com.read.schoolbook.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    // 数据库信息
    private static final String DATABASE_NAME = "schoolbook.db";
    private static final int DATABASE_VERSION = 3;
    
    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_CONTACT = "contact";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PERMISSION = "permission";
    
    // 创建用户表的SQL语句
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_GENDER + " TEXT NOT NULL,"
            + COLUMN_AGE + " INTEGER NOT NULL,"
            + COLUMN_CONTACT + " TEXT NOT NULL,"
            + COLUMN_ADDRESS + " TEXT NOT NULL,"
            + COLUMN_PERMISSION + " INTEGER DEFAULT 0"
            + ")";

    // 类目表
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";

    // 创建类目表的SQL语句
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT UNIQUE NOT NULL"
            + ")";

    // 图书表
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_ISBN = "isbn";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PUBLISHER = "publisher";
    private static final String COLUMN_PUBLISH_DATE = "publish_date";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATE_TIME = "create_time";

    // 创建图书表的SQL语句
    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + TABLE_BOOKS + "("
            + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ISBN + " TEXT UNIQUE NOT NULL,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_AUTHOR + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_PUBLISHER + " TEXT NOT NULL,"
            + COLUMN_PUBLISH_DATE + " TEXT NOT NULL,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_LOCATION + " TEXT NOT NULL,"
            + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
            + COLUMN_STATUS + " INTEGER DEFAULT 0,"
            + COLUMN_CREATE_TIME + " TEXT NOT NULL,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ") ON DELETE RESTRICT"
            + ")";

    // 借阅记录表
    private static final String TABLE_BORROW_RECORDS = "borrow_records";
    private static final String COLUMN_RECORD_ID = "record_id";
    private static final String COLUMN_BORROW_TIME = "borrow_time";
    private static final String COLUMN_RETURN_TIME = "return_time";
    private static final String COLUMN_OVERDUE_STATUS = "overdue_status";
    private static final String COLUMN_OVERDUE_DAYS = "overdue_days";

    // 创建借阅记录表的SQL语句
    private static final String CREATE_TABLE_BORROW_RECORDS = "CREATE TABLE " + TABLE_BORROW_RECORDS + "("
            + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BOOK_ID + " INTEGER NOT NULL,"
            + COLUMN_ISBN + " TEXT NOT NULL,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_AUTHOR + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_PUBLISHER + " TEXT NOT NULL,"
            + COLUMN_PUBLISH_DATE + " TEXT NOT NULL,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_LOCATION + " TEXT NOT NULL,"
            + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
            + COLUMN_STATUS + " INTEGER DEFAULT 0,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_BORROW_TIME + " TEXT NOT NULL,"
            + COLUMN_RETURN_TIME + " TEXT,"
            + COLUMN_OVERDUE_STATUS + " TEXT DEFAULT '正常',"
            + COLUMN_OVERDUE_DAYS + " INTEGER DEFAULT 0,"
            + "FOREIGN KEY(" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ") ON DELETE RESTRICT"
            + ")";

    // 借阅配置表
    private static final String TABLE_BORROW_CONFIG = "borrow_config";
    private static final String COLUMN_CONFIG_ID = "config_id";
    private static final String COLUMN_MAX_BORROW_DAYS = "max_borrow_days";
    private static final String COLUMN_MAX_BORROW_COUNT = "max_borrow_count";
    private static final String COLUMN_UPDATE_TIME = "update_time";

    // 创建借阅配置表的SQL语句
    private static final String CREATE_TABLE_BORROW_CONFIG = "CREATE TABLE " + TABLE_BORROW_CONFIG + "("
            + COLUMN_CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MAX_BORROW_DAYS + " INTEGER DEFAULT 30,"
            + COLUMN_MAX_BORROW_COUNT + " INTEGER DEFAULT 5,"
            + COLUMN_UPDATE_TIME + " TEXT NOT NULL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        db.execSQL(CREATE_TABLE_USERS);
        
        // 创建类目表
        db.execSQL(CREATE_TABLE_CATEGORIES);
        
        // 创建图书表
        db.execSQL(CREATE_TABLE_BOOKS);
        
        // 创建借阅记录表
        db.execSQL(CREATE_TABLE_BORROW_RECORDS);
        
        // 创建借阅配置表
        db.execSQL(CREATE_TABLE_BORROW_CONFIG);
        
        // 插入默认管理员账户
        insertDefaultAdmin(db);
        
        // 插入默认类目数据
        insertDefaultCategories(db);
        
        // 插入默认图书数据
        insertDefaultBooks(db);
        
        // 插入默认借阅配置
        insertDefaultBorrowConfig(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // 创建新表
            db.execSQL(CREATE_TABLE_BORROW_RECORDS);
            db.execSQL(CREATE_TABLE_BORROW_CONFIG);
            
            // 插入默认借阅配置
            insertDefaultBorrowConfig(db);
        }
    }

    // 插入默认管理员账户和默认用户账户
    private void insertDefaultAdmin(SQLiteDatabase db) {
        // 插入管理员账户：admin/123456
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "123456");
        adminValues.put(COLUMN_GENDER, "男");
        adminValues.put(COLUMN_AGE, 30);
        adminValues.put(COLUMN_CONTACT, "13800138000");
        adminValues.put(COLUMN_ADDRESS, "管理员地址");
        adminValues.put(COLUMN_PERMISSION, 1); // 管理员权限
        
        db.insert(TABLE_USERS, null, adminValues);
        
        // 插入默认用户账户：user/123456
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, "user");
        userValues.put(COLUMN_PASSWORD, "123456");
        userValues.put(COLUMN_GENDER, "女");
        userValues.put(COLUMN_AGE, 25);
        userValues.put(COLUMN_CONTACT, "13900139000");
        userValues.put(COLUMN_ADDRESS, "用户地址");
        userValues.put(COLUMN_PERMISSION, 0); // 普通用户权限
        
        db.insert(TABLE_USERS, null, userValues);
    }

    // 添加用户
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_AGE, user.getAge());
        values.put(COLUMN_CONTACT, user.getContact());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_PERMISSION, user.getPermission());
        
        // 插入新用户
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // 验证用户登录
    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_ID, 
                COLUMN_USERNAME, 
                COLUMN_PASSWORD, 
                COLUMN_GENDER, 
                COLUMN_AGE, 
                COLUMN_CONTACT, 
                COLUMN_ADDRESS, 
                COLUMN_PERMISSION
        };
        
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
            user.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
            user.setPermission(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERMISSION)));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return user;
    }

    // 检查用户名是否已存在
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        boolean exists = cursor != null && cursor.getCount() > 0;
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_ID, 
                COLUMN_USERNAME, 
                COLUMN_PASSWORD, 
                COLUMN_GENDER, 
                COLUMN_AGE, 
                COLUMN_CONTACT, 
                COLUMN_ADDRESS, 
                COLUMN_PERMISSION
        };
        
        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                user.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                user.setPermission(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERMISSION)));
                
                userList.add(user);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return userList;
    }

    // 根据ID获取用户
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_ID, 
                COLUMN_USERNAME, 
                COLUMN_PASSWORD, 
                COLUMN_GENDER, 
                COLUMN_AGE, 
                COLUMN_CONTACT, 
                COLUMN_ADDRESS, 
                COLUMN_PERMISSION
        };
        
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
            user.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
            user.setPermission(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERMISSION)));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return user;
    }

    // 删除用户
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    // 更新用户信息
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_AGE, user.getAge());
        values.put(COLUMN_CONTACT, user.getContact());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_PERMISSION, user.getPermission());
        
        int result = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", 
                new String[]{String.valueOf(user.getId())});
        db.close();
        return result > 0;
    }

    // 插入默认类目数据
    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] categories = {"文学", "科技", "历史", "哲学", "艺术", "教育", "经济", "医学"};
        
        for (String categoryName : categories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, categoryName);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // 插入默认图书数据
    private void insertDefaultBooks(SQLiteDatabase db) {
        // 获取第一个类目的ID
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CATEGORY_ID}, 
                null, null, null, null, COLUMN_CATEGORY_ID + " ASC", "1");
        int firstCategoryId = 1;
        if (cursor != null && cursor.moveToFirst()) {
            firstCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
            cursor.close();
        }

        // 插入一些示例图书
        ContentValues book1 = new ContentValues();
        book1.put(COLUMN_ISBN, "9787101003048");
        book1.put(COLUMN_TITLE, "红楼梦");
        book1.put(COLUMN_AUTHOR, "曹雪芹");
        book1.put(COLUMN_DESCRIPTION, "中国古典四大名著之一");
        book1.put(COLUMN_PUBLISHER, "人民文学出版社");
        book1.put(COLUMN_PUBLISH_DATE, "1791-01-01");
        book1.put(COLUMN_IMAGE_PATH, "");
        book1.put(COLUMN_LOCATION, "A区-文学-001");
        book1.put(COLUMN_CATEGORY_ID, firstCategoryId);
        book1.put(COLUMN_STATUS, 0);
        book1.put(COLUMN_CREATE_TIME, "2024-01-01 10:00:00");
        db.insert(TABLE_BOOKS, null, book1);

        ContentValues book2 = new ContentValues();
        book2.put(COLUMN_ISBN, "9787506365437");
        book2.put(COLUMN_TITLE, "活着");
        book2.put(COLUMN_AUTHOR, "余华");
        book2.put(COLUMN_DESCRIPTION, "讲述一个人和他的命运之间的友情");
        book2.put(COLUMN_PUBLISHER, "作家出版社");
        book2.put(COLUMN_PUBLISH_DATE, "1993-01-01");
        book2.put(COLUMN_IMAGE_PATH, "");
        book2.put(COLUMN_LOCATION, "A区-文学-002");
        book2.put(COLUMN_CATEGORY_ID, firstCategoryId);
        book2.put(COLUMN_STATUS, 0);
        book2.put(COLUMN_CREATE_TIME, "2024-01-01 10:00:00");
        db.insert(TABLE_BOOKS, null, book2);
    }

    // 插入默认借阅配置
    private void insertDefaultBorrowConfig(SQLiteDatabase db) {
        ContentValues config = new ContentValues();
        config.put(COLUMN_MAX_BORROW_DAYS, 30);
        config.put(COLUMN_MAX_BORROW_COUNT, 5);
        config.put(COLUMN_UPDATE_TIME, "2024-01-01 10:00:00");
        db.insert(TABLE_BORROW_CONFIG, null, config);
    }

    // 图书管理相关方法
    public long addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DESCRIPTION, book.getDescription());
        values.put(COLUMN_PUBLISHER, book.getPublisher());
        values.put(COLUMN_PUBLISH_DATE, book.getPublishDate());
        values.put(COLUMN_IMAGE_PATH, book.getImagePath());
        values.put(COLUMN_LOCATION, book.getLocation());
        values.put(COLUMN_CATEGORY_ID, book.getCategoryId());
        values.put(COLUMN_STATUS, book.getStatus());
        values.put(COLUMN_CREATE_TIME, book.getCreateTime());
        
        long id = db.insert(TABLE_BOOKS, null, values);
        db.close();
        return id;
    }

    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_BOOKS + " ORDER BY " + COLUMN_CREATE_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setBookId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
                book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                book.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)));
                book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
                book.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                book.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                book.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                book.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
                
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return bookList;
    }

    public Book getBookById(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_BOOK_ID + " = ?";
        String[] selectionArgs = {String.valueOf(bookId)};
        
        Cursor cursor = db.query(TABLE_BOOKS, null, selection, selectionArgs, null, null, null);
        
        Book book = null;
        if (cursor != null && cursor.moveToFirst()) {
            book = new Book();
            book.setBookId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
            book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
            book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
            book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            book.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)));
            book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
            book.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
            book.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
            book.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            book.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return book;
    }

    public boolean updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DESCRIPTION, book.getDescription());
        values.put(COLUMN_PUBLISHER, book.getPublisher());
        values.put(COLUMN_PUBLISH_DATE, book.getPublishDate());
        values.put(COLUMN_IMAGE_PATH, book.getImagePath());
        values.put(COLUMN_LOCATION, book.getLocation());
        values.put(COLUMN_CATEGORY_ID, book.getCategoryId());
        values.put(COLUMN_STATUS, book.getStatus());
        values.put(COLUMN_CREATE_TIME, book.getCreateTime());
        
        int result = db.update(TABLE_BOOKS, values, COLUMN_BOOK_ID + " = ?", 
                new String[]{String.valueOf(book.getBookId())});
        db.close();
        return result > 0;
    }

    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BOOKS, COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)});
        db.close();
        return result > 0;
    }

    // 类目管理相关方法
    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        
        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, COLUMN_CATEGORY_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return categoryList;
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        
        Cursor cursor = db.query(TABLE_CATEGORIES, null, selection, selectionArgs, null, null, null);
        
        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return category;
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        
        int result = db.update(TABLE_CATEGORIES, values, COLUMN_CATEGORY_ID + " = ?", 
                new String[]{String.valueOf(category.getCategoryId())});
        db.close();
        return result > 0;
    }

    public boolean deleteCategory(int categoryId) {
        // 检查该类目下是否有图书
        SQLiteDatabase db = this.getReadableDatabase();
        String checkQuery = "SELECT COUNT(*) FROM " + TABLE_BOOKS + " WHERE " + COLUMN_CATEGORY_ID + " = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(categoryId)});
        
        boolean hasBooks = false;
        if (cursor != null && cursor.moveToFirst()) {
            hasBooks = cursor.getInt(0) > 0;
            cursor.close();
        }
        
        if (hasBooks) {
            db.close();
            return false; // 该类目下有图书，不能删除
        }
        
        db.close();
        
        // 删除类目
        SQLiteDatabase writeDb = this.getWritableDatabase();
        int result = writeDb.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        writeDb.close();
        return result > 0;
    }

    // 搜索图书方法
    public List<Book> searchBooks(String keyword) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_BOOKS + 
                      " WHERE " + COLUMN_TITLE + " LIKE ? OR " + 
                      COLUMN_AUTHOR + " LIKE ? OR " + 
                      COLUMN_ISBN + " LIKE ?" + 
                      " ORDER BY " + COLUMN_CREATE_TIME + " DESC";
        
        String searchKeyword = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchKeyword, searchKeyword, searchKeyword});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setBookId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
                book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                book.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)));
                book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
                book.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                book.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                book.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                book.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
                
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return bookList;
    }

    // 搜索类目方法
    public List<Category> searchCategories(String keyword) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_CATEGORIES + 
                      " WHERE " + COLUMN_CATEGORY_NAME + " LIKE ?" + 
                      " ORDER BY " + COLUMN_CATEGORY_NAME + " ASC";
        
        String searchKeyword = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchKeyword});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return categoryList;
    }

    // ========== 借阅记录管理相关方法 ==========

    // 添加借阅记录
    public long addBorrowRecord(BorrowRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_ID, record.getBookId());
        values.put(COLUMN_ISBN, record.getIsbn());
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_AUTHOR, record.getAuthor());
        values.put(COLUMN_DESCRIPTION, record.getDescription());
        values.put(COLUMN_PUBLISHER, record.getPublisher());
        values.put(COLUMN_PUBLISH_DATE, record.getPublishDate());
        values.put(COLUMN_IMAGE_PATH, record.getImagePath());
        values.put(COLUMN_LOCATION, record.getLocation());
        values.put(COLUMN_CATEGORY_ID, record.getCategoryId());
        values.put(COLUMN_STATUS, record.getStatus());
        values.put(COLUMN_USERNAME, record.getUsername());
        values.put(COLUMN_BORROW_TIME, record.getBorrowTime());
        values.put(COLUMN_RETURN_TIME, record.getReturnTime());
        values.put(COLUMN_OVERDUE_STATUS, record.getOverdueStatus());
        values.put(COLUMN_OVERDUE_DAYS, record.getOverdueDays());
        
        long id = db.insert(TABLE_BORROW_RECORDS, null, values);
        db.close();
        return id;
    }

    // 获取所有借阅记录
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> recordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_BORROW_RECORDS + " ORDER BY " + COLUMN_BORROW_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORD_ID)));
                record.setBookId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                record.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
                record.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                record.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
                record.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                record.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)));
                record.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
                record.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                record.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                record.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                record.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                record.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                record.setBorrowTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_TIME)));
                record.setReturnTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RETURN_TIME)));
                record.setOverdueStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OVERDUE_STATUS)));
                record.setOverdueDays(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OVERDUE_DAYS)));
                
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return recordList;
    }

    // 根据用户名获取借阅记录
    public List<BorrowRecord> getBorrowRecordsByUsername(String username) {
        List<BorrowRecord> recordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_BORROW_RECORDS, null, selection, selectionArgs, null, null, COLUMN_BORROW_TIME + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORD_ID)));
                record.setBookId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                record.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
                record.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                record.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
                record.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                record.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)));
                record.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
                record.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));
                record.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                record.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                record.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                record.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                record.setBorrowTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_TIME)));
                record.setReturnTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RETURN_TIME)));
                record.setOverdueStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OVERDUE_STATUS)));
                record.setOverdueDays(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OVERDUE_DAYS)));
                
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return recordList;
    }

    // 更新借阅记录状态
    public boolean updateBorrowRecordStatus(int recordId, int status, String returnTime, String overdueStatus, int overdueDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_RETURN_TIME, returnTime);
        values.put(COLUMN_OVERDUE_STATUS, overdueStatus);
        values.put(COLUMN_OVERDUE_DAYS, overdueDays);
        
        int result = db.update(TABLE_BORROW_RECORDS, values, COLUMN_RECORD_ID + " = ?", 
                new String[]{String.valueOf(recordId)});
        db.close();
        return result > 0;
    }

    // 删除借阅记录
    public boolean deleteBorrowRecord(int recordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BORROW_RECORDS, COLUMN_RECORD_ID + " = ?", new String[]{String.valueOf(recordId)});
        db.close();
        return result > 0;
    }

    // 更新图书状态
    public boolean updateBookStatus(int bookId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        
        int result = db.update(TABLE_BOOKS, values, COLUMN_BOOK_ID + " = ?", 
                new String[]{String.valueOf(bookId)});
        db.close();
        return result > 0;
    }

    // ========== 借阅配置管理相关方法 ==========

    // 获取借阅配置
    public BorrowConfig getBorrowConfig() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_BORROW_CONFIG, null, null, null, null, null, COLUMN_CONFIG_ID + " ASC", "1");
        
        BorrowConfig config = null;
        if (cursor != null && cursor.moveToFirst()) {
            config = new BorrowConfig();
            config.setConfigId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_ID)));
            config.setMaxBorrowDays(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_BORROW_DAYS)));
            config.setMaxBorrowCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_BORROW_COUNT)));
            config.setUpdateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATE_TIME)));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        
        // 如果没有配置，返回默认配置
        if (config == null) {
            config = new BorrowConfig(30, 5, "2024-01-01 10:00:00");
        }
        
        return config;
    }

    // 更新借阅配置
    public boolean updateBorrowConfig(int maxBorrowDays, int maxBorrowCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 获取当前时间
        String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAX_BORROW_DAYS, maxBorrowDays);
        values.put(COLUMN_MAX_BORROW_COUNT, maxBorrowCount);
        values.put(COLUMN_UPDATE_TIME, currentTime);
        
        // 检查是否已有配置
        Cursor cursor = db.query(TABLE_BORROW_CONFIG, null, null, null, null, null, COLUMN_CONFIG_ID + " ASC", "1");
        int result;
        
        if (cursor != null && cursor.moveToFirst()) {
            // 有现有配置，更新它
            int configId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_ID));
            result = db.update(TABLE_BORROW_CONFIG, values, COLUMN_CONFIG_ID + " = ?", 
                    new String[]{String.valueOf(configId)});
        } else {
            // 没有配置，插入新配置
            result = (int) db.insert(TABLE_BORROW_CONFIG, null, values);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result > 0;
    }

    // 获取用户总借阅图书数量
    public int getUserTotalBorrowCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + TABLE_BORROW_RECORDS + 
                      " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    // 获取用户当前借阅中的图书数量
    public int getUserCurrentBorrowCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + TABLE_BORROW_RECORDS + 
                      " WHERE " + COLUMN_USERNAME + " = ? AND " + 
                      COLUMN_STATUS + " = 0"; // 状态为0表示借阅中
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    // 获取用户已归还的图书数量
    public int getUserReturnedCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + TABLE_BORROW_RECORDS + 
                      " WHERE " + COLUMN_USERNAME + " = ? AND " + 
                      COLUMN_STATUS + " = 1"; // 状态为1表示已归还
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    // 获取用户逾期未还的图书数量
    public int getUserOverdueCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + TABLE_BORROW_RECORDS + 
                      " WHERE " + COLUMN_USERNAME + " = ? AND " + 
                      COLUMN_OVERDUE_STATUS + " = '逾期'";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    // 获取用户最近借阅的图书信息（最多5本）
    public String getUserRecentBorrows(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String recentBooks = "";
        
        String query = "SELECT br.*, b.title, b.author FROM " + TABLE_BORROW_RECORDS + " br " +
                "LEFT JOIN " + TABLE_BOOKS + " b ON br.book_id = b.book_id " +
                "WHERE br.username = ? " +
                "ORDER BY br.borrow_time DESC LIMIT 5";
        
        Cursor cursor = db.rawQuery(query, new String[]{username});
        
        if (cursor.moveToFirst()) {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String borrowTime = cursor.getString(cursor.getColumnIndexOrThrow("borrow_time"));
                
                // 格式化借阅时间（只显示日期部分）
                String formattedTime = borrowTime;
                if (borrowTime != null && borrowTime.length() >= 10) {
                    formattedTime = borrowTime.substring(0, 10);
                }
                
                sb.append("• ").append(title).append(" - ").append(author)
                  .append(" (").append(formattedTime).append(")\n");
                count++;
            } while (cursor.moveToNext() && count < 5);
            
            recentBooks = sb.toString().trim();
        }
        
        cursor.close();
        db.close();
        return recentBooks;
    }
}