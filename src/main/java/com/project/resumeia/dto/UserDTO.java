package com.project.resumeia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotBlank(message = "O nome é obrigatorio.") String name,

        @NotBlank(message = "O email é obrigatório.") String email,

        @NotBlank(message = "O senha é obrigatoria.") String password,

        @NotNull(message = "O idade é obrigatória.") Integer age
){}
