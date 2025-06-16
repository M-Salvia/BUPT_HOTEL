package org.mason.server_back.entity;

public class RoomStatusDTO {
    public boolean isOn; // 表示房间空调是否开启的状态
    public double targetTemp;// 房间目标温度，用户设定的期望温度值
    public FanSpeed windSpeed;// 当前风速设置（枚举类型：低速、中速、高速）
    public double currentFee;// 当前服务产生的费用
    public boolean isWaiting;// 表示该房间是否处于等待队列中
    public long serviceTime;// 房间已经接受服务的时间（毫秒）
    public long waitingTime;// 房间在等待队列中的等待时间（毫秒）
    public int detailCount;// 该房间产生的详单数量统计
    public int tempChangeCount;// 该房间温度调节次数统计
    public int windChangeCount;// 该房间风速调节次数统计
    public int dayCount;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String mode;
    public double currentTemp;
    // 构造函数
    public void setIsOn(boolean inService) {
        this.isOn = inService;
    }

    public void setCurrentFee(double doubleValue) {
        this.currentFee = doubleValue;
    }

    public void setWaitingTime(long duration) {
        this.waitingTime = duration;
    }

    public void setServiceTime(long duration) {
        this.serviceTime = duration;
    }

    public void setIsWaiting(boolean roomBeingWaiting) {
        this.isWaiting = roomBeingWaiting;
    }

    public void setWindSpeed(FanSpeed fanSpeed) {
        this.windSpeed = fanSpeed;
    }

    public void setTargetTemp(double targetTemperature) {
        this.targetTemp = targetTemperature;
    }
    public void setDetailCount(int detailCount) {
        this.detailCount = detailCount;
    }

    public void setTempChangeCount(int tempChangeCount) {
        this.tempChangeCount = tempChangeCount;
    }
    public void setWindChangeCount(int windChangeCount) {
        this.windChangeCount = windChangeCount;
    }
    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }
    // Getters and Setters
    // ... 添加所有字段的getter和setter方法
}
