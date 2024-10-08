package com.kosa.chanzipup.api.estimate.controller;

import com.kosa.chanzipup.api.ApiResponse;
import com.kosa.chanzipup.api.estimate.controller.request.EstimatePriceRequest;
import com.kosa.chanzipup.api.estimate.controller.request.EstimateRegisterRequest;
import com.kosa.chanzipup.api.estimate.controller.response.EstimateResult;
import com.kosa.chanzipup.api.estimate.controller.response.EstimateUpdateResponse;
import com.kosa.chanzipup.api.estimate.service.query.EstimateQueryService;
import com.kosa.chanzipup.config.security.userdetail.UnifiedUserDetails;
import com.kosa.chanzipup.domain.account.company.CompanyId;
import com.kosa.chanzipup.domain.estimate.EstimateRequest;
import com.kosa.chanzipup.api.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/estimates")
public class EstimateController {

    private final EstimateService estimateService;
    private final EstimateQueryService queryService;

    @PostMapping("/send")
    public ApiResponse<EstimateResult> sendEstimateToCompany(@RequestBody EstimateRegisterRequest request,
                                                             @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        // 견적 요청 사용자
        String userEmail = userDetails.getUsername();
        EstimateResult estimateResult = estimateService.sendEstimateToCompany(userEmail, request);
        return ApiResponse.ok(estimateResult);
    }

    @GetMapping("/request/latest")
    public ApiResponse<Long> getLatestEstimateRequest(@AuthenticationPrincipal UnifiedUserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        EstimateRequest latestEstimateRequest = estimateService.getLatestEstimateRequestByUserEmail(userEmail);
        return ApiResponse.ok(latestEstimateRequest.getId());
    }


    @PostMapping("/{estimateRequestId}/cancel")
    public ApiResponse<Void> cancelEstimate(@PathVariable Long estimateRequestId,
                                            @AuthenticationPrincipal UnifiedUserDetails userDetails,
                                            @RequestBody CompanyId companyId) {
        // 로그인한 업체 이메일을 가져옴
        String memberEmail = userDetails.getUsername();

        // 서비스 호출 시 estimateRequestId와 companyEmail을 넘김
        estimateService.rejectEstimateByMember(estimateRequestId, memberEmail, companyId.getCompanyId());
        return ApiResponse.ok(null);
    }

    @PostMapping("/{estimateRequestId}/approval")
    public ApiResponse<Void> approvalEstimate(@PathVariable Long estimateRequestId,
                                              @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        // 로그인한 업체 이메일을 가져옴
        String companyEmail = userDetails.getUsername();

        // 서비스 호출 시 estimateRequestId와 companyEmail을 넘김
        estimateService.approvalEstimateByRequestIdAndCompanyEmail(estimateRequestId, companyEmail);
        return ApiResponse.ok(null);
    }


    @GetMapping("/{estimateId}")
    @PreAuthorize("ROLE_COMPANY")
    public ResponseEntity<EstimateUpdateResponse> getUpdateEstimateData(@PathVariable("estimateId") Long estimateId,
                                                                        @AuthenticationPrincipal UnifiedUserDetails userDetails) {

        return ResponseEntity.ok(queryService.updateEstimateResponse(estimateId, userDetails.getUsername()));
    }

    @DeleteMapping("/{estimateId}")
    @PreAuthorize("ROLE_COMPANY")
    public ResponseEntity<Boolean> deleteEstimate(@PathVariable("estimateId") Long estimateId,
                                                  @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        return ResponseEntity.ok(estimateService.deleteEstimate(estimateId, userDetails.getUsername()));
    }

    @PatchMapping("/{estimateId}")
    @PreAuthorize("ROLE_COMPANY")
    public ResponseEntity<Boolean> updateEstimate(@PathVariable("estimateId") Long estimateId,
                                                  @RequestBody EstimatePriceRequest request,
                                                  @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        return ResponseEntity.ok(estimateService.updateEstimate(estimateId, userDetails.getUsername(), request));
    }
}
