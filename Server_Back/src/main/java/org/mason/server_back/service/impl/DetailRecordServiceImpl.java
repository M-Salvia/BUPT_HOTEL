package org.mason.server_back.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mason.server_back.entity.DetailRecord;
import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.entity.ServiceObject;
import org.mason.server_back.mapper.DetailRecordMapper;
import org.mason.server_back.service.DetailRecordService;
import org.mason.server_back.service.ServiceScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DetailRecordServiceImpl extends ServiceImpl<DetailRecordMapper, DetailRecord> implements DetailRecordService {
    @Autowired
    @Lazy
    private ServiceScheduler schedulerService;
    @Override
    public List<DetailRecord> listByOrderId(String orderId) {
        // 使用 QueryWrapper 构造查询条件
        QueryWrapper<DetailRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId); // 根据订单ID查询

        // 使用 MyBatis-Plus 提供的方法来查询数据
        return this.list(queryWrapper); // 返回所有符合条件的详单记录
    }


    public DetailRecord createDetailRecord(ServiceObject serviceObj) {
        // 参数校验
        if (serviceObj == null) {
            throw new IllegalArgumentException("ServiceObject cannot be null");
        }

        // 计算费用相关数据
        BigDecimal rate = getFanSpeedRate(serviceObj.getFanSpeed());  // 根据风速计算费率
        BigDecimal currentCost = calculateCost(serviceObj);         // 计算当前费用
        long timestamp = System.currentTimeMillis();
        String orderId = ServiceScheduler.getOrderIdByRoomId(serviceObj.getRoomId());
        // 创建详单对象
        DetailRecord record = new DetailRecord(
                null, // detailRecordId
                orderId,
                serviceObj.getRoomId(),
                serviceObj.getCreateTime(), // 请求时间使用服务开始时间
                serviceObj.serviceTimer.getstart(),
                LocalDateTime.now(), // 服务结束时间
                serviceObj.getServiceDuration(),
                serviceObj.getFanSpeed(),
                currentCost.doubleValue(),
                rate.doubleValue()
        );
        return record;
    }

    //getFanSpeedRate方法根据风速计算费率
    public BigDecimal getFanSpeedRate(FanSpeed speed) {
        switch (speed) {
            case HIGH:
                return new BigDecimal("0.1");
            case MEDIUM:
                return new BigDecimal("0.05");
            case LOW:
                return new BigDecimal("0.033");
        }
        return null;
    }

    //calculateCost方法计算当前费用
    public BigDecimal calculateCost(ServiceObject serviceObj) {
        BigDecimal rate = getFanSpeedRate(serviceObj.getFanSpeed());
        BigDecimal duration = new BigDecimal(serviceObj.getServiceDuration());
        duration = duration.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP); // 再乘以6;
        BigDecimal cost = rate.multiply(duration);
        return cost;
    }

    public void printDetailRecord(DetailRecord record) {
        // 可以根据需求实现不同的打印格式
        System.out.println(record.toString());
        // 或者添加更多的打印逻辑
    }
}
