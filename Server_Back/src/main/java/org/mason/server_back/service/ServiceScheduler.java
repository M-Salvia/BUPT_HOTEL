package org.mason.server_back.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.mason.server_back.entity.FanSpeed;
import org.mason.server_back.entity.DetailRecord;
import org.mason.server_back.entity.ServiceObject;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ServiceScheduler {
    private static final int MAX_SERVICE_QUEUE_SIZE = 3;
    public static Map<String, String> orderRoomMap = new HashMap<>();
    private Queue<ServiceObject> serviceQueue; // 服务队列
    private Queue<ServiceObject> waitingQueue; // 等待队列
    private final Map<String, RoomCounter> roomCounters = new HashMap<>();
    //ServiceScheduler scheduler = new ServiceScheduler();
    public ServiceScheduler() {
        System.out.println("ServiceScheduler 初始化完成，服务调度器已启动。");
        this.serviceQueue = new LinkedList<>();
        this.waitingQueue = new LinkedList<>();

    }
    public static String getOrderIdByRoomId(String roomId) {
        for (Map.Entry<String, String> entry : orderRoomMap.entrySet()) {
            if (entry.getValue().equals(roomId)) {
                return entry.getKey();
            }
        }
        return null; // 未找到时返回null
    }
    // 打印服务队列和等待队列信息

    // 在ServiceScheduler类中添加
    //private List<ServiceObject> lastServiceQueue = new ArrayList<>();

//    @Async
//    @Scheduled(fixedRate = 1000)
//    public synchronized void checkServiceQueueChange() {
//
//    }

    @Async
    @Scheduled(fixedRate = 1000) // 每秒执行一次
    public synchronized void updateServiceStatus() {
        System.out.println("当前服务队列：");
        for (ServiceObject service : serviceQueue) {
            System.out.println("房间ID: " + service.roomId + ", 风速: " + service.getFanSpeed() + ", 服务时长: " + service.getServiceDuration());
        }
        System.out.println("当前等待队列：");
        for (ServiceObject waiting : waitingQueue) {
            System.out.println("房间ID: " + waiting.roomId + ", 风速: " + waiting.getFanSpeed() + ", 等待时长: " + waiting.getWaitingDuration());
        }
        System.out.println(" ");
        // 打印服务队列和等待队列信息

        // 如果服务队列未满且等待队列为空，直接返回
        if (serviceQueue.size() < MAX_SERVICE_QUEUE_SIZE && waitingQueue.isEmpty()) {
            return;
        }

        // 如果服务队列未满且等待队列不为空，将等待队列中的服务对象按优先级排序后加入服务队列
        if (serviceQueue.size() < MAX_SERVICE_QUEUE_SIZE && !waitingQueue.isEmpty()) {
            // 将等待队列转换为列表以便排序
            List<ServiceObject> waitingList = new ArrayList<>(waitingQueue);
            // 按风速优先，等待时间次之的原则排序
            waitingList.sort((a, b) -> {
                int speedCompare = b.getFanSpeed().compareTo(a.getFanSpeed());
                if (speedCompare != 0) {
                    return speedCompare;
                }
                return Long.compare(b.getWaitingDuration(), a.getWaitingDuration());
            });

            // 将排序后的服务对象加入服务队列，直到服务队列满
            while (serviceQueue.size() < MAX_SERVICE_QUEUE_SIZE && !waitingList.isEmpty()) {
                ServiceObject nextService = waitingList.remove(0);
                waitingQueue.remove(nextService);
                nextService.stopWaiting();
                nextService.startService();
                serviceQueue.offer(nextService);
            }
        }

        // 如果服务队列满且等待队列不为空，检查是否需要替换
        if (serviceQueue.size() == MAX_SERVICE_QUEUE_SIZE && !waitingQueue.isEmpty()) {
            if (waitingQueue.size() > 1){
                List<ServiceObject> waitingList = new ArrayList<>(waitingQueue);
                waitingList.sort((a, b) -> {
                    int speedCompare = b.getFanSpeed().compareTo(a.getFanSpeed());
                    if (speedCompare!= 0) {
                        return speedCompare;
                    }
                    return Long.compare(b.getWaitingDuration(), a.getWaitingDuration());
                });
                //把上述的List转回到Queue
                waitingQueue = new LinkedList<>(waitingList);
            }

            //先给等待队列中的服务对象按风速优先，等待时间(降序)次之的原则排序
            for (ServiceObject waitingService : waitingQueue) {
                List<ServiceObject> serviceList = new ArrayList<>(serviceQueue);

                // 找出服务队列中风速比等待服务小的对象
                List<ServiceObject> lowerSpeedServices = serviceList.stream()
                        .filter(s -> s.getFanSpeed().compareTo(waitingService.getFanSpeed()) < 0)
                        .toList();

                if (!lowerSpeedServices.isEmpty()) {
                    // 风速低的排前面
                    // 服务时长长的排前面
                    ServiceObject toReplace = lowerSpeedServices.stream().min((a, b) -> {
                                int speedCompare = a.getFanSpeed().compareTo(b.getFanSpeed());
                                if (speedCompare != 0) {
                                    return speedCompare; // 风速低的排前面
                                }
                                return Long.compare(b.getServiceDuration(), a.getServiceDuration()); // 服务时长长的排前面
                            })
                            .get();
                    replaceService(toReplace, waitingService);
                    break;
                } else {
                    // 找出服务队列中风速相同的对象
                    if(waitingService.getWaitingDuration() < 19000)
                    {
                        continue;
                    }
                    List<ServiceObject> sameSpeedServices = serviceList.stream()
                            .filter(s -> s.getFanSpeed().equals(waitingService.getFanSpeed()))
                            .filter(s -> s.getServiceDuration() >= 19000) // 服务时间超过2分钟
                            .toList();

                    if (!sameSpeedServices.isEmpty()) {
                        // 选择服务时间最长的进行替换
                        ServiceObject toReplace = sameSpeedServices.stream()
                                .max((a, b) -> Long.compare(a.getServiceDuration(), b.getServiceDuration()))
                                .get();
                        replaceService(toReplace, waitingService);
                    }
                }
            }
        }

//        List<ServiceObject> currentList = new ArrayList<>(serviceQueue);
//
//        // 找出上一次有，这次没有的服务对象（即出队的）
//        List<ServiceObject> exitedServices = lastServiceQueue.stream()
//                .filter(last -> currentList.stream().noneMatch(now -> now.roomId.equals(last.roomId)))
//                .toList();
//
//        for (ServiceObject exited : exitedServices) {
//            // 创建并保存详单
//            DetailRecord detailRecord = detailRecordService.createDetailRecord(exited);
//            detailRecordService.printDetailRecord(detailRecord);
//            detailRecordService.save(detailRecord);
//            getOrCreateCounter(exited.roomId).detailCount++;
//        }
//
//        // 更新快照
//        lastServiceQueue = currentList;
    }

    // 用于存储每个房间的计数信息


    // 计数器内部类
    public static class RoomCounter {
        public FanSpeed fanspeed=FanSpeed.LOW;
        public double currentTemp=0;
        public double targetTemp=0;
        public String mode="Warm";
        public int detailCount = 0;
        public int tempChangeCount = 0;
        public int windChangeCount = 0;
        public int day =0;
        public void setFanSpeed(FanSpeed speed) {
            this.fanspeed = speed;
        }
        public void setCurrentTemp(double currentTemp) {
            this.currentTemp = currentTemp;
        }
        public void setTargetTemp(double targetTemp) {
            this.targetTemp = targetTemp;
        }
        public void setMode(String mode) {
            this.mode = mode;
        }
    }
    //获取RoomCounter中的fanspeed
    public String getMode(String roomId) {
        return getOrCreateCounter(roomId).mode;
    }
    public FanSpeed getFanSpeed(String roomId) {
        return getOrCreateCounter(roomId).fanspeed;
    }
    public double getCurrentTemp(String roomId) {
        return getOrCreateCounter(roomId).currentTemp;
    }
    public double getTargetTemp(String roomId) {
        return getOrCreateCounter(roomId).targetTemp;
    }
    // 获取房间的计数器，如果不存在则创建新的
    public RoomCounter getOrCreateCounter(String roomId) {
        return roomCounters.computeIfAbsent(roomId, k -> new RoomCounter());
    }
    public void removeCounter(String roomId) {
        roomCounters.remove(roomId);
    }

    // 获取计数方法
    public int getDetailCount(String roomId) {
        return getOrCreateCounter(roomId).detailCount;
    }

    public int getTempChangeCount(String roomId) {
        return getOrCreateCounter(roomId).tempChangeCount;
    }

    public int getWindChangeCount(String roomId) {
        return getOrCreateCounter(roomId).windChangeCount;
    }

    public int getDayCount(String roomId) {
        return getOrCreateCounter(roomId).day;
    }

    @Autowired
    private DetailRecordService detailRecordService;  // Spring 会自动注入 DetailRecordServiceImpl
    // 处理PowerOn请求
    public synchronized void handlePowerOn(String roomId,double currentRoomTemp) {
        // 检查是否已存在相同roomId的服务
        // if (isRoomBeingServiced(roomId)) {
        //     return;

        // }else{
        ServiceObject newService = new ServiceObject(roomId,22.0,currentRoomTemp);
        // serviceQueue.offer(newService);
        // }
        //newService.setFanSpeed(FanSpeed.MEDIUM);
        //ServiceObject newService = new ServiceObject(roomId);

        // 如果服务队列未满，直接加入服务队列
        if (serviceQueue.size() < MAX_SERVICE_QUEUE_SIZE) {
            serviceQueue.offer(newService);
            newService.startService();
            newService.setFanSpeed(FanSpeed.MEDIUM);
        } else {
            // 服务队列已满，加入等待队列
            waitingQueue.offer(newService);
            newService.startWaiting();
            newService.setFanSpeed(FanSpeed.MEDIUM);
        }
        getOrCreateCounter(roomId).day++;
    }

    // 处理ChangeSpeed请求
    public synchronized void handleChangeSpeed(String roomId, FanSpeed fanSpeed) {
        ServiceObject service = getServiceByRoomId(roomId);
        if (service != null) {

            if( service.isInService()) {
                // 增加风速改变计数
                getOrCreateCounter(roomId).windChangeCount++;
                //打印详单
                DetailRecord detailRecord = detailRecordService.createDetailRecord(service);
                detailRecordService.printDetailRecord(detailRecord);
                // 存入数据库***********
                detailRecordService.save(detailRecord);
                getOrCreateCounter(roomId).detailCount++;

                service.serviceTimer.reset(); // 重置服务计时器
                service.createTime= LocalDateTime.now(); // 更新创建时间
            }
            service.setFanSpeed(fanSpeed);
//            else {
//                // 如果服务处于等待状态，更新等待计时器
//                service.waitTimer.reset(); // 重置等待计时器
//            }
        }
    }

    // 处理ChangeTemp请求
    public synchronized void handleChangeTemp(String roomId, double targetTemp) {
        ServiceObject service = getServiceByRoomId(roomId);
        if (service!= null) {
            service.setTargetTemperature(targetTemp);
            getOrCreateCounter(roomId).tempChangeCount++;
        }
    }

    // 处理PowerOff请求
    public synchronized void handlePowerOff(String roomId) {
        ServiceObject service = getServiceByRoomId(roomId);
        if (service!= null) {
            service.stopService();

            completeService(roomId);

            //修改room数据库风速为低俗，target温度为25

        }
    }


    // 获取服务对象
    public ServiceObject getServiceByRoomId(String roomId) {
        for (ServiceObject service : serviceQueue) {
            if (service.roomId.equals(roomId)) {
                return service;
            }
        }
        for (ServiceObject service : waitingQueue) {
            if (service.roomId.equals(roomId)) {
                return service;
            }
        }
        return null;
    }
    // 检查房间是否正在被服务
    public boolean isRoomBeingServiced(String roomId) {
        return serviceQueue.stream().anyMatch(service -> service.roomId.equals(roomId));
    }

    public boolean isRoomBeingWaiting(String roomId) {
        return waitingQueue.stream().anyMatch(service -> service.roomId.equals(roomId));
    }

    // 当服务完成时调用此方法
    public synchronized void completeService(String roomId) {
        ServiceObject serviceObj = getServiceByRoomId(roomId);
        if (serviceObj != null) {
            // 创建详单服务实例
            // 创建并保存详单
            if(isRoomBeingServiced(roomId)) {
                System.out.println("powerOff powerOff powerOff powerOff called, roomId: " + roomId);
                DetailRecord detailRecord = detailRecordService.createDetailRecord(serviceObj);
                detailRecordService.printDetailRecord(detailRecord);
                // 保存到数据库
                detailRecordService.save(detailRecord);  // MyBatis-Plus 提供的保存方法
                getOrCreateCounter(roomId).detailCount++;
                // 从服务队列中移除完成的服务
                serviceQueue.removeIf(service -> service.roomId.equals(roomId));
            }
        }
    }

    // 替换服务队列中的服务对象
    private void replaceService(ServiceObject oldService, ServiceObject newService) {
        // 创建详单并打印
        // 创建并保存详单
        DetailRecord detailRecord = detailRecordService.createDetailRecord(oldService);
        detailRecordService.printDetailRecord(detailRecord);
        // 保存到数据库
        detailRecordService.save(detailRecord);  // MyBatis-Plus 提供的保存方法
        getOrCreateCounter(oldService.roomId).detailCount++;

        // 从服务队列中移除旧服务
        serviceQueue.remove(oldService);
        oldService.stopService();
        oldService.startWaiting();
        //oldService.resetWaitTimer();
        waitingQueue.offer(oldService);

        // 将新服务加入服务队列
        waitingQueue.remove(newService);
        newService.stopWaiting();
        newService.startService();
        //newService.setcreateTime();
        //oldService.resetWaitTimer();
        serviceQueue.offer(newService);
    }

    public synchronized boolean getRoomServiceStatus(String roomId) {
        ServiceObject service = getServiceByRoomId(roomId);
        if (service != null) {
            return service.isInService();
        }
        return false;  // 如果找不到该房间的服务对象，返回 false
    }

    // 温度达标时调用
    public synchronized void temp_reached(String roomId) {

        ServiceObject serviceObj = getServiceByRoomId(roomId);
        if (serviceObj != null) {
            // 创建并保存详单
            DetailRecord detailRecord = detailRecordService.createDetailRecord(serviceObj);
            detailRecordService.printDetailRecord(detailRecord);
            // 保存到数据库
            detailRecordService.save(detailRecord);  // MyBatis-Plus 提供的保存方法
            getOrCreateCounter(roomId).detailCount++;
            serviceQueue.removeIf(service -> service.roomId.equals(roomId));
        }
    }

    // 温度回升时调用
    public synchronized void temp_rising(String roomId,double targetTemp, double currentRoomTemp,FanSpeed fanSpeed) {
        ServiceObject newService = new ServiceObject(roomId, targetTemp, currentRoomTemp);
        //newService.setFanSpeed(fanSpeed);
        if (serviceQueue.size() < MAX_SERVICE_QUEUE_SIZE) {
            serviceQueue.offer(newService);
            newService.startService();
            newService.setFanSpeed(fanSpeed);
        } else {
            waitingQueue.offer(newService);
            newService.startWaiting();
            newService.setFanSpeed(fanSpeed);
        }
    }

    // 定时更新服务状态




}