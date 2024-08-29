package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  Optional<Project> findByNameOrLink(String name, String link);
}
