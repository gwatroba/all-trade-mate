package com.project.trademate.client.allegro;

import com.project.trademate.auth.AllegroAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Slf4j
@Service
public class AllegroApiClient {
    private static final String ALLEGRO_V1_CONTENT_TYPE = "application/vnd.allegro.public.v1+json";

    private final WebClient webClient;
    private final AllegroAuthService authService; // New dependency

    public AllegroApiClient(WebClient webClient, AllegroAuthService authService) {
        this.webClient = webClient;
        this.authService = authService;
    }

    public <T> T get(String url, Class<T> responseType) throws IOException {
        String accessToken = authService.getValidAccessToken();

        log.info("Executing GET request to: {}", url);
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.allegro.public.v1+json")
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T> T post(String url, Object requestBody, Class<T> responseType) throws IOException {
        String accessToken = authService.getValidAccessToken();

        log.info("Executing POST request to: {}", url);
        try {
            return webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, ALLEGRO_V1_CONTENT_TYPE)
                    .header(HttpHeaders.CONTENT_TYPE, ALLEGRO_V1_CONTENT_TYPE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("API POST call to {} failed with status {} and body {}", url, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new IOException("API call failed", e);
        }
    }

    public <T> T put(String url, Object requestBody, Class<T> responseType) throws IOException {
        String accessToken = authService.getValidAccessToken();

        log.info("Executing PUT request to: {}", url);
        try {
            return webClient.put()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, ALLEGRO_V1_CONTENT_TYPE)
                    .header(HttpHeaders.CONTENT_TYPE, ALLEGRO_V1_CONTENT_TYPE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("API PUT call to {} failed with status {} and body {}", url, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new IOException("API call failed", e);
        }
    }

}