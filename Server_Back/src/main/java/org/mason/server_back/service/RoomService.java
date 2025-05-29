package org.mason.server_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mason.server_back.entity.Room;

public interface RoomService extends IService<Room> {

    /**
     * 查找空闲的房间（根据房型和状态）
     *
     * @param type 房间类型（如：1 表示单人房，2 表示双人房，等等）
     * @return 空闲房间的 ID，如果没有找到空闲房间，则返回 null
     */
    String findAvailableRoom(int type);

    /**
     * 更新房间状态（例如：从空闲变为已预定，或者其他状态）
     *
     * @param roomId 房间ID
     * @param newState 新状态（如：available, booked, etc.）
     * @return 更新是否成功
     */
    boolean updateRoomState(String roomId, String newState);
}
