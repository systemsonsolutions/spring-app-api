package com.sos.app.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.dtos.Banner.BannerDto;
import com.sos.app.dtos.Banner.CreateBannerDto;
import com.sos.app.models.BannerModel;

import com.sos.app.services.BannerService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/banners")
public class BannerController {

  @Autowired
  private BannerService bannerService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping
  public ResponseEntity<Page<BannerDto>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<BannerDto> bannerPage = bannerService.findAll(pageable);
    return ResponseEntity.ok(bannerPage);
  }

  @Transactional
  @GetMapping("/{id}")
  public ResponseEntity<Object> getBanner(@PathVariable(value = "id") Long id) {
    BannerDto bannerDto = bannerService.findById(id);
    return ResponseEntity.ok(bannerDto);
  }

  // @Transactional
  // @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  // @PutMapping("/{id}")
  // public ResponseEntity<BannerDto> updateById(@Valid @RequestBody
  // @ModelAttribute UpdateBannerDto bannerRequest,
  // @PathVariable("id") Long id, BindingResult br) throws IOException {

  // BannerDto bannerDto = bannerService.updateBanner(bannerRequest, id);
  // return ResponseEntity.ok().body(bannerDto);
  // }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteBanner(@PathVariable(value = "id") Long id) {
    bannerService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PostMapping
  public ResponseEntity<BannerModel> newBanner(@ModelAttribute CreateBannerDto dto) throws IOException {
    BannerModel banner = bannerService.newBanner(dto);
    return ResponseEntity.ok().body(banner);
  }

}
