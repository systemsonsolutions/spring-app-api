package com.sos.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.controllers.dto.CreateProjectDto;
import com.sos.app.models.Project;
import com.sos.app.repository.ProjectRepository;
import com.sos.app.repository.RoleRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PostMapping
  public ResponseEntity<Void> newProject(@ModelAttribute CreateProjectDto dto) throws IOException {

    // Verificar se já existe um projeto com o mesmo nome ou link (opcional)
    var existingProject = projectRepository.findByNameOrLink(dto.name(), dto.link());
    if (existingProject.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Project already exists");
    }

    // Fazer o upload da imagem
    String imagePath = uploadImage(dto.image());

    // Criar o novo projeto
    var project = new Project();
    project.setName(dto.name());
    project.setLink(dto.link());
    project.setImage(imagePath);

    projectRepository.save(project);

    return ResponseEntity.ok().build();
  }

  private String uploadImage(MultipartFile file) throws IOException {
    // Criar diretório se não existir
    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Salvar o arquivo no diretório especificado
    String fileName = file.getOriginalFilename();
    Path filePath = uploadPath.resolve(fileName);
    file.transferTo(filePath.toFile());

    return fileName; // Retorna o nome do arquivo para salvar no banco
  }
}