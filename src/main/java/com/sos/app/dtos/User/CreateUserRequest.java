package com.sos.app.dtos.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "O usuário não deve estar em branco")
    private String username;

    @NotBlank(message = "O nome não deve estar em branco")
    private String name;

    @NotBlank(message = "A senha não deve estar em branco")
    private String password;

    @NotNull(message = "A grupo de usuário não deve estar em branco")
    private Long role;

}