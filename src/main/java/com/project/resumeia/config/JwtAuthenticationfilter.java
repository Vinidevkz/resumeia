package com.project.resumeia.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationfilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException
    {

        if (request.getRequestURI().startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

          String authorizationHeader = request.getHeader("Authorization");

          if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
              //validar token

              //!!
              String token = authorizationHeader.substring(7);

              if(tokenProvider.isTokenValid(token)){
                  String username = tokenProvider.getUsername(token);
                  Long userId = tokenProvider.getUserId(token);

                  UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                  UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                  //id injection
                  authenticationToken.setDetails(userId);

                  SecurityContextHolder.getContext().setAuthentication(authenticationToken);

              }
          }

          filterChain.doFilter(request, response);
    }
}
