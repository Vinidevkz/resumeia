package com.project.resumeia.services;

import com.project.resumeia.entities.User;
import com.project.resumeia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public void addSession(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            user.setTokensPerDay(user.getTokensPerDay() + 1);

            userRepository.save(user);
        }


    }

}
