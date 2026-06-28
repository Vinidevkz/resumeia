package com.project.resumeia.services;

import com.project.resumeia.entities.User;
import com.project.resumeia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public Boolean getUserSessions(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        return user.getTokensPerDay() < 2;

    }

    @Transactional
    public void addSession(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            user.setTokensPerDay(user.getTokensPerDay() + 1);

        }
    }

    @Transactional
    public void updateUser(String newName, Integer newAge, Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não contrado."));

        user.setName(newName);
        user.setAge(newAge);


    }

}
