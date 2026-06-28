package com.project.resumeia.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDTO(
        String name,
        Integer age

)

{}
