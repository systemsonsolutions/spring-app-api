package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.BannerModel;

public interface BannerRepository extends JpaRepository<BannerModel, Long> {
  Optional<BannerModel> findByName(String name);
}
