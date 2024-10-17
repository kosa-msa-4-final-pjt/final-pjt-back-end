package com.kosa.chanzipup.application;

import com.kosa.chanzipup.domain.account.AccountRole;
import com.kosa.chanzipup.domain.account.company.Company;
import com.kosa.chanzipup.domain.account.company.CompanyRepository;
import com.kosa.chanzipup.domain.account.member.Member;
import com.kosa.chanzipup.domain.account.member.MemberRepository;
import com.kosa.chanzipup.domain.account.member.MemberType;
import com.kosa.chanzipup.domain.buildingtype.BuildingType;
import com.kosa.chanzipup.domain.buildingtype.BuildingTypeRepository;
import com.kosa.chanzipup.domain.constructiontype.ConstructionType;
import com.kosa.chanzipup.domain.constructiontype.ConstructionTypeRepository;
import com.kosa.chanzipup.domain.membership.Membership;
import com.kosa.chanzipup.domain.membership.MembershipName;
import com.kosa.chanzipup.domain.membership.MembershipRepository;
import com.kosa.chanzipup.domain.membership.MembershipType;
import com.kosa.chanzipup.domain.membership.MembershipTypeRepository;
import com.kosa.chanzipup.domain.review.Review;
import com.kosa.chanzipup.domain.review.ReviewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationSetUp {
    private final MembershipTypeRepository membershipTypeRepository;
    private final ConstructionTypeRepository constructionTypeRepository;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final BuildingTypeRepository buildingTypeRepository;
    private final ReviewRepository reviewRepository;
    private final MembershipRepository membershipRepository;


    //@PostConstruct
    public void addCompany() {
        Company company = Company.ofNewCompanyForTest("test10@test.com", "집다부셔", encoder.encode("qweqwe123!"),
                "010-2344-3333", "집다부셔버려", "1234", LocalDate.of(2020, 04, 30),
                "서울역 4번 출구", "다부셔버려");


        Company company2 = Company.ofNewCompanyForTest("test11@test.com", "집다부셔", encoder.encode("qweqwe123!"),
                "010-2344-3333", "집다부셔버려", "1234", LocalDate.of(2020, 04, 30),
                "서울역 4번 출구", "다부셔버려");

        companyRepository.saveAll(List.of(company, company2));
    }


//    @PostConstruct
    public void init() {
        ConstructionType type1 = new ConstructionType("창호/샷시");
        ConstructionType type2 = new ConstructionType("도배/페인트");
        ConstructionType type3 = new ConstructionType("욕실");
        ConstructionType type4 = new ConstructionType("주방");
        ConstructionType type5 = new ConstructionType("장판");
        ConstructionType type6 = new ConstructionType("마루");
        ConstructionType type7 = new ConstructionType("전기/조명");
        ConstructionType type8 = new ConstructionType("필름");
        ConstructionType type9 = new ConstructionType("중문/도어");
        constructionTypeRepository.saveAll(List.of(
                type1, type2, type3, type4, type5, type6, type7, type8, type9
        ));

        BuildingType buildingType1 = new BuildingType("아파트");
        BuildingType buildingType2 = new BuildingType("빌라");
        BuildingType buildingType3 = new BuildingType("주택");
        BuildingType buildingType4 = new BuildingType("오피스텔");
        BuildingType buildingType5 = new BuildingType("상업");
        buildingTypeRepository.saveAll(List.of(
                buildingType1, buildingType2, buildingType3, buildingType4, buildingType5
        ));

        MembershipType basic = new MembershipType(100, MembershipName.BASIC);
        MembershipType premium = new MembershipType(150, MembershipName.PREMIUM);

        membershipTypeRepository.save(basic);
        membershipTypeRepository.save(premium);

        Member member1 = Member.ofLocalForTest(AccountRole.USER, "test1@test.com", encoder.encode("qweqwe123!"),
                "010-9393-0303",
                MemberType.LOCAL, "testNickName1", "Oh1");
        Member member2 = Member.ofLocalForTest(AccountRole.USER, "test2@test.com", encoder.encode("qweqwe123!"),
                "010-9393-0304",
                MemberType.LOCAL, "testNickName2", "Oh2");
        Member member3 = Member.ofLocalForTest(AccountRole.ADMIN, "admin@changzipup.com", encoder.encode("qweqwe123!"),
                "010-9393-0304",
                MemberType.LOCAL, "체인집업관리자", "체인집업");

        memberRepository.saveAll(List.of(member1, member2, member3));

        Company company = Company.ofNewCompanyForTest("test3@test.com", "집다부셔", encoder.encode("qweqwe123!"),
                "010-2344-3333", "집다부셔버려", "1234", LocalDate.of(2020, 04, 30),
                "서울역 4번 출구", "다부셔버려");
        Company company2 = Company.ofNewCompanyForTest("test4@test.com", "집다부셔2", encoder.encode("qweqwe123!"),
                "010-2344-4444", "집다부셔버려2", "1234", LocalDate.of(2020, 04, 30),
                "서울역 4번 출구", "다부셔버려2");
        Company company3 = Company.ofNewCompanyForTest("test5@test.com", "집다부셔3", encoder.encode("qweqwe123!"),
                "010-2344-4444", "집다부셔버려3", "12344", LocalDate.of(2020, 04, 30),
                "서울역 4번 출구", "다부셔버려3");

        companyRepository.saveAll(List.of(company, company2, company3));

        Membership membership1 = Membership.ofNewMembership(company2, basic, LocalDateTime.now(), LocalDateTime.now().plusDays(30), null);
        Membership membership2 = Membership.ofNewMembership(company3, premium, LocalDateTime.now(), LocalDateTime.now().plusDays(30), null);

        membershipRepository.saveAll(List.of(membership1, membership2));

        // 리뷰 임의 데이터 두개 넣었어요. content는 비워뒀어요.
        Review review1 = Review.ofNewReview(
                "리뷰 제목 1", LocalDateTime.now(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1),
                5, member1, company, buildingType1, null, 1000000L, 10
        );

        Review review2 = Review.ofNewReview(
                "리뷰 제목 2", LocalDateTime.now(), LocalDate.of(2023, 3, 1), LocalDate.of(2023, 4, 1),
                4, member2, company2, buildingType2, null, 1500000L, 15
        );

        reviewRepository.saveAll(List.of(review1, review2));
    }
}
