package com.study.safepetit.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    public class HazardDto {

        private Integer id;

        private String title;

        private String location;

        private String description;

        @JsonProperty("custom_analysis")
        private String customAnalysis;

        private List<String> solutions;

        @JsonProperty("recommended_items")
        private List<String> recommendedItems;
    }

