package org.mason.server_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mason.server_back.entity.DetailRecord;
import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.entity.ServiceObject;

import java.math.BigDecimal;
import java.util.List;

public interface DetailRecordService extends IService<DetailRecord> {

    // 通过订单 ID 查询所有相关的详单记录
    List<DetailRecord> listByOrderId(String orderId);
    public DetailRecord createDetailRecord(ServiceObject serviceObj);
    public BigDecimal getFanSpeedRate(FanSpeed speed);
    public BigDecimal calculateCost(ServiceObject serviceObj);
    public void printDetailRecord(DetailRecord record);

}
