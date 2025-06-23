package com.project.trademate.auth;

import lombok.Getter;

public class TokenData {
    @Getter
    private final String accessToken;
    @Getter
    private final String refreshToken;
    private final long expiresAt;

    public TokenData(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = System.currentTimeMillis() + (expiresIn * 1000L);
    }

    public boolean isAccessTokenExpired() {
        // Check if the current time is past the expiration time, with a 60-second buffer.
        return System.currentTimeMillis() >= (this.expiresAt - 60_000);
    }
}