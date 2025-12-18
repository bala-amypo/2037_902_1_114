package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig{

    @Bean
    public OpenApi customOpenAPI(){
        return new OpenAPI()
        //You need to change the port as per your server
        .servers(List.of(
            new Server().url("https://9539.pro604cr.amypo.ai/")
        ));
    }
}