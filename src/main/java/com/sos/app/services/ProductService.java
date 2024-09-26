package com.sos.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.dtos.Product.CreateProductDto;
import com.sos.app.dtos.Product.ProductDto;
import com.sos.app.dtos.Product.UpdateProductDto;
import com.sos.app.models.ProductModel;
import com.sos.app.repository.ProductRepository;
import com.sos.app.services.exceptions.DataIntegrityException;
import com.sos.app.services.exceptions.NotFoundException;

@Service
public class ProductService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ModelMapper modelMapper;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Autowired
  BCryptPasswordEncoder passwordEncoder;

  public Page<ProductDto> findAll(Pageable pageable) {
    return productRepository.findAll(pageable).map(user -> {
      ProductDto productDto = modelMapper.map(user, ProductDto.class);
      return productDto;
    });
  }

  public ProductDto findById(Long id) {
    try {
      Optional<ProductModel> productOptional = productRepository.findById(id);

      if (!productOptional.isPresent()) {
        throw new DataIntegrityException("Projeto não existe");
      }

      ProductDto productDto = modelMapper.map(productOptional.get(), ProductDto.class);

      return productDto;
    } catch (NoSuchElementException e) {
      throw new NotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + ProductModel.class.getName());
    }
  }

  public ProductModel newProduct(CreateProductDto product) throws IOException {
    var existingproduct = productRepository.findByNameOrLink(product.name(), product.link());

    if (existingproduct.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Product already exists");
    }

    if (product.image() == null || product.image().isEmpty()) {
      // Trate o caso onde o arquivo não foi enviado ou está vazio
      throw new DataIntegrityException("Arquivo não selecionado!");
    }

    // Fazer o upload da imagem
    String imagePath = uploadImage(product.image());

    // Criar o novo projeto
    var productModel = new ProductModel();
    productModel.setName(product.name());
    productModel.setLink(product.link());
    productModel.setPrice(product.price());
    productModel.setImage(imagePath);

    productRepository.save(productModel);

    return productModel;
  }

  public ProductDto updateProduct(UpdateProductDto product, Long id) throws IOException {
    try {
      Optional<ProductModel> productOptional = productRepository.findById(id);

      if (productOptional.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }

      ProductModel productModel = productOptional.get();
      productModel.setName(product.getName());
      productModel.setLink(product.getLink());
      productModel.setPrice(product.getPrice());

      // Verifica se a imagem foi enviada
      if (product.getImage() != null && !product.getImage().isEmpty()) {
        String imagePath = uploadImage(product.getImage());
        productModel.setImage(imagePath);
      }

      productRepository.save(productModel);

      return modelMapper.map(productModel, ProductDto.class);
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityException("Campo(s) obrigatório(s) não foi(foram) preenchido(s).");
    }
  }

  public void deleteById(Long id) {
    try {
      if (productRepository.existsById(id)) {
        productRepository.deleteById(id);
      } else {
        throw new DataIntegrityException("O Id do Usuário não existe na base de dados!");
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
