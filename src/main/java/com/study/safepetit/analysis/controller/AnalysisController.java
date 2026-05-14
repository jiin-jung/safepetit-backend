package com.study.safepetit.analysis.controller;

import com.study.safepetit.analysis.dto.AnalysisRequestDto;
import com.study.safepetit.analysis.dto.AnalysisResponseDto;
import com.study.safepetit.analysis.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")

public class AnalysisController {
    private final GeminiService geminiService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "이미지 안전 분석")
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

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
