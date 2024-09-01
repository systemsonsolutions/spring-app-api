package com.sos.app.dtos.Auth;

public record LoginResponse(String accessToken, Long expiresIn, String username, String name, String role) {
    
}
