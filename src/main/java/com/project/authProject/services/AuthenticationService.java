package com.project.authProject.services;

import com.project.authProject.config.TokenProvider;
import com.project.authProject.dto.LoginDTO;
import com.project.authProject.dto.TokenResponseDTO;
import com.project.authProject.dto.UserDTO;
import com.project.authProject.entities.RolesEntity;
import com.project.authProject.entities.User;
import com.project.authProject.enums.RoleTypes;
import com.project.authProject.repositories.RolesRepository;
import com.project.authProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    @Value("${jwt.expiration}")
    private long expiration;

    private final AuthenticationManager authenticationManager;

    public ResponseEntity<User> registerUser(@RequestBody @Validated UserDTO user) throws BadRequestException {
        User userExist = (User) userRepository.findByEmailIgnoreCase(user.email()).orElse(null);

        if(userExist != null){
            throw new BadRequestException("O usuário já existe no sistema.");
        }

        RolesEntity role = rolesRepository.findByNome(RoleTypes.ROLE_ALUNO.name()).orElseGet(()
                -> rolesRepository.save(RolesEntity.builder()
                .nome(RoleTypes.ROLE_ALUNO.name())
                .build()
        ));
        //conversao json -> dto
        User newUser = new User();
        newUser.setName(user.nome());
        newUser.setEmail(user.email());
        newUser.setRoles(Set.of(role));
        //criptografando a senha:
        newUser.setPassword(passwordEncoder.encode(user.senha()));

        User savedUser = userRepository.save(newUser);

        return ResponseEntity.ok().body(savedUser);


    }

    public TokenResponseDTO login(LoginDTO loginDTO) throws Exception{
        try{
            //manager -> provider - userDetailsService -> passwordEncoderMatches
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.senha()));
            String token = tokenProvider.generateToken(authentication);

            return new TokenResponseDTO(token, expiration);
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Credenciais inválidas.");
        }catch(Exception e){
            throw e;
        }
    }


}
