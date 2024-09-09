package com.sos.app.dtos.User;

import java.util.UUID;

public record CreateUserDto(String name, String username, String password, String role, UUID user_id) {
}
