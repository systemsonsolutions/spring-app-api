package com.sos.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.sos.app.dtos.Founder.CreateFounderDto;
import com.sos.app.dtos.Founder.FounderDto;
import com.sos.app.dtos.Founder.UpdateFounderDto;
import com.sos.app.models.FounderModel;
import com.sos.app.repository.FounderRepository;
import com.sos.app.services.exceptions.DataIntegrityException;
import com.sos.app.services.exceptions.NotFoundException;

@Service
public class FounderService {

  @Autowired
  FounderRepository founderRepository;

  @Autowired
  ModelMapper modelMapper;

  @Value("${file.upload-dir}")
  private String uploadDir;

  public Page<FounderDto> findAll(Pageable pageable) {
    return founderRepository.findAll(pageable).map(user -> {
      FounderDto founderDto = modelMapper.map(user, FounderDto.class);
      return founderDto;
    });
  }

  public FounderDto findById(Long id) {
    try {
      Optional<FounderModel> founderOptional = founderRepository.findById(id);

      if (!founderOptional.isPresent()) {
        throw new DataIntegrityException("Fundador não existe");
      }

      FounderDto founderDto = modelMapper.map(founderOptional.get(), FounderDto.class);

      return founderDto;
    } catch (NoSuchElementException e) {
      throw new NotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + FounderModel.class.getName());
    }
  }

  public FounderModel newFounder(CreateFounderDto founder) throws IOException {
    var existingFounder = founderRepository.findByNameOrLinkedin(founder.name(), founder.linkedin());

    if (existingFounder.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Founder already exists");
    }

    if (founder.image() == null || founder.image().isEmpty()) {
      // Trate o caso onde o arquivo não foi enviado ou está vazio
      throw new DataIntegrityException("Arquivo não selecionado!");
    }

    // Fazer o upload da imagem
    String imagePath = uploadImage(founder.image());

    // Criar o novo projeto
    var founderModel = new FounderModel();
    founderModel.setName(founder.name());
    founderModel.setLinkedin(founder.linkedin());
    founderModel.setPosition(founder.position());
    founderModel.setImage(imagePath);

    founderRepository.save(founderModel);

    return founderModel;
  }

  public FounderDto updateFounder(UpdateFounderDto founder, Long id) throws IOException {
    try {
      System.out.println(founder.getName());
      System.out.println(founder.getLinkedin());
      System.out.println(founder.getPosition());
      System.out.println(founder.getImage());
      Optional<FounderModel> founderOptional = founderRepository.findById(id);

      if (founderOptional.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }

      FounderModel founderModel = founderOptional.get();
      founderModel.setName(founder.getName());
      founderModel.setLinkedin(founder.getLinkedin());
      founderModel.setPosition(founder.getPosition());

      // Verifica se a imagem foi enviada
      if (founder.getImage() != null && !founder.getImage().isEmpty()) {
        String imagePath = uploadImage(founder.getImage());
        founderModel.setImage(imagePath);
      }

      founderRepository.save(founderModel);

      System.out.println(founderModel.getName());
      System.out.println(founderModel.getLinkedin());
      System.out.println(founderModel.getPosition());
      System.out.println(founderModel.getImage());

      return modelMapper.map(founderModel, FounderDto.class);
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityException("Campo(s) obrigatório(s) da Pessoa não foi(foram) preenchido(s).");
    }
  }

  public void deleteById(Long id) {
    try {
      if (founderRepository.existsById(id)) {
        founderRepository.deleteById(id);
      } else {
        throw new DataIntegrityException("O Id do fundador não existe na base de dados!");
      }
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityException("Não é possível excluir a Pessoa!");
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
