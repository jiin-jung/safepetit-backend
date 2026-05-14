package com.study.safepetit.analysis.controller;

import com.study.safepetit.analysis.dto.AnalysisRequestDto;
import com.study.safepetit.analysis.dto.AnalysisResponseDto;
import com.study.safepetit.analysis.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")

public class AnalysisController {
    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<AnalysisResponseDto> analyze(
            @RequestPart("request") AnalysisRequestDto request,
            @RequestPart("image") MultipartFile image
    ) {
        String result = geminiService.analyzeSafety(request, image);
        return ResponseEntity.ok(new AnalysisResponseDto(result));
    }
}
