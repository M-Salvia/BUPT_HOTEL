package org.mason.server_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

@TableName("AccommodationOrder")  // 指定实体类对应的数据库表名
public class AccommodationOrder {

    @TableId  // 表明 orderId 是主键
    private String orderId;       // 订单ID（唯一标识符）

    @TableField("idCard")        // 映射 idCard 字段
    private String idCard;    // 顾客身份证号码，与顾客关联

    @TableField("roomId")        // 映射 roomId 字段
    private String roomId;        // 房间ID（与房间关联）

    @TableField("checkInDate")   // 映射 checkInDate 字段
    private Timestamp checkInDate;     // 入住时间

    @TableField("checkOutDate")   // 映射 checkOutDate 字段
    private Timestamp checkOutDate;    // 离店时间

    @TableField("ACFee")         // 映射 ACFee 字段
    private float ACFee;          // 空调费用

    @TableField("stayFee")       // 映射 stayFee 字段
    private float stayFee;        // 住宿费用

    @TableField("totalfee")      // 映射 totalfee 字段
    private float totalfee;         // 总费用

    @TableField("totalday")           // 映射 day 字段
    private int day = 0;              // 入住天数

    @TableField(exist = false)  // 表示该字段不在数据库中
    private List<DetailRecord> detailRecordList = new ArrayList<>(); // 本订单对应的详单

    // 构造方法
    public AccommodationOrder(String idCard, String roomId) {
        this.idCard = idCard;
        this.roomId = roomId;
        this.orderId = generateOrderId();  // 生成订单ID
        this.checkInDate = new Timestamp(System.currentTimeMillis());  // 设置入住时间为当前时间
        this.checkOutDate = null; // 默认离店时间为null
        this.ACFee = 0;           // 默认空调费用
        this.stayFee = 0;         // 默认住宿费用
        this.totalfee = 0;        // 默认费用
        this.detailRecordList = new ArrayList<>(); // 初始化详单
    }

    // 订单ID生成方法：生成一个唯一的订单号
    private String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        return "ORD-" + timestamp;  // 使用时间戳生成订单号
    }

    // Getter 和 Setter 方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Timestamp getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Timestamp checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Timestamp getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Timestamp checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public float getACFee() {
        return ACFee;
    }

    public void setACFee(float ACFee) {
        this.ACFee = ACFee;
    }

    public float getStayFee() {
        return stayFee;
    }

    public void setStayFee(float stayFee) {
        this.stayFee = stayFee;
    }

    public float getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(float totalfee) {
        this.totalfee = totalfee;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<DetailRecord> getDetailRecordList() {
        return detailRecordList;
    }

    public void setDetailRecordList(List<DetailRecord> detailRecordList) {
        this.detailRecordList = detailRecordList;
    }

    // 计算空调费用
    public void calculateACFee() {
        float total = 0;
        for (DetailRecord record : detailRecordList) {
            total += record.getCurrentFee();  // 累加每条服务记录的费用
        }
        this.ACFee = total;  // 更新空调费用
    }

    // 每开一次空调入住日期加1天
    public void updateDay() {
        this.day = this.day + 1; // 每次调用此方法，入住天数加1
    }

}
