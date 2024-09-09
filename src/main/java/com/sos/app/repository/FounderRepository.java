package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.FounderModel;

public interface FounderRepository extends JpaRepository<FounderModel, Long> {
  Optional<FounderModel> findByNameOrLinkedin(String name, String linkedin);
}
