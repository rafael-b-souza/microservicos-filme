package br.edu.utfpr.api_gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/api-docs")
public class OpenApiAggregatorController {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiAggregatorController.class);

    private final WebClient webClient;
    private final String GATEWAY_URL = "http://localhost:8080"; 

    public OpenApiAggregatorController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/{service}")
    public Mono<ResponseEntity<String>> getApiDocs(@PathVariable String service) {
        String serviceUrl = switch (service) {
            case "avaliacao" -> "http://localhost:8081/v3/api-docs";
            case "filme" -> "http://localhost:8082/v3/api-docs";
            case "usuario" -> "http://localhost:8083/v3/api-docs";
            case "recomendacao" -> "http://localhost:8084/v3/api-docs";
            default -> null;
        };

        if (serviceUrl == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid service name"));
        }

        return webClient.get()
                .uri(serviceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(originalJson -> logger.info("Original API Docs for {}: {}", service, originalJson)) 
                .map(body -> {
                    // Define base URL do gateway na documentacao
                    String modifiedBody = body.replaceFirst(
                            "\"servers\":\\s*\\[.*?\\]", // Matches any "servers" array
                            "\"servers\":[{\"url\":\"" + GATEWAY_URL + "\"}]" // Replace with API Gateway URL
                    );

                    
                    if (!modifiedBody.contains("\"paths\": {") || modifiedBody.contains("\"paths\": {}")) {
                        logger.warn("API Docs for {} contains empty paths!", service);
                    }

                    logger.info("Modified API Docs for {}: {}", service, modifiedBody);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(modifiedBody);
                })
                .onErrorResume(e -> {
                    logger.error("Error fetching API Docs for {}: {}", service, e.getMessage());
                    return Mono.just(ResponseEntity.status(500)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"error\": \"Error fetching API Docs: " + e.getMessage() + "\"}"));
                });
    }
}
