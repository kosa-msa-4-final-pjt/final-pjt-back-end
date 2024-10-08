package com.kosa.chanzipup.domain.estimate;

import com.kosa.chanzipup.domain.BaseEntity;
import com.kosa.chanzipup.domain.account.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Table(name = "estimate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Estimate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstimateStatus estimateStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_request_id", nullable = false)
    private EstimateRequest estimateRequest;

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimatePrice> estimatePrices = new ArrayList<>();

    @Builder
    public Estimate(EstimateStatus estimateStatus,
                    Company company, EstimateRequest estimateRequest,
                    List<EstimateConstructionType> estimateConstructionTypes,
                    Map<Long, Integer> estimatePrices) {
        this.estimateStatus = estimateStatus;
        this.company = company;
        this.estimateRequest = estimateRequest;
        this.estimatePrices.addAll(
                toEstimatePrices(estimateConstructionTypes, estimatePrices)
        );
    }

    private List<EstimatePrice> toEstimatePrices(List<EstimateConstructionType> estimateConstructionTypes,
                                                 Map<Long, Integer> estimatePrices) {
        return estimateConstructionTypes
                .stream()
                .map(type -> new EstimatePrice(this, type, estimatePrices.get(type.getId())))
                .toList();
    }


    public static Estimate sent(Company company, EstimateRequest request,
                                List<EstimateConstructionType> estimateConstructionTypes,
                                Map<Long, Integer> constructionPrices) {
        return Estimate.builder()
                .company(company)
                .estimateRequest(request)
                .estimateStatus(EstimateStatus.SENT)
                .estimateConstructionTypes(estimateConstructionTypes)
                .estimatePrices(constructionPrices)
                .build();
    }


    public static Estimate received(Company company, EstimateRequest request) {
        return Estimate
                .builder()
                .company(company)
                .estimateStatus(EstimateStatus.RECEIVED)
                .estimateRequest(request)
                .estimateConstructionTypes(Collections.emptyList())
                .estimatePrices(Collections.emptyMap())
                .build();
    }

    public void updateEstimateStatus(EstimateStatus newStatus) {
        this.estimateStatus = newStatus;
    }

    public void updatePrices(Map<Long, Integer> constructionPrices) {
        updateEstimateStatus(EstimateStatus.SENT);
        this.estimatePrices.stream()
                .forEach(price -> price.updatePrice(constructionPrices.get(price.getId())));
    }

    public void updatePrices(List<EstimateConstructionType> constructionTypes, Map<Long, Integer> constructionPrices) {
        updateEstimateStatus(EstimateStatus.SENT);
        this.estimatePrices.addAll(toEstimatePrices(constructionTypes, constructionPrices));
    }

    public int getTotalPrices() {
        if (estimatePrices.isEmpty()) {
            return 0;
        }

        return estimatePrices
                .stream()
                .mapToInt(EstimatePrice::getPrice)
                .sum();
    }

    public void accept() {
        this.estimateStatus = EstimateStatus.ACCEPTED;
        estimateRequest.ongoing();
    }

    public void reject() {
        this.estimateStatus = EstimateStatus.REJECTED;
    }

    public void cancel() {
        this.estimateStatus = EstimateStatus.REJECTED;
    }

    public void complete() {
        this.estimateStatus = EstimateStatus.COMPLETE;
    }
}
