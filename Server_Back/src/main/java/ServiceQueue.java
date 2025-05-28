import java.util.*;

public class ServiceQueue {
    private List<ServiceObject> serviceRecords = new ArrayList<>();
    private Map<FanSpeed, List<ServiceObject>> fanSpeedGroups = new HashMap<>();

    public void addServiceRecord(ServiceObject serviceObject) {
        serviceRecords.add(serviceObject);
        fanSpeedGroups.computeIfAbsent(serviceObject.getFanSpeed(), k -> new ArrayList<>()).add(serviceObject);
    }

    public void removeServiceRecord(String roomId) {
        Iterator<ServiceObject> iterator = serviceRecords.iterator();
        while (iterator.hasNext()) {
            ServiceObject serviceObject = iterator.next();
            if (serviceObject.getRoomId().equals(roomId)) {
                iterator.remove();
                // 同步移除fanSpeedGroups中的对象
                List<ServiceObject> groupList = fanSpeedGroups.get(serviceObject.getFanSpeed());
                if (groupList != null) {
                    groupList.remove(serviceObject);
                    if (groupList.isEmpty()) {
                        fanSpeedGroups.remove(serviceObject.getFanSpeed());
                    }
                }
            }
        }
    }

    public int getServiceObjectCount() {
        return serviceRecords.size();
    }

    public ServiceObject findSmallAndLongestService() {
        return serviceRecords.stream().max(Comparator.comparingLong(ServiceObject::getServiceTime)).orElse(null);
    }

    public ServiceObject findLongestService() {
        return serviceRecords.stream().max(Comparator.comparingLong(ServiceObject::getServiceTime)).orElse(null);
    }
}
