package com.sos.app.dtos.Founder;

import org.springframework.web.multipart.MultipartFile;

public record CreateFounderDto(String name, String linkedin, String position, MultipartFile image) {

}
