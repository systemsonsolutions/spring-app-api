package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sos.app.models.ProjectModel;

public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {
  Optional<ProjectModel> findByNameOrLink(String name, String link);
}
