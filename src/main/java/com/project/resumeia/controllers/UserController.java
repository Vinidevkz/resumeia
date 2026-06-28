package com.project.resumeia.controllers;

import com.project.resumeia.dto.LoginDTO;
import com.project.resumeia.dto.TokenResponseDTO;
import com.project.resumeia.dto.UpdateDTO;
import com.project.resumeia.dto.UserDTO;
import com.project.resumeia.entities.User;
import com.project.resumeia.repositories.UserRepository;
import com.project.resumeia.services.AuthenticationService;
import com.project.resumeia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    /*
    @GetMapping("/test")
    public String testApi(){
        return "API funcionando!";
    }
    */

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registerUser(@Validated @RequestBody UserDTO requestBodyDto) throws Exception{
        return authenticationService.registerUser(requestBodyDto);
    }

    @PostMapping("/login")
    public TokenResponseDTO loginUser(@Validated @RequestBody LoginDTO loginDTO) throws Exception{
        return authenticationService.login(loginDTO);
    }

    @PutMapping("/updateUserData")
    public ResponseEntity<Void> updateUser(@Validated @RequestBody UpdateDTO updateDTO, Authentication authentication) throws Exception {
        Long userId = (Long) authentication.getDetails();

        userService.updateUser(updateDTO.name(), updateDTO.age(), userId);

        return ResponseEntity.noContent().build();
    }


}
