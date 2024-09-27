package com.sos.app.dtos.Product;

import org.springframework.web.multipart.MultipartFile;

public record CreateProductDto(String name, String link, Float price, MultipartFile image) {

}
