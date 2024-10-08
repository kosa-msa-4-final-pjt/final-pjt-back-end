package com.kosa.chanzipup.api.admin.controller.response.portfolio;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PortfolioDetailResponse {

    private Long id;

    private String title;

    @Lob
    private String content;

    private int floor;

    private int projectBudget;

    private String projectLocation;

    private LocalDate startDate;

    private LocalDate endDate;

    private String buildingType;

    private List<String> services;

    private Long companyId;

    private String companyName;

    private String companyAddress;

    private String companyPhone;

    private String companyLogo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}

