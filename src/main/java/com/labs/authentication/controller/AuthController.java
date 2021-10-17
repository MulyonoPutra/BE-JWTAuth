package com.labs.authentication.controller;

import com.labs.authentication.dto.JSONWebTokenDTO;
import com.labs.authentication.dto.LoginDTO;
import com.labs.authentication.dto.Messages;
import com.labs.authentication.dto.RegisterDTO;
import com.labs.authentication.entity.Role;
import com.labs.authentication.entity.Users;
import com.labs.authentication.enums.RoleName;
import com.labs.authentication.security.JWT.JWTProvider;
import com.labs.authentication.service.RoleService;
import com.labs.authentication.service.UserService;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserService userService;

  @Autowired
  RoleService roleService;

  @Autowired
  JWTProvider jwtProvider;

  @PostMapping("/register")
  public ResponseEntity<?> register(
    @Valid @RequestBody RegisterDTO registerDTO,
    BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity(
        new Messages("Wrong fields or invalid email"),
        HttpStatus.BAD_REQUEST
      );
    }

    if (userService.existsByNameUser(registerDTO.getUsername())) {
      return new ResponseEntity(
        new Messages("that name already exists"),
        HttpStatus.BAD_REQUEST
      );
    }

    if (userService.existsByEmail(registerDTO.getEmail())) {
      return new ResponseEntity(
        new Messages("that email already exists"),
        HttpStatus.BAD_REQUEST
      );
    }

    Users users = new Users(
      registerDTO.getName(),
      registerDTO.getUsername(),
      registerDTO.getEmail(),
      passwordEncoder.encode(registerDTO.getPassword())
    );
    Set<Role> roles = new HashSet<>();
    roles.add(roleService.getByRolName(RoleName.ROLE_USER).get());

    if (registerDTO.getRoles().contains("admin")) {
      roles.add(roleService.getByRolName(RoleName.ROLE_ADMIN).get());
    }
    users.setRoles(roles);
    userService.save(users);
    return new ResponseEntity(new Messages("user saved"), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<JSONWebTokenDTO> login(
    @Valid @RequestBody LoginDTO loginDTO,
    BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity(
        new Messages("badly placed fields"),
        HttpStatus.BAD_REQUEST
      );
    }

    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginDTO.getUsername(),
        loginDTO.getPassword()
      )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtProvider.generateToken(authentication);
    JSONWebTokenDTO jwtDto = new JSONWebTokenDTO(jwt);
    return new ResponseEntity(jwtDto, HttpStatus.OK);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JSONWebTokenDTO> refresh(
    @RequestBody JSONWebTokenDTO jwtDto
  )
    throws ParseException {
    String token = jwtProvider.refreshToken(jwtDto);
    JSONWebTokenDTO jwt = new JSONWebTokenDTO(token);
    return new ResponseEntity(jwt, HttpStatus.OK);
  }
}
