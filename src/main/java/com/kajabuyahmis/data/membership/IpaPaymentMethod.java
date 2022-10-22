/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.membership;

import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.entity.PriceMatrix;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author safrin
 */
public class IpaPaymentMethod {

    PaymentMethod paymentMethod;
    List<PriceMatrix> priceMatrixs;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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
