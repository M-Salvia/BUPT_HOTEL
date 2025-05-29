import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

enum FanSpeed {
    LOW(1, "低速", 0.5f),
    MEDIUM(2, "中速", 1.0f),
    HIGH(3, "高速", 1.5f);

    private final int priority;
    private final String description;
    private final float feeRate;

    FanSpeed(int priority, String description, float feeRate) {
        this.priority = priority;
        this.description = description;
        this.feeRate = feeRate;
    }

    public int getPriority() { return priority; }
    public String getDescription() { return description; }
    public float getFeeRate() { return feeRate; }
}

// 请求对象
class RequestObject {
    private String requestId;
    private String roomId;
    private float targetTemp;
    private FanSpeed fanSpeed;
    private Date startTime;
    private int waitTime;
    private float currentTemp;

    public RequestObject(String roomId, float targetTemp, FanSpeed fanSpeed, float currentTemp) {
        this.requestId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.targetTemp = targetTemp;
        this.fanSpeed = fanSpeed;
        this.currentTemp = currentTemp;
        this.startTime = new Date();
        this.waitTime = 0;
    }

    public void initializeRequest(String roomId, float targetTemp, FanSpeed fanSpeed) {
        this.roomId = roomId;
        this.targetTemp = targetTemp;
        this.fanSpeed = fanSpeed;
        this.startTime = new Date();
    }

    public void releaseRequest() {
        this.requestId = null;
        this.roomId = null;
        this.startTime = null;
    }

    public int getRequestTime() {
        if (startTime == null) return 0;
        return (int) ((new Date().getTime() - startTime.getTime()) / 60000);
    }

    public FanSpeed getFanSpeed() { return fanSpeed; }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getRoomId() { return roomId; }
    public float getTargetTemp() { return targetTemp; }
    public Date getStartTime() { return startTime; }
    public int getWaitTime() { return waitTime; }
    public void setWaitTime(int waitTime) { this.waitTime = waitTime; }
    public float getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(float currentTemp) { this.currentTemp = currentTemp; }
}

// 服务对象
class ServiceObject {
    private String serviceId;
    private String roomId;
    private float targetTemp;
    private FanSpeed fanSpeed;
    private Date startTime;
    private Date endTime;
    private int serveTime;
    private float currentTemp;
    private float fee;
    private float initialTemp;

    public ServiceObject(String roomId, float targetTemp, FanSpeed fanSpeed, float currentTemp) {
        this.serviceId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.targetTemp = targetTemp;
        this.fanSpeed = fanSpeed;
        this.currentTemp = currentTemp;
        this.startTime = new Date();
        this.fee = 0.0f;
        this.initialTemp = currentTemp;
    }

    public void initializeService(String roomId, float targetTemp, FanSpeed fanSpeed) {
        this.roomId = roomId;
        this.targetTemp = targetTemp;
        this.fanSpeed = fanSpeed;
        this.startTime = new Date();
        this.fee = 0.0f;
    }

    public void releaseService() {
        this.endTime = new Date();
        this.serveTime = getServiceTime();
    }

    public void updateCurrentTemp(float targetTemp) {
        // 温度变化逻辑，包括回温机制
        if (this.currentTemp < targetTemp) {
            this.currentTemp = Math.min(targetTemp, this.currentTemp + getChangeRate());
        } else if (this.currentTemp > targetTemp) {
            this.currentTemp = Math.max(targetTemp, this.currentTemp - getChangeRate());
        }
        // 自动回温机制（每分钟回温1度）
        if (this.currentTemp < this.initialTemp) {
            this.currentTemp = Math.min(this.initialTemp, this.currentTemp + 1.0f); // 每分钟回温1度
        }
    }

    private float getChangeRate() {
        switch (fanSpeed) {
            case HIGH: return 1.0f; // 高速风，每分钟降低1度
            case MEDIUM: return 0.5f; // 中速风，每两分钟降低1度
            case LOW: return 0.33f; // 低速风，每三分钟降低1度
            default: return 0;
        }
    }

    public int getServiceTime() {
        if (startTime == null) return 0;
        Date endTimeToUse = (endTime != null) ? endTime : new Date();

        // 计算毫秒差
        long timeDifferenceMillis = endTimeToUse.getTime() - startTime.getTime();

        // 转换为秒并四舍五入
        long timeDifferenceSeconds = Math.round(timeDifferenceMillis / 1000.0); // 转换为秒并四舍五入
        Date a = startTime;
        Date b = endTimeToUse;

        // 返回以秒为单位的时长
        return (int) timeDifferenceSeconds;
    }

    public float calculateFee() {
        int minutes = getServiceTime();
        this.fee = minutes * fanSpeed.getFeeRate(); // 计算费用
        return this.fee;
    }

    public float getFee() { return fee; }

    public boolean isTargetReached() {
        return Math.abs(currentTemp - targetTemp) < 0.5f;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public String getRoomId() { return roomId; }
    public float getTargetTemp() { return targetTemp; }
    public FanSpeed getFanSpeed() { return fanSpeed; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public float getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(float currentTemp) { this.currentTemp = currentTemp; }
}

// 调度结果枚举
enum ScheduleAction {
    ALLOCATE_SERVICE,    // 直接分配服务
    PREEMPT_SERVICE,     // 抢占服务
    TIME_SLICE_SCHEDULE, // 时间片调度
    ADD_TO_WAIT          // 加入等待队列
}

// 调度结果类
class ScheduleResult {
    public final ScheduleAction action;
    public final ServiceQueueItem preemptedServiceItem;

    public ScheduleResult(ScheduleAction action, ServiceQueueItem preemptedServiceItem) {
        this.action = action;
        this.preemptedServiceItem = preemptedServiceItem;
    }
}


// 调度对象
class ScheduleObject {
    private String scheduleId;
    private int maxServiceCount;
    private int maxWaitCount;
    private List<ServiceQueueItem> serviceQueue;
    private List<WaitQueueItem> waitQueue;
    private int timeSlice;
    private int totalRooms;
    private List<DetailRecord> detailRecords;
    private ScheduledExecutorService scheduler;

    public ScheduleObject() {
        this.scheduleId = UUID.randomUUID().toString();
        this.maxServiceCount = 3;
        this.maxWaitCount = 2;
        this.serviceQueue = new ArrayList<>();
        this.waitQueue = new ArrayList<>();
        this.timeSlice = 5; // 120秒
        this.totalRooms = 5;
        this.detailRecords = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(2);

        // 启动定时任务更新状态
        startStatusUpdateTask();
    }

    private void startStatusUpdateTask() {
        // 每秒更新一次状态
        scheduler.scheduleAtFixedRate(() -> {
            updateServiceStatus();
            processWaitQueue();
        }, 1, 1, TimeUnit.SECONDS);
    }

    public synchronized boolean receiveRequest(RequestObject request) {
        System.out.println("收到房间 " + request.getRoomId() + " 的请求，风速: " +
                request.getFanSpeed().getDescription() + "，目标温度: " +
                request.getTargetTemp() + "°C");

        return scheduleRequest(request);
    }

    private boolean scheduleRequest(RequestObject request) {
        // 如果服务队列未满，直接分配服务
        if (serviceQueue.size() < maxServiceCount) {
            return allocateService(request);
        }

        // 服务队列已满，执行调度策略
        return executeScheduleStrategy(request);
    }

    private boolean executeScheduleStrategy(RequestObject request) {
        // 检查优先级调度
        ScheduleResult priorityResult = checkPrioritySchedule(request);

        if (priorityResult.action == ScheduleAction.ALLOCATE_SERVICE) {
            return allocateService(request);
        } else if (priorityResult.action == ScheduleAction.PREEMPT_SERVICE) {
            // 抢占服务
            preemptService(priorityResult.preemptedServiceItem, request);
            return true;
        } else if (priorityResult.action == ScheduleAction.TIME_SLICE_SCHEDULE) {
            // 时间片调度
            return checkTimeSliceSchedule(request);
        } else {
            // 加入等待队列
            return addToWaitQueue(request);
        }
    }

    private ScheduleResult checkPrioritySchedule(RequestObject request) {
        FanSpeed requestSpeed = request.getFanSpeed();

        // 找出所有低于请求风速的服务对象
        List<ServiceQueueItem> lowerPriorityServices = new ArrayList<>();
        List<ServiceQueueItem> samePriorityServices = new ArrayList<>();

        for (ServiceQueueItem item : serviceQueue) {
            if (item.getFanSpeed().getPriority() < requestSpeed.getPriority()) {
                lowerPriorityServices.add(item);
            } else if (item.getFanSpeed().getPriority() == requestSpeed.getPriority()) {
                samePriorityServices.add(item);
            }
        }

        if (!lowerPriorityServices.isEmpty()) {
            // 有低优先级服务，执行抢占
            ServiceQueueItem toPreempt = selectServiceToPreempt(lowerPriorityServices);
            return new ScheduleResult(ScheduleAction.PREEMPT_SERVICE, toPreempt);
        } else if (!samePriorityServices.isEmpty()) {
            // 有相同优先级服务，启动时间片调度
            return new ScheduleResult(ScheduleAction.TIME_SLICE_SCHEDULE, null);
        } else {
            // 所有服务都是高优先级，需要等待
            return new ScheduleResult(ScheduleAction.ADD_TO_WAIT, null);
        }
    }

    private ServiceQueueItem selectServiceToPreempt(List<ServiceQueueItem> candidates) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        // 找出风速最低的，如果有多个风速相同的则选择服务时长最大的
        Map<FanSpeed, List<ServiceQueueItem>> speedGroups = new HashMap<>();
        for (ServiceQueueItem item : candidates) {
            speedGroups.computeIfAbsent(item.getFanSpeed(), k -> new ArrayList<>()).add(item);
        }

        // 找到最低风速
        FanSpeed lowestSpeed = speedGroups.keySet().stream()
                .min(Comparator.comparing(FanSpeed::getPriority))
                .orElse(null);

        List<ServiceQueueItem> lowestSpeedItems = speedGroups.get(lowestSpeed);
        if (lowestSpeedItems.size() == 1) {
            return lowestSpeedItems.get(0);
        }

        // 选择服务时长最长的
        return lowestSpeedItems.stream()
                .max(Comparator.comparing(ServiceQueueItem::getServeTime))
                .orElse(lowestSpeedItems.get(0));
    }

    private void preemptService(ServiceQueueItem preemptedItem, RequestObject newRequest) {
        System.out.println("抢占房间 " + preemptedItem.getRoomId() + " 的服务，分配给房间 " +
                newRequest.getRoomId());

        // 释放被抢占的服务
        ServiceObject preemptedService = preemptedItem.getServiceObject();
        preemptedService.releaseService();

        // 生成被抢占服务的详单
        generateDetailRecord(preemptedService, preemptedItem.getServeTime());

        // 移除被抢占的服务
        serviceQueue.remove(preemptedItem);

        // 将被抢占的房间加入等待队列
        RequestObject preemptedRequest = new RequestObject(
                preemptedItem.getRoomId(),
                preemptedItem.getTargetTemp(),
                preemptedItem.getFanSpeed(),
                preemptedItem.getCurrentTemp()
        );
        addToWaitQueue(preemptedRequest);

        // 为新请求分配服务
        allocateService(newRequest);
    }

    private boolean checkTimeSliceSchedule(RequestObject request) {
        System.out.println("启动时间片调度，房间 " + request.getRoomId() + " 加入等待队列");

        // 加入等待队列并分配120秒等待时间
        WaitQueueItem waitItem = new WaitQueueItem(request);
        waitItem.setRemainingWaitTime(timeSlice); // 单位为秒

        if (waitQueue.size() < maxWaitCount) {
            waitQueue.add(waitItem);
            return true;
        }

        return false; // 等待队列已满
    }

    private boolean allocateService(RequestObject request) {
        ServiceObject service = new ServiceObject(
                request.getRoomId(),
                request.getTargetTemp(),
                request.getFanSpeed(),
                request.getCurrentTemp()
        );

        ServiceQueueItem serviceItem = new ServiceQueueItem(service);
        serviceQueue.add(serviceItem);

        System.out.println("为房间 " + request.getRoomId() + " 分配服务对象: " +
                service.getServiceId());
        return true;
    }

    private boolean addToWaitQueue(RequestObject request) {
        if (waitQueue.size() >= maxWaitCount) {
            System.out.println("等待队列已满，无法处理房间 " + request.getRoomId() + " 的请求");
            return false;
        }

        WaitQueueItem waitItem = new WaitQueueItem(request);
        waitQueue.add(waitItem);

        System.out.println("房间 " + request.getRoomId() + " 加入等待队列");
        return true;
    }

    public void updateServiceStatus() {
        List<ServiceQueueItem> completedServices = new ArrayList<>();

        for (ServiceQueueItem item : serviceQueue) {
            item.updateServeTime();
            item.calculateCurrentFee();

            ServiceObject service = item.getServiceObject();
            service.updateCurrentTemp(service.getTargetTemp());

            // 检查是否达到目标温度
            if (service.isTargetReached()) {
                System.out.println("房间 " + item.getRoomId() + " 达到目标温度，服务完成");
                completedServices.add(item);
            }
        }

        // 处理完成的服务
        for (ServiceQueueItem completedItem : completedServices) {
            releaseService(completedItem.getServiceId());
        }
    }

    public void releaseService(String serviceId) {
        ServiceQueueItem itemToRemove = null;

        for (ServiceQueueItem item : serviceQueue) {
            if (item.getServiceId().equals(serviceId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            ServiceObject service = itemToRemove.getServiceObject();
            service.releaseService();

            // ��成详单
            generateDetailRecord(service, itemToRemove.getServeTime());

            // 从服务队列移除
            serviceQueue.remove(itemToRemove);

            System.out.println("释放房间 " + itemToRemove.getRoomId() + " 的服务对象");

            // 处理等待队列
            processWaitQueueAfterServiceRelease();
        }
    }

    private void processWaitQueueAfterServiceRelease() {
        if (!waitQueue.isEmpty() && serviceQueue.size() < maxServiceCount) {
            // 找到等待时间最长的请求
            WaitQueueItem longestWaitItem = waitQueue.stream()
                    .max(Comparator.comparing(WaitQueueItem::getActualWaitTime))
                    .orElse(null);

            if (longestWaitItem != null) {
                waitQueue.remove(longestWaitItem);
                allocateService(longestWaitItem.getRequestObject());

                System.out.println("从等待队列为房间 " + longestWaitItem.getRoomId() + " 分配服务");
            }
        }
    }

    public void processWaitQueue() {
        List<WaitQueueItem> expiredItems = new ArrayList<>();

        for (WaitQueueItem item : waitQueue) {
            item.updateWaitTime();

            if (item.isWaitTimeExpired()) {
                expiredItems.add(item);
            }
        }

        // 处理超时的等待请求
        for (WaitQueueItem expiredItem : expiredItems) {
//            if (serviceQueue.size() < maxServiceCount) {
                waitQueue.remove(expiredItem);

                // 执行时间片调度的抢占逻辑
                ServiceQueueItem longestServiceItem = serviceQueue.stream()
                        .max(Comparator.comparing(ServiceQueueItem::getServeTime))
                        .orElse(null);

                if (longestServiceItem != null) {
                    preemptService(longestServiceItem, expiredItem.getRequestObject());
                }
//            }
        }
    }

    private void generateDetailRecord(ServiceObject service, int serveTimeInSeconds) {
        DetailRecord record = new DetailRecord(
                service.getRoomId(),
                new Date(), // 请求时间（简化）
                service.getStartTime(),
                service.getEndTime(),
                serveTimeInSeconds, // 以秒为单位的服务时长
                service.getFanSpeed(),
                service.getFee(),
                service.getCurrentTemp(), // 简化，实际应该记录初始温度
                service.getTargetTemp(),
                service.getCurrentTemp(),
                serveTimeInSeconds // 这里也传秒，保持一致
        );

        detailRecords.add(record);
        System.out.println("生成详单: " + record.toString());
    }

    public void generateDetailRecord(String roomId) {
        // 为指定房间生成详单的公共方法
        for (ServiceQueueItem item : serviceQueue) {
            if (item.getRoomId().equals(roomId)) {
                generateDetailRecord(item.getServiceObject(), 0);
                break;
            }
        }
    }

    public void printStatus() {
        System.out.println("\n=== 系统状态 ===");
        System.out.println("服务队列 (" + serviceQueue.size() + "/" + maxServiceCount + "):");
        for (ServiceQueueItem item : serviceQueue) {
            System.out.println("  房间: " + item.getRoomId() +
                    ", 风速: " + item.getFanSpeed().getDescription() +
                    ", 服务时长: " + item.getServeTime() + "秒" +
                    ", 当前温度: " + item.getCurrentTemp() + "°C" +
                    ", 费用: " + item.getCurrentFee() + "元");
        }

        System.out.println("等待队列 (" + waitQueue.size() + "/" + maxWaitCount + "):");
        for (WaitQueueItem item : waitQueue) {
            System.out.println("  房间: " + item.getRoomId() +
                    ", 风速: " + item.getFanSpeed().getDescription() +
                    ", 剩余等待时间: " + item.getRemainingWaitTime() + "秒");
        }
        System.out.println("================\n");
    }

    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // Getters
    public List<DetailRecord> getDetailRecords() { return new ArrayList<>(detailRecords); }
    public List<ServiceQueueItem> getServiceQueue() { return new ArrayList<>(serviceQueue); }
    public List<WaitQueueItem> getWaitQueue() { return new ArrayList<>(waitQueue); }
}


// 服务队列项
class ServiceQueueItem {
    private String roomId;
    private String serviceId;
    private FanSpeed fanSpeed;
    private Date startTime;
    private int serveTime;
    private float targetTemp;
    private float currentTemp;
    private float feeRate;
    private float currentFee;
    private ServiceObject serviceObject;

    public ServiceQueueItem(ServiceObject serviceObject) {
        this.serviceObject = serviceObject;
        this.roomId = serviceObject.getRoomId();
        this.serviceId = serviceObject.getServiceId();
        this.fanSpeed = serviceObject.getFanSpeed();
        this.startTime = serviceObject.getStartTime();
        this.targetTemp = serviceObject.getTargetTemp();
        this.currentTemp = serviceObject.getCurrentTemp();
        this.feeRate = fanSpeed.getFeeRate();
    }

    public void updateServeTime() {
        // 服务时长每秒更新一次，单位为秒
        if (serviceObject.getStartTime() != null) {
            Date endTimeToUse = new Date();
            this.serveTime = (int) ((endTimeToUse.getTime() - serviceObject.getStartTime().getTime()) / 1000); // ��
        } else {
            this.serveTime = 0;
        }
    }

    public void calculateCurrentFee() {
        this.currentFee = serviceObject.calculateFee();
    }

    public boolean isTargetReached() {
        return serviceObject.isTargetReached();
    }

    // Getters
    public String getRoomId() { return roomId; }
    public String getServiceId() { return serviceId; }
    public FanSpeed getFanSpeed() { return fanSpeed; }
    public Date getStartTime() { return startTime; }
    public int getServeTime() { return serveTime; }
    public float getTargetTemp() { return targetTemp; }
    public float getCurrentTemp() { return currentTemp; }
    public float getFeeRate() { return feeRate; }
    public float getCurrentFee() { return currentFee; }
    public ServiceObject getServiceObject() { return serviceObject; }
}

// 等待队列项
class WaitQueueItem {
    private String roomId;
    private FanSpeed fanSpeed;
    private float currentTemp;
    private float targetTemp;
    private Date requestTime;
    private int waitTime;
    private int remainingWaitTime;
    private RequestObject requestObject;

    public WaitQueueItem(RequestObject requestObject) {
        this.requestObject = requestObject;
        this.roomId = requestObject.getRoomId();
        this.fanSpeed = requestObject.getFanSpeed();
        this.currentTemp = requestObject.getCurrentTemp();
        this.targetTemp = requestObject.getTargetTemp();
        this.requestTime = requestObject.getStartTime();
        this.waitTime = 5; // 默认等待5秒
        this.remainingWaitTime = 5;
    }

    public void updateWaitTime() {
        this.remainingWaitTime = Math.max(0, this.remainingWaitTime - 1);
    }

    public boolean isWaitTimeExpired() {
        return remainingWaitTime <= 0;
    }

    public int getActualWaitTime() {
        if (requestTime == null) return 0;
        return (int) ((new Date().getTime() - requestTime.getTime()) / 1000); // 秒
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public FanSpeed getFanSpeed() { return fanSpeed; }
    public float getCurrentTemp() { return currentTemp; }
    public float getTargetTemp() { return targetTemp; }
    public Date getRequestTime() { return requestTime; }
    public int getWaitTime() { return waitTime; }
    public int getRemainingWaitTime() { return remainingWaitTime; }
    public RequestObject getRequestObject() { return requestObject; }
    public void setRemainingWaitTime(int remainingWaitTime) { this.remainingWaitTime = remainingWaitTime; }
}

// 详单记录
class DetailRecord {
    private String roomId;
    private Date requestTime;
    private Date serviceStartTime;
    private Date serviceEndTime;
    private int serviceDuration;
    private FanSpeed fanSpeed;
    private float currentFee;
    private float feeRate;
    private float initialTemp;
    private float targetTemp;
    private float finalTemp;
    private int waitTime;
    private float totalCost;

    public DetailRecord(String roomId, Date requestTime, Date serviceStartTime,
                        Date serviceEndTime, int serviceDuration, FanSpeed fanSpeed,
                        float currentFee, float initialTemp, float targetTemp,
                        float finalTemp, int waitTime) {
        this.roomId = roomId;
        this.requestTime = requestTime;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.serviceDuration = serviceDuration;
        this.fanSpeed = fanSpeed;
        this.currentFee = currentFee;
        this.feeRate = fanSpeed.getFeeRate();
        this.initialTemp = initialTemp;
        this.targetTemp = targetTemp;
        this.finalTemp = finalTemp;
        this.waitTime = waitTime;
        this.totalCost = currentFee;
    }

    @Override
    public String toString() {
        return String.format("房间号: %s, 请求时间: %s, 服务开始: %s, 服务结束: %s, " +
                        "服务时长: %d秒, 风速: %s, 当前费用: %.2f元, 费率: %.2f元/秒, " +
                        "初始温度: %.1f°C, 目标温度: %.1f°C, 最终温度: %.1f°C, " +
                        "等待时长: %d秒, 总费用: %.2f元",
                roomId, requestTime, serviceStartTime, serviceEndTime,
                serviceDuration, fanSpeed.getDescription(), currentFee, feeRate,
                initialTemp, targetTemp, finalTemp, waitTime, totalCost);
    }

    // Getters
    public String getRoomId() { return roomId; }
    public Date getRequestTime() { return requestTime; }
    public Date getServiceStartTime() { return serviceStartTime; }
    public Date getServiceEndTime() { return serviceEndTime; }
    public int getServiceDuration() { return serviceDuration; }
    public FanSpeed getFanSpeed() { return fanSpeed; }
    public float getCurrentFee() { return currentFee; }
    public float getFeeRate() { return feeRate; }
    public float getInitialTemp() { return initialTemp; }
    public float getTargetTemp() { return targetTemp; }
    public float getFinalTemp() { return finalTemp; }
    public int getWaitTime() { return waitTime; }
    public float getTotalCost() { return totalCost; }
}
