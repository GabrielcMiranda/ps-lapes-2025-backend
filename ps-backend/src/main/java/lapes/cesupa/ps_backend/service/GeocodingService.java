package lapes.cesupa.ps_backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeocodingService {

    private final String apiKey;

    private final WebClient webClient;

    public GeocodingService(@Value("${ors.api.key}") String apiKey, WebClient webClient){
        this.apiKey = apiKey;
        this.webClient = webClient;
    }

    public Coordinate geocode(String address) {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);

        JsonNode root = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/search")
                        .queryParam("api_key", apiKey)
                        .queryParam("text", encoded)
                        .queryParam("size", 1)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(); 

        System.out.println("API Response: " + root.toPrettyString());

        JsonNode features = root.path("features");

        if (!features.isArray() || features.size() == 0) {
            throw new RuntimeException("No results found for address: " + address);
        }

        JsonNode coords = features.get(0).path("geometry").path("coordinates");

        double lon = coords.get(0).asDouble();
        double lat = coords.get(1).asDouble();

        return new Coordinate(lat, lon);
    }


    public record Coordinate(double latitude, double longitude) {}
}

