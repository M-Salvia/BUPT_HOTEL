package org.mason.server_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 允许所有接口
                .allowedOrigins("*")  // 允许所有域名跨域访问，如果只允许特定域名，改为 "http://example.com"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(false) // 如果前端需要携带cookie，改成 true
                .maxAge(3600);  // 预检请求缓存时间（秒）
    }
}
