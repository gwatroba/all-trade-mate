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

    public void saveTokensFromNode(JsonNode tokenJson) throws IOException {
        ObjectNode persistentData = objectMapper.createObjectNode();
        persistentData.put("refresh_token", tokenJson.get("refresh_token").asText());

        Path tempFile = TOKEN_FILE_PATH.resolveSibling(TOKEN_FILE_PATH.getFileName() + ".tmp");
        Files.writeString(tempFile, persistentData.toString());

        Files.move(tempFile, TOKEN_FILE_PATH, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
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