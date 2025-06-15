package com.tave.weathertago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(new Server().url("http://localhost:8080")));
    }

    public Info apiInfo() {
        return new Info()
                .title("Swagger")
                .description("REST API")
                .version("1.0.0");
    }
}
