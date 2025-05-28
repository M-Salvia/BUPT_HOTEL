public class Scheduler {
    private ServiceQueue serviceQueue = new ServiceQueue();
    private WaitQueue waitQueue = new WaitQueue();
    private int maxServiceObjects = 3;  // 最大服务对象数
    private int currentServiceCount = 0;

    public void powerOn(String roomId, float targetTemp, FanSpeed fanSpeed) {
        if (currentServiceCount < maxServiceObjects) {
            ServiceObject newServiceObject = new ServiceObject("Service" + roomId, roomId, targetTemp, fanSpeed);
            serviceQueue.addServiceRecord(newServiceObject);
            currentServiceCount++;
        } else {
            // 启动调度策略
            handlePriorityScheduling(roomId, targetTemp, fanSpeed);
        }
    }

    private void handlePriorityScheduling(String roomId, float targetTemp, FanSpeed fanSpeed) {
        ServiceObject lowestFanSpeedService = serviceQueue.findSmallAndLongestService();

        if (lowestFanSpeedService != null && lowestFanSpeedService.getFanSpeed().ordinal() < fanSpeed.ordinal()) {
            // 优先级调度：释放风速较低的服务对象
            serviceQueue.removeServiceRecord(lowestFanSpeedService.getRoomId());
            waitQueue.addWaitRecord(roomId, fanSpeed, 25.0f, 120);  // 假设当前温度为25°C，等待时间为120秒
        }
    }

    public void checkServiceObjectCount() {
        if (currentServiceCount >= maxServiceObjects) {
            // 执行调度策略
        }
    }
}
