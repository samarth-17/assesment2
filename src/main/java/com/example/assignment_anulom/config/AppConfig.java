package com.example.assignment_anulom.config;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OpenAPI swaggerUISetup(){
        return new OpenAPI().info(
                new Info()
                        .title("Backend example ")
                        .description("Mini Project for assement")
        );
    }
}
