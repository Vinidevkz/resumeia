package com.project.authProject.controllers;

import com.project.authProject.dto.LoginDTO;
import com.project.authProject.dto.TokenResponseDTO;
import com.project.authProject.dto.UserDTO;
import com.project.authProject.entities.RolesEntity;
import com.project.authProject.entities.User;
import com.project.authProject.enums.RoleTypes;
import com.project.authProject.repositories.RolesRepository;
import com.project.authProject.repositories.UserRepository;
import com.project.authProject.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping("/test")
    public String testApi(){
        return "API funcionando!";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Validated @RequestBody UserDTO requestBodyDto) throws Exception{
        return authenticationService.registerUser(requestBodyDto);
    }

    @PostMapping("/login")
    public TokenResponseDTO loginUser(@Validated @RequestBody LoginDTO loginDTO) throws Exception{
        return authenticationService.login(loginDTO);
    }


}
