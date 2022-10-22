/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.lab;

import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.facade.BillFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Buddhika
 */
@Named
@SessionScoped
public class LabBillEditController implements Serializable {

    /**
     * Creates a new instance of LabBillEditController
     */
    public LabBillEditController() {
    }

    Bill bill;
    @EJB
    BillFacade billFacade;

    public void updateBill() {
        if (bill == null) {
            UtilityController.addErrorMessage("Select a bill");
            return;
        }
        getBillFacade().edit(bill);
        UtilityController.addErrorMessage("Updates");
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

}
