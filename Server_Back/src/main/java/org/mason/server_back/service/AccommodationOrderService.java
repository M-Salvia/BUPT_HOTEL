package org.mason.server_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mason.server_back.entity.AccommodationOrder;

public interface AccommodationOrderService extends IService<AccommodationOrder> {
    // 可以在此定义一些与业务相关的方法
    /**
     * 计算订单的总费用
     *
     * @param orderId 订单ID
     */
    void calculateOrderTotalFee(String orderId);
    /**
     * 根据订单ID获取订单信息
     *
     * @param orderId 订单ID
     * @return AccommodationOrder 对象
     */
    AccommodationOrder getOrderById(String orderId);

    // 通过房间号获取对应的订单
    AccommodationOrder getByRoomId(String roomId);
    /**
     * 通过房间号获取对应的订单
     *
     * @param roomId 房间ID
     * @return AccommodationOrder 对象
     */
}
