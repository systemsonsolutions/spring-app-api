package com.sos.app.dtos.Project;

import org.springframework.web.multipart.MultipartFile;

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
public class UpdateProjectDto {

    @NotBlank(message = "O nome não deve estar em branco")
    private String name;

    @NotBlank(message = "A descrição não deve estar em branco")
    private String description;

    @NotBlank(message = "O link não deve estar em branco")
    private String link;

    @NotNull(message = "A imagem não deve estar em branco")
    private MultipartFile image;
}