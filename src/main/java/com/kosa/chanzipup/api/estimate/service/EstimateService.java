package com.kosa.chanzipup.api.estimate.service;

import com.kosa.chanzipup.api.estimate.controller.request.EstimatePriceRequest;
import com.kosa.chanzipup.api.estimate.controller.request.EstimateRegisterRequest;
import com.kosa.chanzipup.api.estimate.controller.response.EstimateResult;
import com.kosa.chanzipup.domain.account.company.Company;
import com.kosa.chanzipup.domain.account.company.CompanyRepository;
import com.kosa.chanzipup.domain.account.member.Member;
import com.kosa.chanzipup.domain.account.member.MemberRepository;
import com.kosa.chanzipup.domain.estimate.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EstimateService {

    private final EstimateRequestRepository estimateRequestRepository;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;
    private final EstimateRepository estimateRepository;
    private final EstimatePriceRepository estimatePriceRepository;

    // 회사에 요청 견적을 보낸다.
    @Transactional
    public EstimateResult sendEstimateToCompany(String userEmail,
                                      EstimateRegisterRequest request) {
        // 1. 요청한 유저
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음."));

        // 2. 회사 정보 가져오기
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("회사 정보 없음."));

        // 3. 견적 요청 정보 가져오기
        EstimateRequest estimateRequest = estimateRequestRepository.findByIdWithUser(request.getEstimateRequestId())
                .orElseThrow(() -> new IllegalArgumentException("요청 정보 없음."));

        // 4. 유저가 등록한 요청인지 확인
        if (isNotRequestedMember(estimateRequest, member)) {
            throw new IllegalArgumentException("유저가 등록한 요청이 아닙니다.");
        }

        // 5. 견적 요청을 회사에 전송
        // 고객이 보냈으니, 회사 입장에서 받은 것.
        Estimate estimate = Estimate.received(company, estimateRequest);
        estimateRepository.save(estimate);

        return EstimateResult.of(company, estimateRequest, estimate);
    }

    // 요청한 유저가 맞는지 확인
    private boolean isNotRequestedMember(EstimateRequest estimateRequest, Member member) {
        return !(estimateRequest.getMember() == member);
    }

    // 특정 업체에게 온 견적 요청을 조회
    public List<EstimateResult> getWaitingEstimatesByCompanyEmail(String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new IllegalArgumentException("업체 정보 없음."));

        List<Estimate> estimates = estimateRepository.findAllWaitingByCompany(company);

        return estimates.stream()
                .map(estimate -> EstimateResult.of(estimate.getCompany(), estimate.getEstimateRequest(), estimate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstimateRequest getLatestEstimateRequestByUserEmail(String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음."));

        return estimateRequestRepository.findFirstByMemberOrderByRegDateDesc(member)
                .orElseThrow(() -> new IllegalArgumentException("최근 견적 요청 정보 없음."));
    }


    @Transactional
    public void rejectEstimateByMember(Long estimateRequestId, String memberEmail, Long companyId) {

        // companyEmail을 통해 회사 정보 조회
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일 정보 없음: " + memberEmail));

        // estimateRequestId와 company 정보를 기반으로 Estimate 찾기
        Estimate estimate = estimateRepository.findByEstimateRequestIdAndCompanyId(estimateRequestId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청에 대한 견적이 존재하지 않거나 권한이 없습니다: " + estimateRequestId));

        // 상태를 CANCELLATION로 업데이트
        estimate.updateEstimateStatus(EstimateStatus.REJECTED);

        // 업데이트된 견적을 저장
        estimateRepository.save(estimate);
    }


    @Transactional
    public void rejectEstimateByCompany(Long estimateRequestId, String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사 정보입니다."));

        Estimate estimate = estimateRepository.findByEstimateRequestIdAndCompanyId(estimateRequestId, company.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청에 대한 견적이 존재하지 않거나 권한이 없습니다: " + estimateRequestId));

    }

    @Transactional
    public void approvalEstimateByRequestIdAndCompanyEmail(Long estimateRequestId, String companyEmail) {
        // companyEmail을 통해 회사 정보 조회
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 업체 정보 없음: " + companyEmail));

        // estimateRequestId와 company 정보를 기반으로 Estimate 찾기
        Estimate estimate = estimateRepository.findByEstimateRequestIdAndCompany(estimateRequestId, company)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청에 대한 견적이 존재하지 않거나 권한이 없습니다: " + estimateRequestId));

        // 상태를 ONGOING로 업데이트
        estimate.updateEstimateStatus(EstimateStatus.ACCEPTED);

        // 업데이트된 견적을 저장
        estimateRepository.save(estimate);
    }

    // todo: 현재는 상태만 변경하는데, 맞는 로직을 작성
    @Transactional
    public void acceptEstimate(Long requestId, Long estimateId) {
        // 1. request가 ongoing 상태로 된다.
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 견적 정보입니다."));

        // 2. estimate accepted 상태가 된다.
        estimate.accept();
    }

    @Transactional
    public void rejectEstimate(Long requestId, Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 견적 정보입니다."));

        estimate.reject();
    }

    @Transactional
    public boolean deleteEstimate(Long estimateId, String companyEmail) {
        Estimate estimate = estimateRepository.findByIdAndCompanyEmail(estimateId, companyEmail)
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 요청입니다."));

        estimatePriceRepository.deleteByEstimateId(estimate.getId());
        estimateRepository.deleteById(estimate.getId());
        return true;
    }

    @Transactional
    public Boolean updateEstimate(Long estimateId,
                                  String username,
                                  EstimatePriceRequest request) {
        Estimate estimate = estimateRepository.findByIdWithPrices(estimateId, username)
                .orElseThrow(() -> new IllegalArgumentException("수정 불가"));

        estimate.updatePrices(request.getConstructionPrices());

        return true;
    }
}
