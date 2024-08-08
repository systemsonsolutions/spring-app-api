package com.sos.app.controllers.dto;

public record LoginResponse(String accessToken, Long expiresIn, String username, String name) {

}
