package lapes.cesupa.ps_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import lapes.cesupa.ps_backend.config.TakeawayAddressProperties;

@SpringBootApplication
@EnableConfigurationProperties(TakeawayAddressProperties.class)
public class PsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PsBackendApplication.class, args);
	}

}
