package org.mason.server_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mason.server_back.entity.Room;
import org.mason.server_back.mapper.RoomMapper;
import org.mason.server_back.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private RoomMapper roomMapper;  // 注入 RoomMapper，供查询和更新房间使用

    /**
     * 查找空闲的房间（根据房型和状态）
     *
     * @param type 房间类型（如：1 表示单人房，2 表示双人房，等等）
     * @return 空闲房间的 ID，如果没有找到空闲房间，则返回 null
     */
    @Override
    public String findAvailableRoom(int type) {
        // 使用 MyBatis-Plus 的 QueryWrapper 构造查询条件
        List<Room> rooms = roomMapper.selectList(new QueryWrapper<Room>()
                .eq("roomType", type)
                .eq("roomState", "available"));

        if (!rooms.isEmpty()) {
            return rooms.get(0).getRoomId();
        } else {
            return null;
        }

    }

    /**
     * 更新房间状态（例如：从空闲变为已预定，或者其他状态）
     *
     * @param roomId 房间ID
     * @param newState 新状态（如：available, booked, etc.）
     * @return 更新是否成功
     */
    @Override
    public boolean updateRoomState(String roomId, String newState) {
        Room room = roomMapper.selectById(roomId);  // 根据房间 ID 获取房间信息
        if (room != null) {
            room.setState(newState);  // 设置新的状态
            return this.updateById(room);  // 更新房间状态
        }
        return false;  // 如果找不到房间，则返回 false
    }
}
