package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.Founder;

public interface FounderRepository extends JpaRepository<Founder, Long> {
  Optional<Founder> findByNameOrLinkedin(String name, String linkedin);
}
