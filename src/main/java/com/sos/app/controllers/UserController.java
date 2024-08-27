package com.sos.app.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sos.app.controllers.dto.CreateUserDto;
import com.sos.app.models.Role;
import com.sos.app.models.User;
import com.sos.app.repository.RoleRepository;
import com.sos.app.repository.UserRepository;

@RestController
public class UserController {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository,
      RoleRepository roleRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PostMapping("/users")
  public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto) {

    var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
    var adminRole = roleRepository.findByName(Role.Values.ADMIN.name());

    var userFromDb = userRepository.findByUsername(dto.username());
    if (userFromDb.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    var user = new User();
    user.setUsername(dto.username());
    user.setName(dto.name());
    user.setPassword(passwordEncoder.encode(dto.password()));

    if (dto.role().equals("ADMIN")) {
      user.setRoles(Set.of(adminRole));
    } else {
      user.setRoles(Set.of(basicRole));
    }

    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<User>> listUsers() {
    var users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping("/users/{id}")
  public ResponseEntity<Object> getUser(@PathVariable(value = "id") UUID id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") UUID id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
    userRepository.deleteById(userOptional.get().getUserId());
    // userRepository.delete(userOptional.get());

    return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @PutMapping("/users/{id}")
  public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody CreateUserDto dto) {

    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    User user = userOptional.get();
    user.setName(dto.name());
    user.setUsername(dto.username());

    if (dto.password() != null && !dto.password().isEmpty()) {
      user.setPassword(passwordEncoder.encode(dto.password()));
    }

    var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
    var adminRole = roleRepository.findByName(Role.Values.ADMIN.name());

    if (basicRole == null || adminRole == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role not found.");
    }

    Set<Role> roles = new HashSet<>();
    if (dto.role().equals("ADMIN")) {
      roles.add(adminRole);
    } else {
      roles.add(basicRole);
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

}