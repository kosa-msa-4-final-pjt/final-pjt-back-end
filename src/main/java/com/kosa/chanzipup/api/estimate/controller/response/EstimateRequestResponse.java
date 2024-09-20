package com.kosa.chanzipup.api.estimate.controller.response;

import com.kosa.chanzipup.domain.estimate.Estimate;
import com.kosa.chanzipup.domain.estimate.EstimateRequest;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EstimateRequestResponse {

    private Long requestId;

    private String fullAddress;

    private Integer floor;

    private String budget;

    private String schedule;

    private String nickName;

    private String buildingTypeName;

    private LocalDateTime regDate;

    private List<String> constructionTypes;

    private boolean isSend;



    public EstimateRequestResponse(EstimateRequest estimateRequest) {
        this.requestId = estimateRequest.getId();
        this.fullAddress = estimateRequest.getFullAddress();
        this.floor = estimateRequest.getFloor();
        this.budget = estimateRequest.getBudget();
        this.schedule = estimateRequest.getSchedule();
        this.nickName = estimateRequest.getMember().getNickName();
        this.buildingTypeName = estimateRequest.getBuildingType().getName();
        this.regDate = estimateRequest.getRegDate(); ;
        this.constructionTypes = estimateRequest.getConstructionTypes()
                .stream()
                .map(type -> type.getTypeName())
                .toList();
    }

    public EstimateRequestResponse(Estimate estimate, String companyEmail) {
        this(estimate.getEstimateRequest());
        this.isSend = estimate.getCompany().getEmail().equals(companyEmail);
    }
}