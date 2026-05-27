package com.curso.integracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiIntegradoraInteligenteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiIntegradoraInteligenteApplication.class, args);
    }
}
