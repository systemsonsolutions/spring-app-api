package com.sos.app.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.dtos.Founder.CreateFounderDto;
import com.sos.app.dtos.Founder.FounderDto;
import com.sos.app.dtos.Founder.UpdateFounderDto;
import com.sos.app.models.FounderModel;
import com.sos.app.services.FounderService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/founders")
public class FounderController {
  @Autowired
  private FounderService founderService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping
  public ResponseEntity<Page<FounderDto>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<FounderDto> founderPage = founderService.findAll(pageable);
    return ResponseEntity.ok(founderPage);
  }

  @Transactional
  @GetMapping("/{id}")
  public ResponseEntity<Object> getFounder(@PathVariable(value = "id") Long id) {
    FounderDto founderDto = founderService.findById(id);
    return ResponseEntity.ok(founderDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<FounderDto> updateById(@Valid @RequestBody @ModelAttribute UpdateFounderDto founderRequest,
      @PathVariable("id") Long id, BindingResult br) throws IOException {

    FounderDto founderDto = founderService.updateFounder(founderRequest, id);
    return ResponseEntity.ok().body(founderDto);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteFounder(@PathVariable(value = "id") Long id) {
    founderService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @Transactional
  @PostMapping
  public ResponseEntity<FounderModel> newFounder(@ModelAttribute CreateFounderDto dto) throws IOException {
    FounderModel founder = founderService.newFounder(dto);
    return ResponseEntity.ok().body(founder);
  }

}
