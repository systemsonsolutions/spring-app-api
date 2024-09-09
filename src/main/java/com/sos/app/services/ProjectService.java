package com.sos.app.services;

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

import com.sos.app.dtos.Project.CreateProjectDto;
import com.sos.app.dtos.Project.ProjectDto;
import com.sos.app.dtos.Project.UpdateProjectDto;
import com.sos.app.models.ProjectModel;
import com.sos.app.repository.ProjectRepository;
import com.sos.app.repository.RoleRepository;
import com.sos.app.services.exceptions.DataIntegrityException;
import com.sos.app.services.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Page<ProjectDto> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(user -> {
            ProjectDto projectDto = modelMapper.map(user, ProjectDto.class);
            return projectDto;
        });
    }

    public ProjectDto findById(Long id) {
        try {
            Optional<ProjectModel> projectOptional = projectRepository.findById(id);

            if (!projectOptional.isPresent()) {
                throw new DataIntegrityException("Projeto não existe");
            }

            ProjectDto projectDto = modelMapper.map(projectOptional.get(), ProjectDto.class);

            return projectDto;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + ProjectModel.class.getName());
        }
    }

    public ProjectModel newProject(CreateProjectDto project) throws IOException {
        var existingProject = projectRepository.findByNameOrLink(project.name(), project.link());

        if (existingProject.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Project already exists");
        }

        if (project.image() == null || project.image().isEmpty()) {
            // Trate o caso onde o arquivo não foi enviado ou está vazio
            throw new DataIntegrityException("Arquivo não selecionado!");
        }

        // Fazer o upload da imagem
        String imagePath = uploadImage(project.image());

        // Criar o novo projeto
        var projectModel = new ProjectModel();
        projectModel.setName(project.name());
        projectModel.setLink(project.link());
        projectModel.setImage(imagePath);

        projectRepository.save(projectModel);

        return projectModel;
    }

    public ProjectDto updateProject(UpdateProjectDto project, Long id) throws IOException {
        try {
            System.out.println(project.getName());
            System.out.println(project.getLink());
            System.out.println(project.getImage());
            Optional<ProjectModel> projectOptional = projectRepository.findById(id);

            if (projectOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            ProjectModel projectModel = projectOptional.get();
            projectModel.setName(project.getName());
            projectModel.setLink(project.getLink());

            // Verifica se a imagem foi enviada
            if (project.getImage() != null && !project.getImage().isEmpty()) {
                String imagePath = uploadImage(project.getImage());
                projectModel.setImage(imagePath);
            }

            projectRepository.save(projectModel);

            System.out.println(projectModel.getName());
            System.out.println(projectModel.getLink());
            System.out.println(projectModel.getImage());

            return modelMapper.map(projectModel, ProjectDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Campo(s) obrigatório(s) da Pessoa não foi(foram) preenchido(s).");
        }
    }

    public void deleteById(Long id) {
        try {
            if (projectRepository.existsById(id)) {
                projectRepository.deleteById(id);
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
