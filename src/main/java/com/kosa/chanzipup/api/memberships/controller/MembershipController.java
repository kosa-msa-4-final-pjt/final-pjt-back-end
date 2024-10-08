package com.kosa.chanzipup.api.memberships.controller;

import com.kosa.chanzipup.api.ApiResponse;
import com.kosa.chanzipup.api.memberships.controller.response.MembershipHistories;
import com.kosa.chanzipup.api.memberships.controller.response.MembershipResponse;
import com.kosa.chanzipup.api.memberships.service.MembershipService;
import com.kosa.chanzipup.api.payment.controller.request.PaymentConfirmation;
import com.kosa.chanzipup.api.payment.service.PaymentService;
import com.kosa.chanzipup.config.security.userdetail.UnifiedUserDetails;
import com.kosa.chanzipup.domain.membership.MembershipRegisterException;
import com.kosa.chanzipup.domain.payment.PaymentResult;

import com.kosa.chanzipup.domain.payment.RefundService;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memberships")
@Slf4j
public class MembershipController {

    private final MembershipService membershipService;

    private final PaymentService paymentService;

    private final RefundService refundService;

    @PostMapping
    public ApiResponse<MembershipResponse> subscribeToMembership(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestBody @Valid PaymentConfirmation paymentResult) {
        // 1. 생성했던 결제 정보를 처리한다.(성공 시, 실패 여부를 반환)
        PaymentResult processResult = paymentService.processPayment(paymentResult.getImpUid(), paymentResult.getMerchantUid(),
                paymentResult.getPaidAmount(), paymentResult.getSuccess(), userDetails.getUsername());

        // 2. 성공적으로 결제하면 이때부터 한달 간, 멤버십에 가입된다.
        // 실패하면 예외가 발생한다.
        if (!processResult.isSuccess()) {
            throw new MembershipRegisterException("멤버십 등록에 실패하였습니다.");
        }
        return ApiResponse.ok(membershipService.registerMembership(processResult));
    }


    @GetMapping
    @PreAuthorize("ROLE_COMPANY")
    public ResponseEntity<List<MembershipHistories>> getAllMembershipHistories(@AuthenticationPrincipal
                                                                               UnifiedUserDetails userDetails) {
        return ResponseEntity.ok(membershipService
                .getAllMembershipHistories(userDetails.getUsername()));
    }


    @GetMapping("/isjoin")
    public ResponseEntity<Boolean> isMembershipCompany(@AuthenticationPrincipal UnifiedUserDetails userDetails) {
        return ResponseEntity.ok(membershipService.isMembershipCompany(userDetails.getUsername()));
    }


    @PatchMapping("/{membershipId}/refund")
    public ResponseEntity<Boolean> refundMembership(@AuthenticationPrincipal UnifiedUserDetails userDetails,
                                                    @PathVariable("membershipId") Long membershipId) {
        String uid = membershipService.refundMembership(membershipId);
        refundService.refundBy(uid);
        return ResponseEntity.ok(true);
    }

}