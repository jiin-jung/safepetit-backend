package com.study.safepetit.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisResponseDto {

    @JsonProperty("safety_score")
    private Integer safetyScore;

    @JsonProperty("hazard_count")
    private Integer hazardCount;

    private List<HazardDto> hazards;
}