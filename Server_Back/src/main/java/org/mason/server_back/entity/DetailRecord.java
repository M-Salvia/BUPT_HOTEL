package org.mason.server_back.entity;

import java.util.Calendar;
import java.util.Date;

public class DetailRecord {

    // 详单，每一次服务对应一个详单
    private long id;              // 唯一自增主键
    private String roomId;        // 房间号（房间类的主键）
    private Date requestTime;     // 请求时间
    private Date serviceStartTime; // 服务开始时间
    private Date serviceEndTime;   // 服务结束时间
    private long serviceDuration;  // 服务时长（秒）
    private String fanSpeed;      // 风速
    private double currentFee;    // 当前费用
    private double feeRate;       // 费率

    // 构造方法
    public DetailRecord(long id, String roomId, Date requestTime, Date serviceStartTime, Date serviceEndTime, String fanSpeed, double currentFee, double feeRate) {
        this.id = id;
        this.roomId = roomId;
        this.requestTime = requestTime;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.fanSpeed = fanSpeed;
        this.currentFee = currentFee;
        this.feeRate = feeRate;

        // 计算服务时长，单位为秒
        if (serviceStartTime != null && serviceEndTime != null) {
            this.serviceDuration = (serviceEndTime.getTime() - serviceStartTime.getTime()) / 1000; // 将毫秒转为秒
        } else {
            this.serviceDuration = 0;  // 如果服务时间无效，设置时长为0
        }

    }



    // Getter 和 Setter 方法

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(Date serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public Date getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(Date serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public long getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(long serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(String fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public double getCurrentFee() {
        return currentFee;
    }

    public void setCurrentFee(double currentFee) {
        this.currentFee = currentFee;
    }

    public double getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(double feeRate) {
        this.feeRate = feeRate;
    }

    // toString() 方法，输出详单信息
    @Override
    public String toString() {
        return "DetailRecord{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", requestTime=" + requestTime +
                ", serviceStartTime=" + serviceStartTime +
                ", serviceEndTime=" + serviceEndTime +
                ", serviceDuration=" + serviceDuration + " seconds" +
                ", fanSpeed='" + fanSpeed + '\'' +
                ", currentFee=" + currentFee +
                ", feeRate=" + feeRate +
                '}';
    }
}
