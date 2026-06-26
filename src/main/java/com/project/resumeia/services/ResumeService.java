package com.project.resumeia.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ResumeService {

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public byte[] convertDocToImg(MultipartFile file) throws IOException{

        try(PDDocument document = Loader.loadPDF(file.getBytes())){
            PDFRenderer pdfRenderer= new PDFRenderer(document);

            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bim, "png", baos);
            return baos.toByteArray();

        }catch (Exception e){
            throw new IOException(e);
        }
    }

    public String iaFeedbackGemini(byte[] imageBytes){
        String bs64img = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = "Você é um recrutador profissional e especialista em RH. "
                + "Analise a imagem deste currículo e retorne um feedback detalhado com: "
                + "1) Dicas de como melhorar a estrutura visual e organização. "
                + "2) Críticas e melhorias sobre as informações e textos apresentados. "
                + "3) Uma nota final de 0 a 100 baseada na qualidade geral do currículo.";

        Map<String, Object> inlineData = Map.of(
                "mimeType", "image/png",
                "data",bs64img
        );
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> imagePart = Map.of("inlineData", inlineData);
        Map<String, Object> parts = Map.of("parts", List.of(textPart, imagePart));
        List<Object> contents = List.of(parts);

        //schema
        Map<String, Object> responseSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "nota", Map.of("type", "INTEGER", "description", "Nota de 0 a 100 para o currículo"),
                        "pontosFortes", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"), "description", "Lista com pontos fortes do currículo"),
                        "pontosFracos", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"), "description", "Lista com pontos de atenção ou melhoria"),
                        "avaliacaoGeral", Map.of("type", "STRING", "description", "Texto descritivo com a avaliação do recrutador")
                ),
                "required", List.of("nota", "pontosFortes", "pontosFracos", "avaliacaoGeral")
        );

        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json", // Obriga o Gemini a responder JSON
                "responseSchema", responseSchema
        );

        Map<String, Object> body = Map.of(
                "contents", contents,
                "generationConfig", generationConfig
        );

        try{
            String response = restClient.post()
                    .uri(geminiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Falha ao se comunicar com a API da IA: " + e.getMessage());
        }

    }
}
