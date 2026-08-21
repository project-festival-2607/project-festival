package com.example.chook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ChatbotConfig {
    @Bean

    // WebClient => Spring 서버가 외부 서버와 통신할 때 사용하는 객체
    public WebClient webClient() {

        return WebClient.builder().build();
    }
}
