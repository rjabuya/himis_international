/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity;

import com.kajabuyahmis.data.BillClassType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class CancelledBill extends Bill implements Serializable {

    public CancelledBill() {
        billClassType = BillClassType.CancelledBill;
        qty = 0 - 1;
    }

}
