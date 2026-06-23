package com.project.resumeia.controllers;

import com.project.resumeia.services.ResumeService;
import com.project.resumeia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/resume")
@RequiredArgsConstructor
public class ResumeController {

    @Autowired
    private final ResumeService resumeService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> uploadResume(@RequestParam("file")MultipartFile file, @RequestParam("userId") Long id){

        Boolean userHaveTokens = userService.getUserSessions(id);

        if(!userHaveTokens){
            return ResponseEntity.badRequest().body("Quantidade de sessões máxima atingida por hoje!");
        }

        try{
            byte[] imageBytes;
            String contentType = file.getContentType();

            if("application/pdf".equals(contentType)){
                imageBytes = resumeService.convertDocToImg(file);
            }else if("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)){
                return ResponseEntity.badRequest().body("Para análise visual, por enquanto envie apenas PDF.");
            }else{
                return ResponseEntity.badRequest().body("Formato não suportado.");
            }

            String feedback = resumeService.iaFeedbackGemini(imageBytes);

            userService.addSession(id);
            return ResponseEntity.ok(feedback);
        }catch (Exception e){
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }


}
