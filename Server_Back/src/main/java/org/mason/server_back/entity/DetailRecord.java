package org.mason.server_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

@TableName("DetailRecord") // 表名，注意大小写敏感时应与数据库一致
public class DetailRecord {

    @TableId(value = "DetailRecord_id", type = IdType.AUTO)
    private Long detailRecordId;

    @TableField("order_Id")
    private String orderId;

    @TableField("room_Id")
    private String roomId;

    @TableField("requestTime")
    private LocalDateTime requestTime;

    @TableField("serviceStartTime")
    private LocalDateTime serviceStartTime;

    @TableField("serviceEndTime")
    private LocalDateTime serviceEndTime;

    @TableField("serviceDuration")
    private long serviceDuration;

    @TableField("fanSpeed")
    private FanSpeed fanSpeed;

    @TableField("currentFee")
    private double currentFee;

    @TableField("feeRate")
    private double feeRate;

    // 构造方法
    public DetailRecord(Long detailRecordId,
                        String orderId,
                        String roomId,
                        LocalDateTime requestTime,
                        LocalDateTime serviceStartTime,
                        LocalDateTime serviceEndTime,
                        long serviceDuration,
                        FanSpeed fanSpeed,
                        double currentFee,
                        double feeRate) {
        this.detailRecordId = detailRecordId;
        this.roomId = roomId;
        this.orderId = orderId;
        this.requestTime = requestTime; // 请求时间使用服务开始时间
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.serviceDuration = serviceDuration;
        this.fanSpeed = fanSpeed;
        this.currentFee = currentFee;
        this.feeRate = feeRate;
    }

    // 无参构造
    public DetailRecord() {}

    // Getter 和 Setter

    public Long getDetailRecordId() {
        return detailRecordId;
    }

    public void setDetailRecordId(long detailRecordId) {
        this.detailRecordId = detailRecordId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public LocalDateTime getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(LocalDateTime serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public LocalDateTime getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(LocalDateTime serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public long getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(long serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public FanSpeed getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(FanSpeed fanSpeed) {
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

    @Override
    public String toString() {
        return "DetailRecord{" +
                "detailRecordId=" + detailRecordId +
                ", orderId='" + orderId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", requestTime=" + requestTime +
                ", serviceStartTime=" + serviceStartTime +
                ", serviceEndTime=" + serviceEndTime +
                ", serviceDuration=" + serviceDuration +
                ", fanSpeed='" + fanSpeed + '\'' +
                ", currentFee=" + currentFee +
                ", feeRate=" + feeRate +
                '}';
    }
}
