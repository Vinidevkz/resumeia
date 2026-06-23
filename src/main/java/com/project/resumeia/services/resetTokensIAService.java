package com.project.resumeia.services;

import com.project.resumeia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class resetTokensIAService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Sao_Paulo")
    @Transactional
    public void resetDailyTokens(){
        userRepository.resetAllSessions();
    }
}
