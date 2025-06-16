package org.mason.server_back.controller;

import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.entity.ServiceObject;
import org.mason.server_back.service.ServiceScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
public class TempratureController {

    @Autowired
    private ServiceScheduler schedulerService;

    @PostMapping("/{roomId}")
    public void updateStatus(
            @PathVariable String roomId,
            @RequestBody StatusRequest request
    ) {
        // 这里可以处理 mode 和 currentRoomTemp
        System.out.println("Room: " + roomId + ", Mode: " + request.getMode() + ", Temp: " + request.getCurrentRoomTemp() + ", FanSpeed:" + request.getFanSpeed());
        //把currentRoomTemp赋值给对应roomid的service中的currentTemp
        schedulerService.getOrCreateCounter(roomId).setMode(request.getMode());
        schedulerService.getOrCreateCounter(roomId).setCurrentTemp(request.getCurrentRoomTemp());
        schedulerService.getOrCreateCounter(roomId).setTargetTemp(request.targetTemperature);

        String fanSpeedString = String.valueOf(request.fanSpeed);
        // 将字符串转换为 FanSpeed 枚举
        FanSpeed fanSpeed = FanSpeed.fromString(fanSpeedString);
        schedulerService.getOrCreateCounter(roomId).setFanSpeed(fanSpeed);

        // 获取对应房间的ServiceObject
        ServiceObject service = schedulerService.getServiceByRoomId(roomId);
        if (service != null) {
            service.setCurrentTemperature(request.getCurrentRoomTemp());
            service.setMode(request.getMode());
        }
        //将RoomCounter里的对应roomid的

    }

    // DTO类
    public static class StatusRequest {
        private String mode;
        private double currentRoomTemp;
        private double targetTemperature;
        private String fanSpeed;

        public double getTargetTemperature() {
            return targetTemperature;
        }

        public void setTargetTemperature(double targetTemperature) {
            this.targetTemperature = targetTemperature;
        }

        public String

        getFanSpeed() {
            return fanSpeed;
        }

        public void setFanSpeed(String fanSpeed) {
            this.fanSpeed = fanSpeed;
        }

        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public double getCurrentRoomTemp() { return currentRoomTemp; }
        public void setCurrentRoomTemp(double currentRoomTemp) { this.currentRoomTemp = currentRoomTemp; }

    }
}