package com.project.authProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "O nome é obrigatorio.")
        String nome,

        @NotBlank(message = "email é obrigatório.") String email,

        @NotBlank(message = "senha é obrigatoria.")
        String senha
){}
