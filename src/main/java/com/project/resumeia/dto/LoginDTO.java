package com.project.resumeia.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank(message = "email é obrigatório.")
    String email,
    @NotBlank(message = "senha é obrigatoria.")
    String senha
){}
