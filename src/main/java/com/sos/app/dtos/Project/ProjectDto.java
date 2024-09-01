package com.sos.app.dtos.Project;
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
public class ProjectDto {
    @NotNull(message = "Não pode ser nulo")
    private Long id;

    @NotBlank(message = "O usuário não deve estar em branco")
    private String name;

    @NotBlank(message = "O nome não deve estar em branco")
    private String link;

    @NotNull(message = "Não pode ser nulo")
    private String image;
}
