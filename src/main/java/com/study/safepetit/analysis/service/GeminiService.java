package com.study.safepetit.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.safepetit.analysis.dto.AnalysisRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeSafety(AnalysisRequestDto dto, MultipartFile image) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            String prompt = """
                    당신은 영유아 홈 세이프티 전문가입니다.

                    아이 정보:
                    - 월령: %d개월
                    - 키: %.1fcm
                    - 성별: %s

                    업로드된 실내 사진을 분석해서 아이에게 위험할 수 있는 요소를 찾아주세요.

                    답변 형식:
                    1. 전체 안전 점수: 100점 만점
                    2. 주요 위험 요소
                    3. 왜 위험한지
                    4. 보호자가 바로 할 수 있는 조치
                    5. 한 줄 요약
                    """.formatted(
                    dto.getChildAgeMonth(),
                    dto.getChildHeight(),
                    dto.getChildGender()
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[]{
                            Map.of(
                                    "parts", new Object[]{
                                            Map.of("text", prompt),
                                            Map.of(
                                                    "inline_data", Map.of(
                                                            "mime_type", image.getContentType(),
                                                            "data", base64Image
                                                    )
                                            )
                                    }
                            )
                    }
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            String response = WebClient.create()
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Gemini 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
