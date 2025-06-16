package org.mason.server_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.mason.server_back.mapper")  // 扫描 Mapper 接口
@EnableScheduling // 添加此注解，启用定时任务
@EnableAsync
public class ServerBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerBackApplication.class, args);
    }

}
