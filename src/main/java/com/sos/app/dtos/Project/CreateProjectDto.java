package com.sos.app.dtos.Project;

import org.springframework.web.multipart.MultipartFile;

public record CreateProjectDto(String name, String link, MultipartFile image) {
}
