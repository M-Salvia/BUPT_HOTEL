package org.mason.server_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.mason.server_back.mapper")  // 扫描 Mapper 接口

public class ServerBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerBackApplication.class, args);
    }

}
