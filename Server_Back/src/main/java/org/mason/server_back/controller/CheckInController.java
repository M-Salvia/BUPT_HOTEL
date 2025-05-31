package org.mason.server_back.controller;

import org.mason.server_back.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckInController {

    private final CheckInService checkInService;

    // 构造函数注入 CheckInService
    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/api/checkin")
    public ResponseEntity<String> createAccommodationOrder(@RequestParam String idCard, @RequestParam int type) {
        // 调用服务层的 createAccommodationOrder 方法查找空闲房间
        System.out.println("!@#$" );
        String roomID = checkInService.createAccommodationOrder(idCard, type);
        System.out.println("请求的idCard: " + idCard + ", type: " + type);
        System.out.println("查询到的空闲房间ID: " + roomID);
        if (roomID != null) {
            // 如果找到了空闲房间，返回房间 ID，HTTP 状态码 201 Created
            return new ResponseEntity<>(roomID, HttpStatus.CREATED);
        } else {
            // 如果没有找到空闲房间，返回 HTTP 状态码 400 Bad Request
            return new ResponseEntity<>("No available room found for the given type.", HttpStatus.BAD_REQUEST);
        }
    }
}
