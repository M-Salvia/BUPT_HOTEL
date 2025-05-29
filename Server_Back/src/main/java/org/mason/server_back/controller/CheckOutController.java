//package org.mason.server_back.controller;
//
//import org.mason.server_back.entity.AccommodationOrder;
//import org.mason.server_back.entity.DetailRecord;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.sql.Timestamp;
//import java.util.List;
//
//@RestController
//@RequestMapping("/checkout")
//public class CheckOutController {
//
//    // 假设订单数据已经存储在某个存储层或数据库中
//    private final Map<String, AccommodationOrder> orderMap = new HashMap<>();
//
//    // 退房操作
//    @PostMapping("/process")
//    public ResponseEntity<String> processCheckOut(@RequestParam String orderId) {
//        // 获取订单信息
//        AccommodationOrder order = orderMap.get(orderId);
//
//        // 设置退房时间为当前时间
//        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
//        order.setCheckOutDate(currentTimestamp);
//
//        // 获取详单信息并构建退房信息
//        List<DetailRecord> detailRecords = order.getDetailRecordList();
//        StringBuilder checkoutInfo = new StringBuilder();
//        checkoutInfo.append("退房订单信息：\n");
//        checkoutInfo.append("订单ID: ").append(order.getOrderId()).append("\n");
//        checkoutInfo.append("房间号: ").append(order.getRoomId()).append("\n");
//        checkoutInfo.append("入住时间: ").append(order.getCheckInDate()).append("\n");
//        checkoutInfo.append("退房时间: ").append(order.getCheckOutDate()).append("\n");
//        checkoutInfo.append("空调费: ").append(order.getACFee()).append("元\n");
//        //总费用
//
//        // 遍历详单记录
//        for (DetailRecord record : detailRecords) {
//            checkoutInfo.append("服务请求时间: ").append(record.getRequestTime()).append("\n");
//            checkoutInfo.append("服务开始时间: ").append(record.getServiceStartTime()).append("\n");
//            checkoutInfo.append("服务结束时间: ").append(record.getServiceEndTime()).append("\n");
//            checkoutInfo.append("服务时长: ").append(record.getServiceDuration()).append("秒\n");
//            checkoutInfo.append("风速: ").append(record.getFanSpeed()).append("\n");
//            checkoutInfo.append("当前费用: ").append(record.getCurrentFee()).append("元\n");
//            checkoutInfo.append("费用率: ").append(record.getFeeRate()).append("\n");
//        }
//
//        checkoutInfo.append("空调费用: ").append(order.getACFee()).append("元\n");
//
//        // 返回退房信息
//        return ResponseEntity.ok(checkoutInfo.toString());
//    }
//
//    // 模拟数据初始化（用于测试）
//    @PostMapping("/createOrder")
//    public ResponseEntity<String> createOrder(@RequestParam String idCard, @RequestParam String roomId) {
//        // 创建一个新订单
//        AccommodationOrder order = new AccommodationOrder(idCard, roomId);
//        orderMap.put(order.getOrderId(), order);
//        return ResponseEntity.ok("订单创建成功，订单ID: " + order.getOrderId());
//    }
//}
