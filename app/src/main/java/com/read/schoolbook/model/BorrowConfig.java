package com.read.schoolbook.model;

import java.io.Serializable;

public class BorrowConfig implements Serializable {
    private int configId;           // 配置ID
    private int maxBorrowDays;      // 最大借阅天数
    private int maxBorrowCount;      // 最大借阅数量
    private String updateTime;      // 更新时间

    public BorrowConfig() {
    }

    public BorrowConfig(int maxBorrowDays, int maxBorrowCount, String updateTime) {
        this.maxBorrowDays = maxBorrowDays;
        this.maxBorrowCount = maxBorrowCount;
        this.updateTime = updateTime;
    }

    // Getters and Setters
    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    public void setMaxBorrowDays(int maxBorrowDays) {
        this.maxBorrowDays = maxBorrowDays;
    }

    public int getMaxBorrowCount() {
        return maxBorrowCount;
    }

    public void setMaxBorrowCount(int maxBorrowCount) {
        this.maxBorrowCount = maxBorrowCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BorrowConfig{" +
                "configId=" + configId +
                ", maxBorrowDays=" + maxBorrowDays +
                ", maxBorrowCount=" + maxBorrowCount +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}