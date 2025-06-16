package org.mason.server_back.controller;

import jakarta.persistence.Access;
import org.mason.server_back.entity.AccommodationOrder;
import org.mason.server_back.entity.DetailRecord;
import org.mason.server_back.entity.RoomStatusDTO;
import org.mason.server_back.entity.ServiceObject;
import org.mason.server_back.service.AccommodationOrderService;
import org.mason.server_back.service.DetailRecordService;
import org.mason.server_back.service.ServiceScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class CheckOutController {

    @Autowired
    @Lazy
    private ServiceScheduler schedulerService;



    private final AccommodationOrderService accommodationOrderService;
    private final DetailRecordService detailRecordService;

    // 构造函数注入
    public CheckOutController(AccommodationOrderService accommodationOrderService, DetailRecordService detailRecordService) {
        this.accommodationOrderService = accommodationOrderService;
        this.detailRecordService = detailRecordService;
    }

    @PostMapping("/api/checkout")
    public ResponseEntity<Object> processCheckOut(@RequestBody Map<String, String> payload) {
        String roomId = payload.get("roomId"); // 从请求体中提取 roomId
        String orderId = ServiceScheduler.getOrderIdByRoomId(roomId); // 根据房间号获取订单ID
        // 根据房间号获取对应的订单信息
        AccommodationOrder order = accommodationOrderService.getOrderById(orderId);



        if (order == null) {
            return new ResponseEntity<>("Order not found for the given room", HttpStatus.NOT_FOUND);
        }

        // 设置退房时间为当前时间
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        order.setCheckOutDate(currentTimestamp);

        // 获取详单信息
        List<DetailRecord> detailRecords = detailRecordService.listByOrderId(order.getOrderId());

        // 计算所有详单的总费用
        float totalACFee = 0;
        Iterator<DetailRecord> iterator = detailRecords.iterator(); // 获取迭代器

        while (iterator.hasNext()) {
            DetailRecord record = iterator.next();
            if (record.getCurrentFee() <= 0.1) {
                iterator.remove(); // 从集合中移除该记录，但不删除数据库中的记录
                continue; // 跳过当前记录，不参与费用累加
            }
            totalACFee += record.getCurrentFee();
        }



        // 更新订单的 AC Fee 字段
        order.setACFee(totalACFee);  // 设置订单的空调费用为总费用

        order.setTotalDay(schedulerService.getDayCount(roomId));

        //更新订单状态为 "finished"
        order.setOrderState("finished");

        // 更新订单信息到数据库
        accommodationOrderService.updateById(order);

        // 构建响应体，包含订单信息和详单信息
        CheckOutResponse response = new CheckOutResponse();
        response.setOrder(order); // 账单信息
        response.setDetailRecords(detailRecords); // 详单信息
        // 返回退房信息，状态码 200 OK
        schedulerService.removeCounter(roomId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 内部类，封装返回的退房信息，包括订单和详单
    public static class CheckOutResponse {
        private AccommodationOrder order;
        private List<DetailRecord> detailRecords;

        // Getter 和 Setter
        public AccommodationOrder getOrder() {
            return order;
        }

        public void setOrder(AccommodationOrder order) {
            this.order = order;
        }

        public List<DetailRecord> getDetailRecords() {
            return detailRecords;
        }

        public void setDetailRecords(List<DetailRecord> detailRecords) {
            this.detailRecords = detailRecords;
        }
    }
}
