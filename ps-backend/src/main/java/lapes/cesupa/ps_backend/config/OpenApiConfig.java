package lapes.cesupa.ps_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API PS-LAPES")
                .version("1.0")
                .description("Documentação da API do sistema de pedidos.")
                .contact(new Contact()
                    .name("Gabriel Costa de Miranda")
                    .email("gabriel@email.com"))
            );
    }
}
