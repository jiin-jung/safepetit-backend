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
            @RequestParam Integer childAgeMonth,
            @RequestParam Double childHeight,
            @RequestParam String childGender,
            @RequestPart("image") MultipartFile image
    ) {
        AnalysisRequestDto request = new AnalysisRequestDto();

        request.setChildAgeMonth(childAgeMonth);
        request.setChildHeight(childHeight);
        request.setChildGender(childGender);

        String result = geminiService.analyzeSafety(request, image);

        return ResponseEntity.ok(new AnalysisResponseDto(result));
    }
}
