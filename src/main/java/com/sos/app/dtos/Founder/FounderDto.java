package com.sos.app.dtos.Founder;

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
public class FounderDto {
  @NotNull(message = "Não pode ser nulo")
  private Long id;

  @NotBlank(message = "O nome não deve estar em branco")
  private String name;

  @NotBlank(message = "O cargo não deve estar em branco")
  private String position;

  @NotBlank(message = "O linkedin não deve estar em branco")
  private String linkedin;

  @NotNull(message = "Não pode ser nulo")
  private String image;
}
