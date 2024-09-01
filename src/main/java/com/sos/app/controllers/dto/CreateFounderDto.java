package com.sos.app.controllers.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateFounderDto(String name, String linkedin, String position, MultipartFile image) {
}
