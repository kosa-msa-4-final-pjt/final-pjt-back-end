package com.kosa.chanzipup.api.estimate.controller.response;

import com.kosa.chanzipup.domain.estimate.Estimate;
import com.kosa.chanzipup.domain.estimate.EstimateRequest;
import com.kosa.chanzipup.domain.estimate.EstimateStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class EstimateRequestResponse {

    private Long requestId;

    private String address;

    private String fullAddress;

    private Integer floor;

    private String budget;

    private String schedule;

    private String nickName;

    private String buildingTypeName;

    private LocalDateTime regDate;

    private List<String> constructionTypes;

    private boolean isSend;

    private EstimateStatus status;

    private Long estimateId;

    public EstimateRequestResponse(EstimateRequest estimateRequest, boolean isSend) {
        this.requestId = estimateRequest.getId();
        this.address = estimateRequest.getAddress();
        this.fullAddress = estimateRequest.getFullAddress();
        this.floor = estimateRequest.getFloor();
        this.budget = estimateRequest.getBudget();
        this.schedule = estimateRequest.getSchedule();
        this.nickName = estimateRequest.getMember().getNickName();
        this.buildingTypeName = estimateRequest.getBuildingType().getName();
        this.regDate = estimateRequest.getRegDate();
        this.constructionTypes = estimateRequest.getConstructionTypes()
                .stream()
                .map(type -> type.getTypeName())
                .toList();
        this.isSend = isSend;
    }


    public EstimateRequestResponse(Estimate estimate) {
        this(estimate.getEstimateRequest(), false);
        status = null;
    }

    public EstimateRequestResponse(EstimateRequest estimateRequest, List<Estimate> requestEstimates) {
        this(estimateRequest, requestEstimates != null && !requestEstimates.isEmpty());
        if (isSend) {
            Estimate estimate = requestEstimates.get(0);
            status = estimate.getEstimateStatus();
            estimateId = estimate.getId();
        } else {
            status = null;
        }
    }
}