package com.example.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("我的项目API文档") // 文档标题
                        .description("这里是项目的详细接口说明") // 详细描述
                        .version("v1.0.0") // API版本
                        .contact(new Contact().name("维护者").email("contact@example.com")) // 联系人信息
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))); // 许可证信息
    }

    // 配置API分组（例如，将管理端和用户端接口分开）
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("redis") // 分组名
                .pathsToMatch("/api/redis/**") // 匹配以/admin开头的路径
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("product")
                .pathsToMatch("/api/products/**")
                .build();
    }
}
