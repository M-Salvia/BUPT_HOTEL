package org.mason.server_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

@TableName("customer")  // 指定实体类对应的数据库表名
public class Customer {

    // 顾客编号（唯一标识符），使用@TableId注解表示主键
    @TableId
    private String customerId;    // 顾客编号（唯一标识符）

    // 顾客姓名
    @TableField("customerName")
    private String customerName;          // 姓名

    // 顾客身份证号码
    @TableField("idCard")
    private String idCard;        // 身份证号码

    // 顾客联系方式
    @TableField("phone")
    private String phone;         // 联系方式

    // 构造方法
    public Customer(String customerId, String name, String idCard, String phone) {
        this.customerId = customerId;
        this.customerName = name;
        this.idCard = idCard;
        this.phone = phone;
    }

    // 默认构造方法
    public Customer() {
    }

    // Getter 和 Setter 方法
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return customerName;
    }

    public void setName(String name) {
        this.customerName = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 方法：展示顾客信息
    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + customerName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
