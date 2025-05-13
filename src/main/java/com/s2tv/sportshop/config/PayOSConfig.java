package com.s2tv.sportshop.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Getter
@Configuration
public class PayOSConfig {
    @Value("${payos.client_id}")
    private String clientId;

    @Value("${payos.api_key}")
    private String apiKey;

    @Value("${payos.checksum_key}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
