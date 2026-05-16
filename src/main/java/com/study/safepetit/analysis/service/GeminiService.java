package com.study.safepetit.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.safepetit.analysis.dto.AnalysisRequestDto;
import com.study.safepetit.analysis.dto.AnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisResponseDto analyzeSafety(AnalysisRequestDto dto, MultipartFile image) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            int childAgeMonth = calculateMonthAge(dto.getBirthDate());

            String prompt = """
                    당신은 영유아 주거 환경 안전 분석 전문가입니다.

                    [아이 정보]
                    - 월령: %d개월
                    - 키: %.1fcm
                    - 성별: %s

                    업로드된 사진을 분석하여 아이의 발달 단계에 따른 위험 요소를 파악하세요.

                    반드시 아래 JSON 형식으로만 응답하세요.
                    마크다운 코드 블록, 설명 문장, ```json 표시는 절대 넣지 마세요.
                    순수 JSON 텍스트만 출력하세요.

                    {
                      "safety_score": 0,
                      "hazard_count": 0,
                      "hazards": [
                        {
                          "id": 1,
                          "title": "위험 요소 제목",
                          "location": "위험 요소의 대략적인 위치",
                          "description": "카드에 보여줄 짧은 설명",
                          "custom_analysis": "현재 아이의 월령과 키를 고려했을 때 왜 위험한지 친절하게 분석",
                          "solutions": ["해결 방법 1", "해결 방법 2", "해결 방법 3"],
                          "recommended_items": ["추천 물품 1", "추천 물품 2"]
                        }
                      ]
                    }

                    응답 규칙:
                    - hazard_count는 실제 발견된 위험 요소 개수입니다.
                    - hazards 배열은 반드시 길이가 5여야 합니다.
                    - 위험 요소가 5개보다 적으면 남은 자리는 null로 채우세요.
                    - title은 "콘센트 위험", "전선 노출"처럼 짧게 작성하세요.
                    - description은 카드 미리보기용으로 한 문장만 작성하세요.
                    - custom_analysis에는 아이의 월령과 키를 반영하세요.
                    - solutions는 최대 3개까지만 작성하세요.
                    - recommended_items는 최대 2개까지만 작성하세요.
                    """.formatted(
                    childAgeMonth,
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

            String aiText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            aiText = cleanJsonText(aiText);

            return objectMapper.readValue(aiText, AnalysisResponseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Gemini 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String cleanJsonText(String text) {
        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
    private int calculateMonthAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() * 12
                + Period.between(birthDate, LocalDate.now()).getMonths();
    }
}
