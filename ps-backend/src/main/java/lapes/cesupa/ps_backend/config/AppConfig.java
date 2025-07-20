package lapes.cesupa.ps_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    
        @Bean
    public WebClient openRouteServiceWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader("User-Agent", "Java-WebClient")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
