package org.mason.server_back.service;

import org.mason.server_back.entity.AccommodationOrder;
import org.mason.server_back.mapper.AccommodationOrderMapper;
import org.mason.server_back.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {

    @Autowired
    private RoomService roomService;  // 房间服务，用于查找和更新房间状态

    @Autowired
    private AccommodationOrderMapper orderMapper;  // 订单Mapper，用于插入订单

    @Autowired
    private ServiceScheduler schedulerService;

    /**
     * 创建住宿订单
     *
     * @param idCard 身份证号
     * @param type   房间类型（如单人间、双人间等）
     * @return 房间 ID，如果没有空闲房间返回 null
     */
    public String createAccommodationOrder(String idCard, int type) {
        // 1. 查找一个空闲房间
        String roomId = roomService.findAvailableRoom(type);


        if (roomId != null) {
            // 2. 创建订单对象
            AccommodationOrder order = new AccommodationOrder(idCard, roomId);
            // 假设你有 AccommodationOrder order = new AccommodationOrder(orderId, roomId);
            ServiceScheduler.orderRoomMap.put(order.getOrderId(), order.getRoomId());
            // 3. 插入订单到数据库
            int rows = orderMapper.insert(order);
            if (rows > 0) {
                // 4. 修改房间状态为 "booked"
                boolean updated = roomService.updateRoomState(roomId, "booked");
                if (!updated) {
                    System.err.println("房间状态更新失败，roomId: " + roomId);
                }

                // 5. 返回房间号
                return roomId;
            } else {
                System.err.println("订单插入失败");
                return null;
            }
        } else {
            // 没有可用房间
            return null;
        }
    }
}
