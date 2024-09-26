package com.sos.app.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.dtos.Product.CreateProductDto;
import com.sos.app.dtos.Product.ProductDto;
import com.sos.app.dtos.Product.UpdateProductDto;
import com.sos.app.models.ProductModel;

import com.sos.app.services.ProductService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
  @Autowired
  private ProductService productService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping
  public ResponseEntity<Page<ProductDto>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<ProductDto> productPage = productService.findAll(pageable);
    return ResponseEntity.ok(productPage);
  }

  @Transactional
  @GetMapping("/{id}")
  public ResponseEntity<Object> getProduct(@PathVariable(value = "id") Long id) {
    ProductDto productDto = productService.findById(id);
    return ResponseEntity.ok(productDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> updateById(@Valid @RequestBody @ModelAttribute UpdateProductDto productRequest,
      @PathVariable("id") Long id, BindingResult br) throws IOException {

    ProductDto productDto = productService.updateProduct(productRequest, id);
    return ResponseEntity.ok().body(productDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") Long id) {
    productService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @Transactional
  @PostMapping
  public ResponseEntity<ProductModel> newProduct(@ModelAttribute CreateProductDto dto) throws IOException {
    ProductModel product = productService.newProduct(dto);
    return ResponseEntity.ok().body(product);
  }

}
