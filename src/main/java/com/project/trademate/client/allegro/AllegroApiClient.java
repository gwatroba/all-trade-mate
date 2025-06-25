package com.project.trademate.client.allegro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.trademate.auth.TokenData;
import com.project.trademate.auth.TokenResponse;
import com.project.trademate.auth.TokenStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class AllegroApiClient {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;

    private TokenData currentTokenData;

    public AllegroApiClient(WebClient webClient, TokenStorage tokenStorage, ObjectMapper objectMapper,
                            @Value("${allegro.client.id}") String clientId,
                            @Value("${allegro.client.secret}") String clientSecret) throws IOException {
        this.webClient = webClient;
        this.tokenStorage = tokenStorage;
        this.objectMapper = objectMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        String refreshToken = tokenStorage.loadRefreshToken();
        if (refreshToken != null) {
            this.currentTokenData = new TokenData("", refreshToken, 0);
        }
    }

    public <T> T get(String url, Class<T> responseType) throws IOException {
        String accessToken = getValidAccessToken();

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.allegro.public.v1+json")
                .retrieve()
                .bodyToMono(responseType)
                .block(); // .block() makes the reactive call synchronous.
    }

    public <T> T post(String url, Object requestBody, Class<T> responseType, String contentType) throws IOException {
        String accessToken = getValidAccessToken();

        try {
            return webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, contentType) // For this endpoint, Accept and Content-Type should be the same.
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("API call to {} failed with status {} and body {}", url, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new IOException("API call failed", e);
        }
    }

    public <T> T put(String url, Object requestBody, Class<T> responseType) throws IOException {
        String accessToken = getValidAccessToken();

        return webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.allegro.public.v1+json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private String getValidAccessToken() throws IOException {
        if (currentTokenData == null) {
            throw new IOException("User not authenticated. Please perform the initial authorization to get a refresh token.");
        }
        if (currentTokenData.isAccessTokenExpired()) {
            System.out.println("Access Token expired or not present. Refreshing...");
            refreshAccessToken();
        }
        return currentTokenData.getAccessToken();
    }

    private void refreshAccessToken() throws IOException {
        String refreshToken = currentTokenData.getRefreshToken();
        if (refreshToken == null) {
            throw new IOException("No refresh token available. Please re-authorize the application.");
        }

        String tokenUrl = "https://allegro.pl/auth/oauth/token";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        formData.add("redirect_uri", "http://localhost:8080/login/oauth2/code/allegro");

        System.out.println("Sending request to refresh token using WebClient...");

        try {
            TokenResponse tokenResponse = webClient.post()
                    .uri(tokenUrl)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();

            if (tokenResponse == null) {
                throw new IOException("Received null response from token endpoint.");
            }

            this.currentTokenData = new TokenData(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresIn()
            );

            tokenStorage.saveTokensFromResponse(tokenResponse);
            System.out.println("Token refreshed successfully.");

        } catch (WebClientResponseException e) {
            int statusCode = e.getStatusCode().value();
            if (statusCode == 400 || statusCode == 401) {
                log.error("FATAL: Refresh token is invalid or has been revoked. Manual re-authentication is required.");
                log.error("Allegro response: {}", e.getResponseBodyAsString());
                throw new IOException("Permanent authentication error. Please re-authorize the application.", e);
            }
            throw new IOException("Failed to refresh token, server responded with status " + statusCode, e);
        } catch (Exception e) {
            throw new IOException("A general error occurred during token refresh: " + e.getMessage(), e);
        }
    }
}