public class AirConditioningSystemDebug {
    public static void main(String[] args) {
        ScheduleObject scheduler = new ScheduleObject();

        try {
            System.out.println("开始调试空调调度系统...\n");

            // 模拟请求 1 - 低速风
            RequestObject req1 = new RequestObject("101", 22.0f, FanSpeed.LOW, 32.0f); // 初始温度32
            scheduler.receiveRequest(req1);  // 第一个请求，加入队列
            scheduler.printStatus();  // 打印当前状态

            // 模拟请求 2 - 中速风
            RequestObject req2 = new RequestObject("102", 20.0f, FanSpeed.MEDIUM, 28.0f); // 初始温度28
            scheduler.receiveRequest(req2);  // 第二个请求，加入队列
            scheduler.printStatus();  // 打印当前状态

            // 模拟请求 3 - 高速风
            RequestObject req3 = new RequestObject("103", 18.0f, FanSpeed.MEDIUM, 30.0f); // 初始温度30
            scheduler.receiveRequest(req3);  // 第三个请求，加入队列
            scheduler.printStatus();  // 打印当前状态

            // 模拟请求 4 - 高速风，抢占低速风服务
            RequestObject req4 = new RequestObject("104", 19.0f, FanSpeed.MEDIUM, 29.0f); // 初始温度29
            scheduler.receiveRequest(req4);  // 高速风请求，应该抢占低速风的服务
            scheduler.printStatus();  // 打印当前状态

            // 模拟请求 5 - 高速风，时间片调度（风速与请求4相同）
            RequestObject req5 = new RequestObject("105", 21.0f, FanSpeed.HIGH, 28.0f);
            scheduler.receiveRequest(req5);  // 高速风请求，应该启动时间片调度
            scheduler.printStatus();  // 打印当前状态

            // 模拟房间温度变化
            System.out.println("等一会========================================================");
            Thread.sleep(6000);  // 模拟一段时间，等待服务时间流逝（5秒）

            // 模拟服务时间达到2分钟的情况，检查服务队列
            scheduler.updateServiceStatus();  // 服务时间更新，超过2分钟的服务对象应该被调度
            scheduler.printStatus();  // 打印当前状态

            // 打印所有详单记录
            System.out.println("\n=== 详单记录 ===");
            for (DetailRecord record : scheduler.getDetailRecords()) {
                System.out.println(record.toString());
            }

            // 模拟温度回温
            ServiceObject service1 = scheduler.getServiceQueue().get(0).getServiceObject();
            service1.updateCurrentTemp(25.0f);  // 调整服务对象的目标温度，模拟温度变化
            System.out.println("回温后房间 " + service1.getRoomId() + " 当前温度: " + service1.getCurrentTemp() + "°C");
            scheduler.printStatus();  // 打印当前状态

            // 模拟回温超过1度的情况，重新启动空调服务
            service1.updateCurrentTemp(23.0f);  // 超过1度回温，重新调度
            scheduler.receiveRequest(new RequestObject(service1.getRoomId(), service1.getTargetTemp(), service1.getFanSpeed(), service1.getCurrentTemp()));
            scheduler.printStatus();  // 打印当前状态

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scheduler.shutdown();
        }
    }
}
