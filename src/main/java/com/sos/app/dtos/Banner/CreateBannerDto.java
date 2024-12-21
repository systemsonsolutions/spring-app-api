package com.sos.app.dtos.Banner;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record CreateBannerDto(String name, Integer height, Integer width) {

}
