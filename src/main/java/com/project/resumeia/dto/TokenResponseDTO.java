package com.project.resumeia.dto;

public record TokenResponseDTO(String userName, String userEmail, Integer userAge, String token, long expiration) {
}
