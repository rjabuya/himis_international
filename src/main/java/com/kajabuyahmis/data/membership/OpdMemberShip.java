/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.membership;

import com.kajabuyahmis.entity.PriceMatrix;
import com.kajabuyahmis.entity.membership.MembershipScheme;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author safrin
 */
public class OpdMemberShip {

    MembershipScheme membershipScheme;
    List<PriceMatrix> priceMatrixs;

    public MembershipScheme getMembershipScheme() {
        return membershipScheme;
    }

    public void setMembershipScheme(MembershipScheme membershipScheme) {
        this.membershipScheme = membershipScheme;
    }
    
  
    public List<PriceMatrix> getPriceMatrixs() {
        if (priceMatrixs == null) {
            priceMatrixs = new ArrayList<>();
        }
        return priceMatrixs;
    }

    public void setPriceMatrixs(List<PriceMatrix> priceMatrixs) {
        this.priceMatrixs = priceMatrixs;
    }

}
