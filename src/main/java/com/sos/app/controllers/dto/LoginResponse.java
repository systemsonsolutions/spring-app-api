package com.sos.app.controllers.dto;

import java.util.Set;

import com.sos.app.models.Role;

public record LoginResponse(String accessToken, Long expiresIn, String username, String name, Set<Role> roles) {

}