package com.sos.app.dtos.Banner;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sos.app.models.ImageModel;

public record CreateBannerDto(String name, Integer height, Integer width, List<MultipartFile> images ) {

}
