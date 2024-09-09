package com.sos.app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.dtos.User.UserDto;
import com.sos.app.dtos.User.CreateUserRequest;
import com.sos.app.dtos.User.UpdateUserRequest;
import com.sos.app.models.UserModel;
import com.sos.app.repository.RoleRepository;
import com.sos.app.repository.UserRepository;
import com.sos.app.services.UserService;
import com.sos.app.services.exceptions.ConstraintException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  UserService userService;

  @Autowired
  BCryptPasswordEncoder passwordEncoder;

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PostMapping
  public ResponseEntity<UserModel> newUser(@Valid @RequestBody CreateUserRequest dto) {
    UserModel user = userService.newUser(dto);
    return ResponseEntity.ok().body(user);
  }

  @GetMapping
  public ResponseEntity<Page<UserDto>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<UserDto> usersPage = userService.findAll(pageable);
    return ResponseEntity.ok(usersPage);
  }

  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
    var users = userService.findById(id);
    return ResponseEntity.ok(users);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<UserDto> updateById(@Valid @RequestBody UpdateUserRequest userRequest,
      @PathVariable("id") Long id, BindingResult br) {

    if (br.hasErrors()) {
      List<String> errors = new ArrayList<>();
      br.getAllErrors().forEach(e -> {
        errors.add(e.getDefaultMessage());
      });

      throw new ConstraintException("Dados incorretos!", errors);
    }

    UserDto personDto = userService.updateUser(userRequest, id);
    return ResponseEntity.ok().body(personDto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
