package com.sos.app.dtos.Founder;

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
public class UpdateFounderDto {
  @NotBlank(message = "O nome não deve estar em branco")
  private String name;

  @NotBlank(message = "O linkedin não deve estar em branco")
  private String linkedin;

  @NotBlank(message = "O cargo não deve estar em branco")
  private String position;

  @NotNull(message = "A imagem não deve estar em branco")
  private MultipartFile image;
}
