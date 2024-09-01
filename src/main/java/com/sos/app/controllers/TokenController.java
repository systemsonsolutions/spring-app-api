package com.sos.app.controllers;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.dtos.Auth.LoginResponse;
import com.sos.app.dtos.Auth.LoginRequest;
import com.sos.app.models.RoleModel;
import com.sos.app.models.UserModel;
import com.sos.app.repository.RoleRepository;
import com.sos.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TokenController {
  private final JwtEncoder jwtEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    Optional<UserModel> user = userRepository.findByUsername(loginRequest.username());
    Optional<RoleModel> role = roleRepository.findById(user.get().getIdRole());

    if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
      throw new BadCredentialsException("user or password is invalid!");
    }

    Instant now = Instant.now();
    Long expiresIn = 7200L;

    RoleModel roleModel = roleRepository.findById(user.get().getIdRole()).get();
    var scopes = roleModel.getName();

    var claims = JwtClaimsSet.builder()
        .issuer("mybackend")
        .subject(user.get().getId().toString())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiresIn))
        .claim("scope", scopes)
        .build();

    var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    return ResponseEntity.ok(
        new LoginResponse(jwtValue, expiresIn, loginRequest.username(), user.get().getName(), role.get().getName()));
  }
}