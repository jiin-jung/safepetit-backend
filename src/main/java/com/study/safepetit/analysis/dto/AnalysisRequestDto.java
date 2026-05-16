package com.study.safepetit.analysis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
    @Setter
    public class AnalysisRequestDto {

        private LocalDate birthDate;

        private Double childHeight;

        private String childGender;
    }

