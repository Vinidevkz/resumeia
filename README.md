![Resume IA](https://raw.githubusercontent.com/Vinidevkz/resumeia/main/src/src/resumeia.png)

<h4 align="center">Obtenha feedbacks precisos sobre curriculos.</h3>

## 🚀 Sobre o projeto
O **Resume IA** é uma plataforma de avaliação de currículos baseada em Inteligência Artificial. Os usuários podem fazer o upload de seus currículos e receber um feedback detalhado envolvendo análise da formatação, nota, pontos fortes e pontos a melhorar, a fim de otimizar sua estrutura e se destacar no mercado de trabalho.

O foco principal deste projeto foi ampliar meu repertório no ecossistema backend com **Java** e **Spring Boot**, além de explorar o desenvolvimento integrado com modelos de IA. Para esse desafio, utilizei a API do **Google Gemini** através do novo `RestClient` do Spring, aproveitando seus recursos avançados de *Vision* (análise multimodal) e *Structured Outputs* (respostas forçadas em JSON estruturado).

A segurança também recebeu atenção especial: implementei o **Spring Security** com autenticação via **Tokens JWT** para simular um cenário real de ambiente de produção, protegendo as rotas de análise e limitando as cotas de requisições diárias por usuário.

## ⚙️ Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Framework Principal:** Spring Boot 3.x (Spring Security, Spring Data JPA)
* **Banco de Dados:** PostgreSQL
* **Integração IA:** Google Gemini API (Modelo `gemini-1.5-flash` / `gemini-2.5-flash`)
* **Processamento de Arquivos:** Apache PDFBox (Conversão de PDF para Imagem)

## 📝 API Reference

### 👤 Usuário:

#### • Cadastro de usuário

```http
  POST /v1/auth/users/regiser
```

| Parametros         | Type   |
|:-------------------|:-------|
| `dados do usuário` | `json` |

| Retorno            | Tipo          |
|:-------------------|:--------------|
| `Status`           | `201 created` |

***>> O usuário precisará logar após cadastrado.***

#### • Login de usuário

```http
  POST /v1/auth/users/login
```

| Parametros      | Type     |
|:----------------|:---------|
| `email e senha` | `json`   |

| Retorno              | Tipo      |
|:---------------------|:----------|
| `status`             | `200 ok`  |
| `dados do usuario`   | `json`    |
| `token JWT`          | `String`  |
| `tempo de expiração` | `Integer` |

#### • Alteração de dados do usuário

```http
  POST /v1/auth/users/update
```
| Parametros                     | Type     |
|:-------------------------------|:---------|
| `json com os dados do usuario` | `json`   |
| `token`                        | `string` |

| Retorno            | Tipo     |
|:-------------------|:---------|
| `Status`           | `204 no content` |

#### • Deletar usuário

```http
  POST /v1/auth/users/delete
```
| Parametros | Type     |
|:-----------|:---------|
| `token`    | `String` |

| Retorno            | Tipo             |
|:-------------------|:-----------------|
| `Status`           | `204 no content` |

### ⭐ Currículo e a integração com a IA do Gemini:

#### • Envio de currículo

```http
  POST /v1/resumeia/resume/send
```

| Parametros         | Type     |
|:-------------------|:---------|
| `token`            | `String` |
| `curriculo em pdf` | `file`   | 

| Retorno | Type |
| :-------- | :------|
| `feedback da ia`      | `string` | 

***Métodos para realidar a chamada da IA:***

```
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
    
    // variável de ambiente da url da ia
    @Value("${gemini.api.url}")
    private String geminiUrl;

    // variável de ambiente da key da ia
    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    //método para conbverter arquivo em imagem
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

        //prompt com instruções para a ia nos retornar o esperado:
        String prompt = "Você é um recrutador profissional e especialista em RH. "
                + "Analise a imagem deste currículo e retorne um feedback detalhado com: "
                + "1) Dicas de como melhorar a estrutura visual e organização. "
                + "2) Críticas e melhorias sobre as informações e textos apresentados. "
                + "3) Uma nota final de 0 a 100 baseada na qualidade geral do currículo.";
        
        //tipo do arquivo que enviaremos
        Map<String, Object> inlineData = Map.of(
                "mimeType", "image/png",
                "data",bs64img
        );
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> imagePart = Map.of("inlineData", inlineData);
        Map<String, Object> parts = Map.of("parts", List.of(textPart, imagePart));
        List<Object> contents = List.of(parts);

        //schema (formato que queremos a resposta em JSON):
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

```
***➡️ Sobre o retorno da IA:*** O feedback vem no seguinte formato:

```
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "{\n  \"nota\": 55,\n  \"pontosFortes\": [\n    \"Objetivo de carreira claro e alinhado à vaga de estágio,
            demonstrando foco.\",\n    \"Listagem de tecnologias relevantes e abrangentes para desenvolvimento backend
            (Java, Spring Framework, MySQL, PostgreSQL, Python, JavaScript).\",\n    \"Presença de projetos práticos com links para o
            GitHub, o que é um diferencial forte e comprova a aplicação de conhecimentos.\",\n    \"Experiência como Monitor Acadêmico,
            indicando proatividade, habilidades de comunicação e domínio do conteúdo.\"\n  ],\n  \"pontosFracos\": [\n    \"Datas inconsistentes e futuras: O ponto mais crítico é a menção de datas futuras em seções como Experiência (\\\"Março 2026 - presente\\\"), Formação (\\\"Agosto 2025 - Junho 2028\\\") e Certificados (\\\"2025\\\", \\\"2026\\\"). Isso gera grande confusão e falta de credibilidade, podendo levar ao descarte imediato.\",\n
            \"Formato visual muito básico: O currículo é funcional, mas carece de um design mais moderno, com melhor uso de hierarquia visual
            (títulos, subtítulos, negritos) e espaçamento para facilitar a leitura e o destaque de informações importantes.\",\n
            \"Descrições verbosas: A descrição da experiência como Monitor Acadêmico poderia ser mais concisa e focada em resultados ou
            competências-chave desenvolvidas que sejam diretamente aplicáveis à área de desenvolvimento.\",\n
            \"Detalhes da Formação: Os sub-itens listados sob a formação superior (\\\"Lógica de programação\\\", etc.)
            podem ser redundantes. Se forem competências fortes, talvez devessem estar na seção de \\\"Competências\\\" ou integrados na descrição do curso.\"\n  ],\n  
            \"avaliacaoGeral\": \"O currículo de Vinicius demonstra um bom potencial para a área de desenvolvimento backend, com um objetivo claro e relevante para uma
            posição de estágio. A lista de tecnologias e, principalmente, os projetos com links para o GitHub são excelentes e evidenciam a proatividade e a capacidade de 
            aplicar conhecimentos práticos, o que é muito valorizado. No entanto, o problema recorrente e crítico com as datas (mencionando eventos futuros como se já estivessem acontecendo ou fossem passados) 
            compromete severamente a credibilidade e a clareza do documento, sendo o principal ponto de atenção. A estrutura visual é funcional,
            mas muito básica, e poderia ser aprimorada para facilitar a leitura e destacar as informações mais importantes. Recomendo fortemente a revisão e
            correção de todas as datas para garantir a precisão e a coerência, além de uma otimização do layout e da concisão das descrições para tornar o
            currículo mais impactante e profissional.\"\n}"
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 338,
    "candidatesTokenCount": 598,
    "totalTokenCount": 4099,
    "promptTokensDetails": [
      {
        "modality": "TEXT",
        "tokenCount": 80
      },
      {
        "modality": "IMAGE",
        "tokenCount": 258
      }
    ],
    "thoughtsTokenCount": 3163,
    "serviceTier": "standard"
  },
  "modelVersion": "gemini-2.5-flash",
  "responseId": "8j1AapSUBJTg8QGAusfQCQ"
}



```

• O JSON é dividido em 3 sessões: nota, pontos fortes, pontos fracos e resumo geral, facilitando ao front-end apenas filtrar na String cada parte para formatá-lo.  

## 🔒 Sobre a segurança: 

A aplicação utiliza o *Spring Security* na versão *7.1.0*. Nas rotas de ***cadastro*** e ***login*** ***não são necessários tokens JWT***, sendo rotas livres de segurança até o momento. Porém, para as demais rotas, como a de ***enviar currículos*** já se torna como parâmetro obrigatório o envio de um ***token válido***.

### Tokens JWT: Formato

Os Tokens JWT são como ***"chaves de segurança"*** para as aplicações web, eles são criptografados e suas informações estão contidas dentro deles. Escolhi enviar alguns dados adicionais, como o ***id do usuário***, a ***data de criação*** e a ***data de espiração***.


