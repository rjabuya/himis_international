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
public class PreBill extends Bill implements Serializable {

    public PreBill() {
        billClassType = BillClassType.PreBill;
        qty = 1;
    }
//    private static final long serialVersionUID = 1L;

}
