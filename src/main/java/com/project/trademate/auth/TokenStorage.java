package com.project.trademate.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class TokenStorage {
    private static final Path TOKEN_FILE_PATH = Paths.get("allegro_token.json");
    private final ObjectMapper objectMapper;

    public TokenStorage(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void saveTokensFromResponse(TokenResponse tokenResponse) throws IOException {
        ObjectNode persistentData = objectMapper.createObjectNode();
        persistentData.put("refresh_token", tokenResponse.getRefreshToken());

        String fileContent = persistentData.toString();
        Files.writeString(TOKEN_FILE_PATH, fileContent);
    }

    public String loadRefreshToken() throws IOException {
        if (!Files.exists(TOKEN_FILE_PATH)) {
            return null;
        }
        String content = Files.readString(TOKEN_FILE_PATH);
        JsonNode persistentData = objectMapper.readTree(content);
        JsonNode refreshTokenNode = persistentData.get("refresh_token");
        return (refreshTokenNode != null) ? refreshTokenNode.asText() : null;
    }
}