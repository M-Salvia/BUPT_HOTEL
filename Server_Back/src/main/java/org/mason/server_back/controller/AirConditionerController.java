// 文件：Server_Back/src/main/java/org/mason/server_back/controller/AirConditionerController.java
package org.mason.server_back.controller;

import aj.org.objectweb.asm.TypePath;
import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.service.ServiceScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class AirConditionerController {

    @Autowired
    private ServiceScheduler schedulerService;

//    @PostMapping("/poweron")
//    public void powerOn(@RequestParam String roomId, @RequestParam double currentRoomTemp) {
//        System.out.println("powerOn called, roomId: " + roomId + ", currentRoomTemp: " + currentRoomTemp);
//        schedulerService.handlePowerOn(roomId, currentRoomTemp);
//    }

    @PostMapping("/poweron")
    public void powerOn(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");

        Object currentRoomTempObj = payload.get("currentRoomTemp");

        double currentRoomTemp = (currentRoomTempObj instanceof Integer) ? ((Integer) currentRoomTempObj).doubleValue() : (Double) currentRoomTempObj;

        System.out.println("powerOn called, roomId: " + roomId + ", currentRoomTemp: " + currentRoomTemp);
        schedulerService.handlePowerOn(roomId, currentRoomTemp);
    }

    @PostMapping("/changespeed")
    public void changeSpeed(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");
        String fanSpeedString = (String) payload.get("fanSpeed");

        // 将字符串转换为 FanSpeed 枚举
        FanSpeed fanSpeed = FanSpeed.fromString(fanSpeedString);

        System.out.println("changeSpeed called, roomId: " + roomId + ", fanSpeed: " + fanSpeed);
        schedulerService.handleChangeSpeed(roomId, fanSpeed);
    }



    @PostMapping("/changetemp")
    public void changeTemp(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");
        // 强制转换 targetTemp 为 double 类型
        Object targetTempObj = payload.get("targetTemp");
        double targetTemp = (targetTempObj instanceof Integer) ? ((Integer) targetTempObj).doubleValue() : (Double) targetTempObj;

        System.out.println("changeTemp called, roomId: " + roomId + ", targetTemp: " + targetTemp);
        schedulerService.handleChangeTemp(roomId, targetTemp);
    }



    @PostMapping("/poweroff")
    public void powerOff(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");
        System.out.println("powerOff called, roomId: " + roomId);
        schedulerService.handlePowerOff(roomId);
    }


    @PostMapping("/temp-reached")
    public void handleTempReached(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");
        System.out.println("handleTempReached called, roomId: " + roomId);
        schedulerService.temp_reached(roomId);
    }


    @PostMapping("/temp-rising")
    public void handleTempRising(@RequestBody Map<String, Object> payload) {
        String roomId = (String) payload.get("roomId");
        Object currentRoomTempObj = payload.get("currentRoomTemp");
        double currentRoomTemp = (currentRoomTempObj instanceof Integer) ? ((Integer) currentRoomTempObj).doubleValue() : (Double) currentRoomTempObj;
        Object targetTempObj = payload.get("targetTemp");
        double targetTemp = (targetTempObj instanceof Integer) ? ((Integer) targetTempObj).doubleValue() : (Double) targetTempObj;
        String fanSpeedString = (String) payload.get("fanSpeed");
        // 将字符串转换为 FanSpeed 枚举
        FanSpeed fanSpeed = FanSpeed.fromString(fanSpeedString);
        System.out.println("handleTempRising called, roomId: " + roomId + ", targetTemp: " + targetTemp + ", currentRoomTemp: " + currentRoomTemp + ", fanSpeed: " + fanSpeed);
        schedulerService.temp_rising(roomId, targetTemp, currentRoomTemp, fanSpeed);
    }



    @GetMapping(value = "/room/service-status/stream/{roomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamServiceStatus(@PathVariable String roomId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 创建一个定时任务，每秒发送一次状态更新
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                boolean status = schedulerService.getRoomServiceStatus(roomId);
                emitter.send(SseEmitter.event()
                        .name("service-status")
                        .data(status));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
        return emitter;
    }
}