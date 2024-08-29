package com.sos.app.controllers.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateProjectDto(String name, String link, MultipartFile image) {
}
