package com.sos.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.dtos.Banner.BannerDto;
import com.sos.app.dtos.Banner.CreateBannerDto;
import com.sos.app.dtos.Image.ImageDto;
import com.sos.app.models.BannerModel;
import com.sos.app.models.ImageModel;
import com.sos.app.repository.BannerRepository;
import com.sos.app.repository.ImageRepository;
import com.sos.app.services.exceptions.DataIntegrityException;
import com.sos.app.services.exceptions.NotFoundException;

@Service
public class ImageService {

  @Autowired
  BannerRepository bannerRepository;
  
  @Autowired
  ImageRepository imageRepository;
  
  List<ImageModel> imagesPath;

  @Autowired
  ModelMapper modelMapper;

  @Value("${file.upload-dir}")
  private String uploadDir;

  public Page<ImageDto> findAll(Pageable pageable, Long id) {
    return imageRepository.findByBannerId(pageable, id).map(user -> {
      ImageDto imageDto = modelMapper.map(user, ImageDto.class);
      return imageDto;
    });
  }

  public BannerDto findById(Long id) {
    try {
      Optional<BannerModel> bannerOptional = bannerRepository.findById(id);

      if (!bannerOptional.isPresent()) {
        throw new DataIntegrityException("Fundador não existe");
      }

      BannerDto bannerDto = modelMapper.map(bannerOptional.get(), BannerDto.class);

      return bannerDto;
    } catch (NoSuchElementException e) {
      throw new NotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + BannerModel.class.getName());
    }
  }

  public ImageModel newImage(BannerDto banner, MultipartFile image) throws IOException {
    var existingBanner = bannerRepository.findByName(banner.getName());

    if (!existingBanner.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Banner not found");
    }

    // if (!images.isEmpty()) {
    //   for (MultipartFile image : images) {
    //     try {
    //       // Chama o método de upload de imagem para cada imagem na lista
    //       String imagePath = uploadImage(image);
          
    //       ImageModel imageModel = new ImageModel();
    //       imageModel.setUrl(imagePath);
    //       imageModel.setBanner(existingBanner.get());
    //       imagesPath.add(imageModel);
    //       System.out.println("Upload realizado com sucesso: " + imagePath);
    //     } catch (Exception e) {
    //       // Trata possíveis erros no upload
    //       System.err.println("Erro ao fazer upload da imagem: " + image);
    //       e.printStackTrace();
    //     }
    //   }
    // }

    // Criar o novo Banner
    var bannerModel = new BannerModel();
    var imageModel = new ImageModel();

    String imagePath = uploadImage(image);

    imageModel.setUrl(imagePath);
    imageModel.setBanner(existingBanner.get());

    imageRepository.save(imageModel);

    return imageModel;
  }

  // public FounderDto updateFounder(UpdateFounderDto founder, Long id) throws IOException {
  //   try {
  //     System.out.println(founder.getName());
  //     System.out.println(founder.getLinkedin());
  //     System.out.println(founder.getPosition());
  //     System.out.println(founder.getImage());
  //     Optional<BannerModel> founderOptional = bannerRepository.findById(id);

  //     if (founderOptional.isEmpty()) {
  //       throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  //     }

  //     BannerModel bannerModel = founderOptional.get();
  //     bannerModel.setName(founder.getName());
  //     bannerModel.setLinkedin(founder.getLinkedin());
  //     bannerModel.setPosition(founder.getPosition());

  //     // Verifica se a imagem foi enviada
  //     if (founder.getImage() != null && !founder.getImage().isEmpty()) {
  //       String imagePath = uploadImage(founder.getImage());
  //       bannerModel.setImage(imagePath);
  //     }

  //     bannerRepository.save(founderModel);

  //     System.out.println(bannerModel.getName());
  //     System.out.println(bannerModel.getLinkedin());
  //     System.out.println(bannerModel.getPosition());
  //     System.out.println(bannerModel.getImage());

  //     return modelMapper.map(bannerModel, FounderDto.class);
  //   } catch (DataIntegrityViolationException e) {
  //     throw new DataIntegrityException("Campo(s) obrigatório(s) da Pessoa não foi(foram) preenchido(s).");
  //   }
  // }

  public void deleteById(Long id) {
    try {
      if (imageRepository.existsById(id)) {
        imageRepository.deleteById(id);
      } else {
        throw new DataIntegrityException("O Id da imagem não existe na base de dados!");
      }
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityException("Não é possível excluir a Imagem!");
    }
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
