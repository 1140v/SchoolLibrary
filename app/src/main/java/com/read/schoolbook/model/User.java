package com.read.schoolbook.model;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String gender;
    private int age;
    private String contact;
    private String address;
    private int permission; // 0: 普通用户, 1: 管理员

    public User() {
    }

    public User(String username, String password, String gender, int age, String contact, String address, int permission) {
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.contact = contact;
        this.address = address;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public boolean isAdmin() {
        return permission == 1;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", permission=" + permission +
                '}';
    }
}