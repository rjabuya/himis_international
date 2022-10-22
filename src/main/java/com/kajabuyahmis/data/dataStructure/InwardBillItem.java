/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */

package com.kajabuyahmis.data.dataStructure;


import com.kajabuyahmis.data.inward.InwardChargeType;
import com.kajabuyahmis.entity.BillItem;
import java.util.List;

/**
 *
 * @author safrin
 */
public class InwardBillItem {
    private InwardChargeType inwardChargeType;
    private List<BillItem> billItems;

    public InwardChargeType getInwardChargeType() {
        return inwardChargeType;
    }

    public void setInwardChargeType(InwardChargeType inwardChargeType) {
        this.inwardChargeType = inwardChargeType;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
}
