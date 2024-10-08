package com.kosa.chanzipup.api.payment.controller.response;

import com.kosa.chanzipup.domain.account.company.Company;
import com.kosa.chanzipup.domain.membership.MembershipName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentPrepareResponse {

    private String merchantUid;
    private MembershipName type;
    private int price;
    private PaymentCompany paymentCompany;


    private PaymentPrepareResponse(String merchantUid, MembershipName type, PaymentCompany paymentCompany) {
        this.merchantUid = merchantUid;
        this.type = type;
        this.price = type.getPrice();
        this.paymentCompany = paymentCompany;
    }

    public static PaymentPrepareResponse of(String merchantUid, MembershipName type, Company company) {
        return new PaymentPrepareResponse(merchantUid, type, PaymentCompany.of(company));
    }
}
//imp_955552843949
//imp_824746492309
//imp_897864716868