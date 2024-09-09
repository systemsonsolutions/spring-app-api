package com.sos.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sos.app.models.RoleModel;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
  Optional<RoleModel> findByName(String name);

  Boolean existsByName(String name);

}
