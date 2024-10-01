package com.sos.app.dtos.Product;

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
public class ProductDto {

  @NotNull(message = "Não pode ser nulo")
  private Long id;

  @NotBlank(message = "O nome não deve estar em branco")
  private String name;

  @NotBlank(message = "O link não deve estar em branco")
  private String link;

  @NotBlank(message = "O preço não deve estar em branco")
  private Float price;

  @NotNull(message = "A imagem não deve estar em branco")
  private String image;
}
