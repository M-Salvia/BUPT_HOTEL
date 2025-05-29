package org.mason.server_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mason.server_back.entity.AccommodationOrder;

public interface AccommodationOrderService extends IService<AccommodationOrder> {
    // 可以在此定义一些与业务相关的方法
    void calculateOrderTotalFee(String orderId);
}
