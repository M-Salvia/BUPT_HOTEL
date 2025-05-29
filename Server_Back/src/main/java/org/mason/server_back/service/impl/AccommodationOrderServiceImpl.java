package org.mason.server_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mason.server_back.entity.AccommodationOrder;
import org.mason.server_back.mapper.AccommodationOrderMapper;
import org.mason.server_back.service.AccommodationOrderService;
import org.springframework.stereotype.Service;

@Service
public class AccommodationOrderServiceImpl extends ServiceImpl<AccommodationOrderMapper, AccommodationOrder> implements AccommodationOrderService {

    @Override
    public void calculateOrderTotalFee(String orderId) {
        // 例如，计算订单的总费用
        AccommodationOrder order = this.getById(orderId);
        if (order != null) {
            // 假设有一些计算逻辑
            float totalFee = order.getStayFee() + order.getACFee();
            order.setTotalfee(totalFee);
            this.updateById(order);  // 更新订单的总费用
        }
    }
}
