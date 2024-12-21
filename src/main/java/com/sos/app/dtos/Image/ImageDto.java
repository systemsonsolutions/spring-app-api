package com.sos.app.dtos.Image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
public class ImageDto {
  @NotNull(message = "Não pode ser nulo")
  private Long id;

  @NotBlank(message = "Não pode ser nulo")
  private String url;

  @NotBlank(message = "Não pode ser nulo")
  private Integer banner_id;

  @NotNull(message = "Não pode ser nulo")
  private MultipartFile image;
}
