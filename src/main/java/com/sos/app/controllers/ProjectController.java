package com.sos.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.controllers.dto.CreateProjectDto;
import com.sos.app.models.Project;
import com.sos.app.repository.ProjectRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectRepository projectRepository;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping("/all")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<Project>> listProjects() {
    var projects = projectRepository.findAll();
    return ResponseEntity.ok(projects);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<Object> getProject(@PathVariable(value = "id") Long id) {
    Optional<Project> projectOptional = projectRepository.findById(id);
    if (!projectOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(projectOptional.get());
  }

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
    Path uploadPath;

    if (Paths.get(uploadDir).isAbsolute()) {
      // Se uploadDir já é um caminho absoluto, use-o diretamente
      uploadPath = Paths.get(uploadDir);
    } else {
      // Caso contrário, combine com o diretório raiz do projeto
      String absolutePath = System.getProperty("user.dir") + "/" + uploadDir;
      uploadPath = Paths.get(absolutePath);
    }

    // Criar diretório se não existir
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