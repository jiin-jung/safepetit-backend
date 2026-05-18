package com.study.safepetit.analysis.controller;

import com.study.safepetit.analysis.dto.AnalysisRequestDto;
import com.study.safepetit.analysis.dto.AnalysisResponseDto;
import com.study.safepetit.analysis.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://ideaton-team1-frontend-6hkc.vercel.app")
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final GeminiService geminiService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 안전 분석")
    public ResponseEntity<AnalysisResponseDto> analyze(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate birthDate,
            @RequestParam Double childHeight,
            @RequestParam String childGender,
            @RequestPart("image") MultipartFile image
    ) {
        AnalysisRequestDto request = new AnalysisRequestDto();

        request.setBirthDate(birthDate);
        request.setChildHeight(childHeight);
        request.setChildGender(childGender);

        AnalysisResponseDto result = geminiService.analyzeSafety(request, image);

        return ResponseEntity.ok(result);
    }


}