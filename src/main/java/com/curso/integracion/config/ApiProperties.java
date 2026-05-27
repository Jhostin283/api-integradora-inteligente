package com.curso.integracion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.externa")
public record ApiProperties(
        String baseUrl,
        String key
) {
}
