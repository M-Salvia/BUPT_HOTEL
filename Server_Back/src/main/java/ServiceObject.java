import java.util.Date;

public class ServiceObject {
    private String serviceId;
    private String roomId;
    private float targetTemp;
    private FanSpeed fanSpeed;
    private Date startTime;
    private ServiceStatus status;

    public ServiceObject(String serviceId, String roomId, float targetTemp, FanSpeed fanSpeed) {
        this.serviceId = serviceId;
        this.roomId = roomId;
        this.targetTemp = targetTemp;
        this.fanSpeed = fanSpeed;
        this.startTime = new Date();
        this.status = ServiceStatus.SERVING;
    }

    public void terminateService() {
        this.status = ServiceStatus.FINISHED;
    }

    public void updateTargetTemp(float targetTemp) {
        this.targetTemp = targetTemp;
    }

    public void updateFanSpeed(FanSpeed fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public long getServiceTime() {
        return (new Date().getTime() - startTime.getTime()) / 1000; // 返回已服务的时长，单位秒
    }

    public float calculateFee() {
        // 这里可以根据风速和服务时长计算费用
        return getServiceTime() * fanSpeed.getRate(); // 假设风速决定费用率
    }

    // Getter and Setter methods
    public FanSpeed getFanSpeed() {
        return fanSpeed;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getRoomId() {
        return roomId;
    }

    public float getTargetTemp() {
        return targetTemp;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ServiceStatus getStatus() {
        return status;
    }
}
