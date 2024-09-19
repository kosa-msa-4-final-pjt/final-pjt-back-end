package com.kosa.chanzipup.api.estimate.service.query;

import com.kosa.chanzipup.api.estimate.controller.response.EstimateConstructionResponse;
import com.kosa.chanzipup.api.estimate.controller.response.EstimateRequestResponse;
import com.kosa.chanzipup.domain.estimate.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kosa.chanzipup.domain.account.member.QMember.member;
import static com.kosa.chanzipup.domain.buildingtype.QBuildingType.buildingType;
import static com.kosa.chanzipup.domain.constructiontype.QConstructionType.constructionType;
import static com.kosa.chanzipup.domain.estimate.QEstimateConstructionType.estimateConstructionType;
import static com.kosa.chanzipup.domain.estimate.QEstimateRequest.estimateRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstimateRequestQueryService {
    private final JPAQueryFactory factory;

    public List<EstimateRequestResponse> getEstimateRequestResponsesOnWaiting() {

        List<EstimateRequest> fetch = factory.select(estimateRequest)
                .from(estimateRequest)
                .leftJoin(estimateRequest.member, member).fetchJoin()
                .leftJoin(estimateRequest.buildingType, buildingType).fetchJoin()
                .leftJoin(estimateRequest.constructionTypes, estimateConstructionType).fetchJoin()
                .leftJoin(estimateConstructionType.constructionType, constructionType).fetchJoin()
                .where(estimateRequest.status.eq(EstimateRequestStatus.WAITING))
                .fetch();

        return fetch.stream()
                .map(estimateRequest -> new EstimateRequestResponse(estimateRequest))
                .toList();
    }

    public List<EstimateConstructionResponse> getEstimatePriceDetail(Long estimateRequestId) {


        Optional<EstimateRequest> findRequest = Optional.of(factory.select(estimateRequest)
                .from(estimateRequest)
                .leftJoin(estimateRequest.constructionTypes, estimateConstructionType)
                .leftJoin(estimateConstructionType.constructionType, constructionType)
                .where(estimateRequest.id.eq(estimateRequestId))
                .fetchOne());

        List<EstimateConstructionResponse> estimateConstructionResponses = findRequest
                .map(request -> request.getConstructionTypes()
                        .stream()
                        .map(type -> new EstimateConstructionResponse(type.getId(), type.getTypeName()))
                        .toList())
                .orElse(null);

        return estimateConstructionResponses;
    }

}
