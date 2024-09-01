package com.sos.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import com.sos.app.dtos.Project.CreateProjectDto;
import com.sos.app.dtos.Project.ProjectDto;
import com.sos.app.dtos.Project.UpdateProjectDto;
import com.sos.app.models.ProjectModel;
import com.sos.app.services.ProjectService;

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/projects")
public class ProjectController {
  @Autowired
  private ProjectService projectService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping
  public ResponseEntity<Page<ProjectDto>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<ProjectDto> projectPage = projectService.findAll(pageable);
    return ResponseEntity.ok(projectPage);
  }

  @Transactional
  @GetMapping("/{id}")
  public ResponseEntity<Object> getProject(@PathVariable(value = "id") Long id) {
    ProjectDto projectDto = projectService.findById(id);
    return ResponseEntity.ok(projectDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ProjectDto> updateById(@Valid @RequestBody @ModelAttribute UpdateProjectDto projectRequest,
      @PathVariable("id") Long id, BindingResult br) throws IOException {

    ProjectDto projectDto = projectService.updateProject(projectRequest, id);
    return ResponseEntity.ok().body(projectDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProject(@PathVariable(value = "id") Long id) {
    projectService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @Transactional
  @PostMapping
  public ResponseEntity<ProjectModel> newProject(@ModelAttribute CreateProjectDto dto) throws IOException {
    ProjectModel project = projectService.newProject(dto);
    return ResponseEntity.ok().body(project);
  }
}