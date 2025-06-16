package org.mason.server_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mason.server_back.entity.AccommodationOrder;
import org.mason.server_back.mapper.AccommodationOrderMapper;
import org.mason.server_back.mapper.RoomMapper;
import org.mason.server_back.service.AccommodationOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccommodationOrderServiceImpl extends ServiceImpl<AccommodationOrderMapper, AccommodationOrder> implements AccommodationOrderService {

    @Autowired
    private AccommodationOrderMapper accommodationOrderMapper;  // 注入Mapper

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

    @Override
    public AccommodationOrder getOrderById(String orderId) {
        return this.getById(orderId);  // 获取订单信息
    }

    @Override
    public AccommodationOrder getByRoomId(String roomId) {
        // 假设通过 MyBatis-Plus 提供的条件查询来查找订单
        return this.baseMapper.selectOne(new QueryWrapper<AccommodationOrder>().eq("room_id", roomId));
    }
}
