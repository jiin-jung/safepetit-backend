package com.study.safepetit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Safepetit API")
                        .description("팀 너구리너구리의 서비스 세이쁘띠(SafePetit) 백엔드 API 문서입니다.")
                        .version("v1"));
    }
}
