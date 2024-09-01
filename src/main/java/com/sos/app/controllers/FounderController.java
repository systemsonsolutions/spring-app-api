package com.sos.app.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.controllers.dto.CreateFounderDto;

import com.sos.app.models.Founder;

import com.sos.app.repository.FounderRepository;

@RestController
@RequestMapping("/founders")
public class FounderController {

  @Autowired
  private FounderRepository founderRepository;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping("/all")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<Founder>> listFounders() {
    var founders = founderRepository.findAll();
    return ResponseEntity.ok(founders);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<Object> getFounder(@PathVariable(value = "id") Long id) {
    Optional<Founder> founderOptional = founderRepository.findById(id);
    if (!founderOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Founder not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(founderOptional.get());
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateFounder(@PathVariable Long id, @ModelAttribute CreateFounderDto dto)
      throws IOException {

    Optional<Founder> founderOptional = founderRepository.findById(id);
    if (founderOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Founder founder = founderOptional.get();
    founder.setName(dto.name());
    founder.setLinkedin(dto.linkedin());
    founder.setPosition(dto.position());
    // Verifica se a imagem foi enviada
    if (dto.image() != null && !dto.image().isEmpty()) {
      String imagePath = uploadImage(dto.image());
      founder.setImage(imagePath);
    }

    founderRepository.save(founder);

    return ResponseEntity.ok().build();
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteFounder(@PathVariable(value = "id") Long id) {
    Optional<Founder> founderOptional = founderRepository.findById(id);
    if (!founderOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Founder not found.");
    }
    founderRepository.deleteById(founderOptional.get().getId());
    // userRepository.delete(userOptional.get());

    return ResponseEntity.status(HttpStatus.OK).body("Founder deleted successfully.");
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PostMapping
  public ResponseEntity<Void> newFounder(@ModelAttribute CreateFounderDto dto) throws IOException {

    // Verificar se já existe um projeto com o mesmo nome ou link (opcional)
    var existingFounder = founderRepository.findByNameOrLinkedin(dto.name(), dto.linkedin());
    if (existingFounder.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Founder already exists");
    }

    // Fazer o upload da imagem
    String imagePath = uploadImage(dto.image());

    // Criar o novo projeto
    var founder = new Founder();
    founder.setName(dto.name());
    founder.setLinkedin(dto.linkedin());
    founder.setPosition(dto.position());
    founder.setImage(imagePath);

    founderRepository.save(founder);

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
