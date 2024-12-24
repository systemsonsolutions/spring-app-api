package com.sos.app.dtos.Banner;

import java.util.List;

import com.sos.app.models.ImageModel;

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
public class BannerDto {
  @NotNull(message = "Não pode ser nulo")
  private Long id;

  @NotBlank(message = "Não pode ser nulo")
  private String name;

  @NotBlank(message = "Não pode ser nulo")
  private Integer height;

  @NotBlank(message = "Não pode ser nulo")
  private Integer width;
}
