package com.project.resumeia.config;

import com.project.resumeia.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.key}")
    private String key;

    //gerar token
    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();



        return buildToken(user.getId(), user.getEmail());
    }

    private String buildToken(Long userId, String username){

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        //inserindo informações no token:
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    //validar token

    public Boolean isTokenValid(String token){
        try{
            getClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private Claims getClaims(String token){
        //validar assinatura
        //validar expiracao
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //extrair email do token
    public String getUsername(String token){
        return getClaims(token).getSubject();
    }

    //extrair id do token
    public Long getUserId(String token){
        Claims claims = getClaims(token);

        return claims.get("userId", Long.class);
    };

}
