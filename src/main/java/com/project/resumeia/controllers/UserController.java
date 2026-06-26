package com.project.resumeia.controllers;

import com.project.resumeia.dto.LoginDTO;
import com.project.resumeia.dto.TokenResponseDTO;
import com.project.resumeia.dto.UserDTO;
import com.project.resumeia.entities.User;
import com.project.resumeia.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    /*
    @GetMapping("/test")
    public String testApi(){
        return "API funcionando!";
    }
    */

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Validated @RequestBody UserDTO requestBodyDto) throws Exception{
        return authenticationService.registerUser(requestBodyDto);
    }

    @PostMapping("/login")
    public TokenResponseDTO loginUser(@Validated @RequestBody LoginDTO loginDTO) throws Exception{
        return authenticationService.login(loginDTO);
    }


}
