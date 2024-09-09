package com.sos.app.services;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sos.app.dtos.User.CreateUserRequest;
import com.sos.app.dtos.User.UpdateUserRequest;
import com.sos.app.dtos.User.UserDto;
import com.sos.app.models.UserModel;
import com.sos.app.repository.RoleRepository;
import com.sos.app.repository.UserRepository;
import com.sos.app.services.exceptions.DataIntegrityException;
import com.sos.app.services.exceptions.NotFoundException;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
        });
    }

    public UserDto findById(Long id) {
        try {
            UserModel userModel = userRepository.findById(id).get();

            UserDto userResponseDto = modelMapper.map(userModel, UserDto.class);

            return userResponseDto;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + UserModel.class.getName());
        }
    }

    public UserModel newUser(CreateUserRequest user) {
        var roleDto = roleRepository.findById(user.getRole());

        var userFromDb = userRepository.findByUsername(user.getUsername());
        
        if (userFromDb.isPresent()) {
            throw new DataIntegrityException("Usuário já existe");
        }

        UserModel userModel = new UserModel();
        userModel.setUsername(user.getUsername());
        userModel.setPassword(passwordEncoder.encode(user.getPassword()));
        userModel.setIdRole(roleDto.get().getId());
        userModel.setName(user.getName());

        userRepository.save(userModel);

        return userModel;
    }

     public UserDto updateUser(UpdateUserRequest user, Long id ) {
        try {
            Optional<UserModel> userExist = userRepository.findById(id);
            
            if (userExist.isPresent()) {
                UserModel userUpdated = userExist.get();

                modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                modelMapper.map(user, userUpdated);

                userUpdated = userRepository.save(userUpdated);

                return modelMapper.map(userUpdated, UserDto.class);
            }else{
                throw new DataIntegrityException("O Id do Usuário não existe na base de dados!");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Campo(s) obrigatório(s) da Pessoa não foi(foram) preenchido(s).");
        }
    }

    public void deleteById(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
            }else {
                throw new DataIntegrityException("O Id do Usuário não existe na base de dados!");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possível excluir a Pessoa!");
        }
    }
}
