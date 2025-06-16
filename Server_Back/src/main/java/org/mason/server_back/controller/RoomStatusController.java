package org.mason.server_back.controller;

import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.entity.RoomStatusDTO;
import org.mason.server_back.entity.ServiceObject;
import org.mason.server_back.service.DetailRecordService;
import org.mason.server_back.service.ServiceScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/room-status")
public class RoomStatusController {

    @Autowired
    private ServiceScheduler schedulerService;
    
    @Autowired
    private DetailRecordService detailRecordService;

    @GetMapping(value = "/stream/{roomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRoomStatus(@PathVariable String roomId) {
        System.out.println("dd");
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);


        // 创建一个定时任务，每秒发送一次状态更新
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                ServiceObject service = schedulerService.getServiceByRoomId(roomId);
                RoomStatusDTO status = new RoomStatusDTO();

                status.setCurrentTemp(schedulerService.getOrCreateCounter(roomId).currentTemp);
                status.setMode(schedulerService.getOrCreateCounter(roomId).mode);
                status.setTargetTemp(schedulerService.getOrCreateCounter(roomId).targetTemp);
                status.setDetailCount(schedulerService.getDetailCount(roomId));
                status.setTempChangeCount(schedulerService.getTempChangeCount(roomId));
                status.setWindChangeCount(schedulerService.getWindChangeCount(roomId));
                status.setIsWaiting(schedulerService.isRoomBeingWaiting(roomId));
                status.setWindSpeed(schedulerService.getOrCreateCounter(roomId).fanspeed);
                status.setDayCount(schedulerService.getDayCount(roomId));



































                
                if (service != null) {
                    status.setIsOn(service.isInService());
                    status.setCurrentFee(detailRecordService.calculateCost(service).doubleValue());
                    status.setServiceTime(service.serviceTimer.getDuration());
                    status.setWaitingTime(service.waitTimer.getDuration());
                }else{
                    status.setIsOn(false);
                    status.setCurrentFee(0);
                    status.setServiceTime(0);
                    status.setWaitingTime(0);
                }

                emitter.send(SseEmitter.event()
                        .name("room-status")
                        .data(status));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        return emitter;
    }

    // DTO类用于传输数据

}