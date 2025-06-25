package com.project.trademate.auth;

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
public class AllegroAuthService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;
    private final String clientId;
    private final String clientSecret;

    private TokenData currentTokenData;

    public AllegroAuthService(WebClient webClient, TokenStorage tokenStorage,
                              @Value("${allegro.client.id}") String clientId,
                              @Value("${allegro.client.secret}") String clientSecret) throws IOException {
        this.webClient = webClient;
        this.tokenStorage = tokenStorage;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        String refreshToken = tokenStorage.loadRefreshToken();
        if (refreshToken != null) {
            this.currentTokenData = new TokenData("", refreshToken, 0);
        }
    }

    public String getValidAccessToken() throws IOException {
        if (currentTokenData == null) {
            throw new IOException("User not authenticated. Please perform the initial authorization to get a refresh token.");
        }
        if (currentTokenData.isAccessTokenExpired()) {
            log.info("Access Token expired or not present. Refreshing...");
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
        String redirectUri = "http://localhost:8080/login/oauth2/code/allegro";

        log.info("Sending request to refresh token using WebClient...");

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            formData.add("redirect_uri", redirectUri);

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
            log.info("Token refreshed successfully.");

        } catch (WebClientResponseException e) {
            int statusCode = e.getStatusCode().value();
            if (statusCode == 400 || statusCode == 401) {
                log.error("FATAL: Refresh token is invalid or has been revoked. Manual re-authentication is required.");
                log.error("Allegro response: {}", e.getResponseBodyAsString());
                throw new IOException("Permanent authentication error. Please re-authorize the application.", e);
            }
            throw new IOException("Failed to refresh token, server responded with status " + statusCode, e);
        } catch (Exception e) {
            log.error("A general error occurred during token refresh.", e);
            throw new IOException("A general error occurred during token refresh: " + e.getMessage(), e);
        }
    }
}