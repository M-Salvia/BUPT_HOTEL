// 测试类
public class AirConditioningSystemTest {
    public static void main(String[] args) {
        ScheduleObject scheduler = new ScheduleObject();

        try {
            // 模拟请求
            System.out.println("开始测试空调调度系统...\n");

            // 第一批请求 - 填满服务队列
            RequestObject req1 = new RequestObject("101", 22.0f, FanSpeed.LOW, 26.0f);
            RequestObject req2 = new RequestObject("102", 20.0f, FanSpeed.MEDIUM, 25.0f);
            RequestObject req3 = new RequestObject("103", 18.0f, FanSpeed.HIGH, 24.0f);

            scheduler.receiveRequest(req1);
            scheduler.receiveRequest(req2);
            scheduler.receiveRequest(req3);

            scheduler.printStatus();

            // 第四个请求 - 高优先级，应该抢占低优先级
            RequestObject req4 = new RequestObject("104", 19.0f, FanSpeed.HIGH, 27.0f);
            scheduler.receiveRequest(req4);

            scheduler.printStatus();

            // 第五个请求 - 相同优先级，应该启动时间片调度
            RequestObject req5 = new RequestObject("105", 21.0f, FanSpeed.HIGH, 28.0f);
            scheduler.receiveRequest(req5);

            scheduler.printStatus();

            // 等待一段时间观察状态变化
            Thread.sleep(3000);
            scheduler.printStatus();

            // 打印所有详单
            System.out.println("\n=== 详单记录 ===");
            for (DetailRecord record : scheduler.getDetailRecords()) {
                System.out.println(record.toString());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scheduler.shutdown();
        }
    }
}