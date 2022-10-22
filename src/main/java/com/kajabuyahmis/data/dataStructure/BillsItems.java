/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.dataStructure;

import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillItem;
import java.util.List;

/**
 *
 * @author safrin
 */
public class BillsItems {
    private Bill bill;
    private List<BillItem> billItems;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
}
