package org.example.dto;

public record LoginResponse(String token, String username, long expiresInMs) {
}

