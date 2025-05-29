package org.mason.server_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

@TableName("Room")  // 指定实体类对应的数据库表名
public class Room {

    @TableId  // 表明 roomId 是主键
    private String roomId;        // 房间号

    @TableField("roomType")          // 映射 type 字段
    private int roomType;             // 房型（1, 2, 3, 4, 5 等）

    @TableField("pricePerDay")   // 映射 pricePerDay 字段
    private double pricePerDay;   // 每日价格

    @TableField("roomState")         // 映射 state 字段
    private String roomState;         // 房间状态（如：available、booked）

    @TableField("deposit")       // 映射 deposit 字段
    private double deposit;       // 押金（默认房价1/5）

    @TableField("temperature")   // 映射 temperature 字段
    private double temperature;   // 房间温度（如：22.0）

    // 构造方法
    public Room(String roomId, int type, double pricePerDay, String state) {
        this.roomId = roomId;
        this.roomType = type;
        this.pricePerDay = pricePerDay;
        this.roomState = state;
        this.deposit = pricePerDay / 5; // 押金为房价的1/5
        this.temperature = 22.0;  // 假设默认温度为22.0
    }

    // Getter 和 Setter 方法
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getType() {
        return roomType;
    }

    public void setType(int type) {
        this.roomType = type;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getState() {
        return roomState;
    }

    public void setState(String state) {
        this.roomState = state;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    // toString 方法，方便输出房间的基本信息
    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", type=" + roomType +
                ", pricePerDay=" + pricePerDay +
                ", state='" + roomState + '\'' +
                ", deposit=" + deposit +
                ", temperature=" + temperature +
                '}';
    }
}
