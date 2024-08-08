package com.sos.app.controllers;

import java.time.Instant;
import java.util.stream.Collectors;

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

import com.sos.app.controllers.dto.LoginRequest;
import com.sos.app.controllers.dto.LoginResponse;
import com.sos.app.models.Role;
import com.sos.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TokenController {
  private final JwtEncoder jwtEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

    var user = userRepository.findByUsername(loginRequest.username());

    if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
      throw new BadCredentialsException("user or password is invalid!");
    }

    var now = Instant.now();
    var expiresIn = 300L;

    var scopes = user.get().getRoles()
        .stream()
        .map(Role::getName)
        .collect(Collectors.joining(" "));

    var claims = JwtClaimsSet.builder()
        .issuer("mybackend")
        .subject(user.get().getUserId().toString())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiresIn))
        .claim("scope", scopes)
        .build();

    var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn, loginRequest.username(), user.get().getName()));
  }
}
