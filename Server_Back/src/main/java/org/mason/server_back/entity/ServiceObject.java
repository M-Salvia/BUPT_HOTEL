package org.mason.server_back.entity;
import java.time.LocalDateTime;

public class ServiceObject {
    public String roomId;                // 房间号
    public LocalDateTime createTime;     // 创建时间
    public Timer serviceTimer;           // 服务时长计时器
    public Timer waitTimer;              // 等待时长计时器
    public boolean isInService;          // 是否处于服务状态
    public double targetTemperature;     // 目标温度
    public double currentTemperature;    // 当前温度
    public double initialTemperature;    // 初始温度
    public FanSpeed fanSpeed;



    public void setMode(String mode) {
        this.mode = mode;
    }

    public String mode;// 当前风速

    public ServiceObject(String roomId, double targetTemp, double currentTemp ) {
        this.roomId = roomId;
        this.createTime = LocalDateTime.now();
        this.serviceTimer = new Timer();
        this.waitTimer = new Timer();
        this.isInService = false;
        this.targetTemperature = targetTemp;
        this.currentTemperature = currentTemp;
        this.initialTemperature = currentTemp;
        this.mode = "Warm";
    }

    public void setcreateTime() {
        this.createTime = LocalDateTime.now();
    }
    public void setFanSpeed(FanSpeed speed) {
        this.fanSpeed = speed;
    }

    public double getTemperatureDifference() {
        return Math.abs(currentTemperature - targetTemperature);
    }
    //setTargetTemperature
    public void setTargetTemperature(double temperature) {
        this.targetTemperature = temperature;
    }
    public void startService() {
        this.isInService = true;
        this.serviceTimer.start();
        this.waitTimer.stop();
    }

    public void stopService() {
        this.isInService = false;
        this.serviceTimer.stop();
    }

    public void startWaiting() {
        this.isInService = false;
        this.waitTimer.start();
    }
    public void stopWaiting() {
        this.isInService = false;
        this.waitTimer.stop();
    }

    public void setCurrentTemperature(double temperature) {
        this.currentTemperature = temperature;
    }

    public void resetServiceTimer() {
        this.serviceTimer.reset();
    }

    public void resetWaitTimer() {
        this.waitTimer.reset();
    }

    public void resetAllTimers() {
        resetServiceTimer();
        resetWaitTimer();
    }
    // Getters
    public String getRoomId() { return roomId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public long getServiceDuration() { return serviceTimer.getDuration(); }
    public long getWaitingDuration() { return waitTimer.getDuration(); }
    public boolean isInService() { return isInService; }
    public double getTargetTemperature() { return targetTemperature; }
    public double getCurrentTemperature() { return currentTemperature; }
    public double getInitialTemperature() { return initialTemperature; }
    public FanSpeed getFanSpeed() { return fanSpeed; }
    public String getMode() {
        return mode;
    }
}