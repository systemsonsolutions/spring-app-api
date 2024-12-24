package com.sos.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.ImageModel;

public interface ImageRepository extends JpaRepository<ImageModel, Long> {
    Page<ImageModel> findByBannerId(Pageable pageable, Long id);
}
