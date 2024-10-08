package com.kosa.chanzipup.api.admin.service;

import com.kosa.chanzipup.api.admin.controller.request.AccountSearchCondition;
import com.kosa.chanzipup.api.admin.controller.response.company.AdminCompanyResponse;
import com.kosa.chanzipup.api.admin.controller.response.member.AdminMemberResponse;
import com.kosa.chanzipup.application.Page;
import com.kosa.chanzipup.domain.account.AccountRole;
import com.kosa.chanzipup.domain.account.AdminAccountRepository;
import com.kosa.chanzipup.domain.account.company.Company;
import com.kosa.chanzipup.domain.account.company.CompanyRepository;
import com.kosa.chanzipup.domain.account.member.Member;
import com.kosa.chanzipup.domain.account.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AdminAccountRepository adminAccountRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;

    public void getAllAccounts(Pageable pageable, AccountSearchCondition condition) {


    }

    // 모든 멤버 조회
    public Page<List<AdminMemberResponse>> getAllMembers(int pageSize, int pageNumber) {
        List<Member> members = memberRepository.findByAccountRole(AccountRole.USER);

        return Page.of(members.stream()
                .map(member -> new AdminMemberResponse(
                        member.getId(),
                        member.getName(),
                        member.getNickName(),
                        member.getEmail(),
                        member.getCreatedDateTime().toLocalDate(),
                        member.getPhoneNumber(),
                        member.isVerified()
                ))
                .collect(Collectors.toList()),pageSize, pageNumber);
    }

    // 특정 멤버 상세 조회
    public Optional<AdminMemberResponse> getMemberDetail(Long id) {
        return memberRepository.findById(id)
                .map(member -> new AdminMemberResponse(
                        member.getId(),
                        member.getName(),
                        member.getNickName(),
                        member.getEmail(),
                        member.getCreatedDateTime().toLocalDate(),
                        member.getPhoneNumber(),
                        member.isVerified()
                ));
    }

    // 모든 회사 조회
    public Page<List<AdminCompanyResponse>> getAllCompanies(int pageNumber, int pageSize) {
        List<Company> companies = companyRepository.findAll();

        return Page.of(companies.stream()
                .map(company -> new AdminCompanyResponse(
                        company.getId(),
                        company.getCompanyName(),
                        company.getCompanyNumber(),
                        company.getEmail(),
                        company.getPublishDate(),
                        company.getPhoneNumber(),
                        company.isVerified(),
                        company.getCompanyLogoUrl(),
                        company.getAddress(),
                        company.getOwner(),
                        company.getRating(),
                        company.getActiveMembership() != null ? company.getActiveMembership().getMembershipName().name() : null,
                        company.getCreatedDateTime().toLocalDate(),
                        company.getConstructionTypes().stream()
                                .map(companyConstructionType -> companyConstructionType.getConstructionType().getName()) // CompanyConstructionType에서 ConstructionType 추출
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList()), pageSize, pageNumber);
    }

    // 특정 회사 상세 조회
    public Optional<AdminCompanyResponse> getCompanyDetail(Long id) {
        return companyRepository.findById(id)
                .map(company -> new AdminCompanyResponse(
                        company.getId(),
                        company.getCompanyName(),
                        company.getCompanyNumber(),
                        company.getEmail(),
                        company.getPublishDate(),
                        company.getPhoneNumber(),
                        company.isVerified(),
                        company.getCompanyLogoUrl(),
                        company.getAddress(),
                        company.getOwner(),
                        company.getRating(),
                        company.getActiveMembership() != null ? company.getActiveMembership().getMembershipName().name() : null,
                        company.getCreatedDateTime().toLocalDate(),
                        company.getConstructionTypes().stream()
                                .map(companyConstructionType -> companyConstructionType.getConstructionType().getName()) // CompanyConstructionType에서 ConstructionType 추출
                                .collect(Collectors.toList())
                ));
    }
}