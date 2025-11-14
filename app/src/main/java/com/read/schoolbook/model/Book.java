package com.read.schoolbook.model;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {
    private int bookId;           // 图书编号
    private String isbn;          // ISBN
    private String title;         // 书名
    private String author;        // 作者
    private String description;   // 简介
    private String publisher;     // 出版社
    private String publishDate;   // 出版日期
    private String imagePath;     // 图片路径
    private String location;      // 书所在馆藏位置
    private int categoryId;       // 类别ID
    private int status;           // 状态（0: 未借出, 1: 借出）
    private String createTime;    // 录入时间

    public Book() {
    }

    public Book(String isbn, String title, String author, String description, 
                String publisher, String publishDate, String imagePath, 
                String location, int categoryId, int status, String createTime) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.imagePath = imagePath;
        this.location = location;
        this.categoryId = categoryId;
        this.status = status;
        this.createTime = createTime;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isBorrowed() {
        return status == 1;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", location='" + location + '\'' +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}