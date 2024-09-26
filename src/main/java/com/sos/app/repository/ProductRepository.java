package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
  Optional<ProductModel> findByNameOrLink(String name, String link);
}
