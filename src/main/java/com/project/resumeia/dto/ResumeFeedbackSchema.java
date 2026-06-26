package com.project.resumeia.dto;

import java.util.List;

public record ResumeFeedbackSchema(
        int note,
        List<String> strengths,
        List<String> weaknesses,
        String finalFeedback
){}
