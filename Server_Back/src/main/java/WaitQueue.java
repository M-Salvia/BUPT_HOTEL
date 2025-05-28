import java.util.*;

public class WaitQueue {
    private List<WaitRecord> waitRecords = new ArrayList<>();

    public void addWaitRecord(String roomId, FanSpeed fanSpeed, float currentTemp, long waitTime) {
        waitRecords.add(new WaitRecord(roomId, fanSpeed, currentTemp, waitTime));
    }

    public void removeWaitRecord(String roomId) {
        waitRecords.removeIf(waitRecord -> waitRecord.roomId.equals(roomId));
    }

    public WaitRecord getLongestWaitRecord() {
        return waitRecords.stream().max(Comparator.comparingLong(WaitRecord::getWaitTime)).orElse(null);
    }

    public void updateWaitTime() {
        for (WaitRecord record : waitRecords) {
            record.setWaitTime(record.getWaitTime() - 120); // 假设每个时间片为 120 秒
        }
    }
}

class WaitRecord {
    private String roomId;
    private FanSpeed fanSpeed;
    private float currentTemp;
    private long waitTime;

    public WaitRecord(String roomId, FanSpeed fanSpeed, float currentTemp, long waitTime) {
        this.roomId = roomId;
        this.fanSpeed = fanSpeed;
        this.currentTemp = currentTemp;
        this.waitTime = waitTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
