package com.read.schoolbook.model;

import java.io.Serializable;

public class BorrowRecord implements Serializable {
    private int recordId;           // 借阅记录ID
    private int bookId;             // 图书编号
    private String isbn;            // ISBN
    private String title;           // 书名
    private String author;          // 作者
    private String description;     // 简介
    private String publisher;      // 出版社
    private String publishDate;     // 出版日期
    private String imagePath;       // 图片路径
    private String location;        // 书所在馆藏位置
    private int categoryId;         // 类别ID
    private int status;             // 状态（0: 借出, 1: 进行中, 2: 归还）
    private String username;        // 用户账号
    private String borrowTime;      // 借阅时间
    private String expectedReturnTime; // 预计归还时间
    private String returnTime;      // 归还时间
    private String overdueStatus;   // 逾期状态（正常，已经逾期XX天）
    private int overdueDays;        // 逾期天数

    public BorrowRecord() {
        this.isbn = "";
        this.title = "";
        this.author = "";
        this.description = "";
        this.publisher = "";
        this.publishDate = "";
        this.imagePath = "";
        this.location = "";
        this.username = "";
        this.borrowTime = "";
        this.expectedReturnTime = "";
        this.returnTime = "";
        this.overdueStatus = "";
    }

    public BorrowRecord(int bookId, String isbn, String title, String author, String description,
                       String publisher, String publishDate, String imagePath, String location,
                       int categoryId, int status, String username, String borrowTime) {
        this.bookId = bookId;
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
        this.username = username;
        this.borrowTime = borrowTime;
    }

    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getOverdueStatus() {
        return overdueStatus;
    }

    public void setOverdueStatus(String overdueStatus) {
        this.overdueStatus = overdueStatus;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(String expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public String getStatusText() {
        switch (status) {
            case 0:
                return "借出";
            case 1:
                return "进行中";
            case 2:
                return "归还";
            default:
                return "未知";
        }
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "recordId=" + recordId +
                ", bookId=" + bookId +
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
                ", username='" + username + '\'' +
                ", borrowTime='" + borrowTime + '\'' +
                ", returnTime='" + returnTime + '\'' +
                ", overdueStatus='" + overdueStatus + '\'' +
                ", overdueDays=" + overdueDays +
                '}';
    }
}