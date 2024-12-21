package com.sos.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.ImageModel;

public interface ImageRepository extends JpaRepository<ImageModel, Long> {
}
