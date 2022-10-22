/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.pharmacy;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.PharmacyBean;
import com.kajabuyahmis.ejb.PharmacyCalculation;
import com.kajabuyahmis.ejb.StaffBean;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.RefundBill;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalBillItem;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.PharmaceuticalBillItemFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class PreReturnController implements Serializable {

    private Bill bill;
    private Bill returnBill;
    private boolean printPreview;
    ////////

    private List<BillItem> billItems;
    ///////
    @EJB
    private PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade;
    @Inject
    private PharmaceuticalItemController pharmaceuticalItemController;
    @Inject
    private PharmacyController pharmacyController;
    @Inject
    private SessionController sessionController;
    @EJB
    private BillNumberGenerator billNumberBean;
    @EJB
    private BillFacade billFacade;
    @EJB
    private PharmacyBean pharmacyBean;
    @EJB
    private BillItemFacade billItemFacade;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        makeNull();

        if (bill.getDepartment() == null) {
            return;
        }

        if (!getSessionController().getDepartment().equals(bill.getDepartment())) {
            UtilityController.addErrorMessage("U can't return another department's Issue.please log to specific department");
            return;
        }

        this.bill = bill;
        generateBillComponent(BillType.PharmacyPre);
    }

    public Bill getReturnBill() {
        if (returnBill == null) {
            returnBill = new RefundBill();
//            returnBill.setBillType(BillType.PharmacyPre);

        }

        return returnBill;
    }

    public void setReturnBill(Bill returnBill) {
        this.returnBill = returnBill;
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    @Inject
    private PharmacyCalculation pharmacyRecieveBean;

    public void onEdit(BillItem tmp) {
        //    PharmaceuticalBillItem tmp = (PharmaceuticalBillItem) event.getObject();

        if (tmp.getQty() > getPharmacyRecieveBean().calQty3(tmp.getReferanceBillItem())) {
            tmp.setQty(0.0);
            UtilityController.addErrorMessage("You cant return over than ballanced Qty ");
        }

        calTotal();
        //   getPharmacyController().setPharmacyItem(tmp.getPharmaceuticalBillItem().getBillItem().getItem());
    }

    public void makeNull() {
        bill = null;
        returnBill = null;
        printPreview = false;
        billItems = null;

    }

    private void saveReturnBill() {

        getReturnBill().copy(getBill());
        getReturnBill().setBilledBill(getBill());
        double dbl = 0 - getReturnBill().getTotal();

        getReturnBill().setBillType(BillType.PharmacyPre);
        getReturnBill().setTotal(dbl);
        getReturnBill().setNetTotal(dbl);

        getReturnBill().setCreater(getSessionController().getLoggedUser());
        getReturnBill().setCreatedAt(Calendar.getInstance().getTime());

        getReturnBill().setDepartment(getSessionController().getDepartment());
        getReturnBill().setInstitution(getSessionController().getInstitution());

        getReturnBill().setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), BillType.PharmacyPre, BillClassType.RefundBill, BillNumberSuffix.PHRET));
        getReturnBill().setDeptId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getDepartment(), BillType.PharmacyPre, BillClassType.RefundBill, BillNumberSuffix.PHRET));

        //   getReturnBill().setInsId(getBill().getInsId());
        if (getReturnBill().getId() == null) {
            getBillFacade().create(getReturnBill());
        }

    }

    private void saveComponent() {
        for (BillItem i : getBillItems()) {
            i.getPharmaceuticalBillItem().setQtyInUnit((double) (double) i.getQty());

            if (i.getPharmaceuticalBillItem().getQty() == 0.0) {
                continue;
            }

            i.setBill(getReturnBill());
            i.setCreatedAt(Calendar.getInstance().getTime());
            i.setCreater(getSessionController().getLoggedUser());
            i.setQty((double) i.getPharmaceuticalBillItem().getQty());

            double value = i.getNetRate() * i.getQty();
            i.setGrossValue(0 - value);
            i.setNetValue(0 - value);

            PharmaceuticalBillItem tmpPh = i.getPharmaceuticalBillItem();
            i.setPharmaceuticalBillItem(null);

            if (i.getId() == null) {
                getBillItemFacade().create(i);
            }

            if (tmpPh.getId() == null) {
                getPharmaceuticalBillItemFacade().create(tmpPh);
            }

            i.setPharmaceuticalBillItem(tmpPh);
            getBillItemFacade().edit(i);

            //   getPharmaceuticalBillItemFacade().edit(i.getPharmaceuticalBillItem());
            //System.err.println("STOCK " + i.getPharmaceuticalBillItem().getStock());
            getPharmacyBean().addToStock(i.getPharmaceuticalBillItem().getStock(), Math.abs(i.getPharmaceuticalBillItem().getQtyInUnit()), i.getPharmaceuticalBillItem(), getSessionController().getDepartment());

            //   i.getBillItem().getTmpReferenceBillItem().getPharmaceuticalBillItem().setRemainingQty(i.getRemainingQty() - i.getQty());
            //   getPharmaceuticalBillItemFacade().edit(i.getBillItem().getTmpReferenceBillItem().getPharmaceuticalBillItem());
            //      updateRemainingQty(i);
            getReturnBill().getBillItems().add(i);
        }

    }

    @EJB
    StaffBean staffBean;

    public void settle() {

        if (getReturnBill().getTotal() == 0) {
            UtilityController.addErrorMessage("Total is Zero cant' return");
            return;
        }

        saveReturnBill();
        saveComponent();

        getBillFacade().edit(getReturnBill());

//        getBill().getReturnPreBills().add(getReturnBill());
        getBillFacade().edit(getBill());

//        if (getReturnBill().getPaymentMethod() == PaymentMethod.Credit) {
//            //   ////// // System.out.println("getBill().getPaymentMethod() = " + getBill().getPaymentMethod());
//            //   ////// // System.out.println("getBill().getToStaff() = " + getBill().getToStaff());
//            if (getBill().getToStaff() != null) {
//                //   ////// // System.out.println("getBill().getNetTotal() = " + getBill().getNetTotal());
//                staffBean.updateStaffCredit(getBill().getToStaff(), getReturnBill().getNetTotal());
//                UtilityController.addSuccessMessage("Staff Credit Updated");
//                getReturnBill().setFromStaff(getBill().getToStaff());
//                getBillFacade().edit(getReturnBill());
//            }
//        }
        /// setOnlyReturnValue();
        printPreview = true;
        UtilityController.addSuccessMessage("Successfully Returned");

    }

    private void calTotal() {
        double grossTotal = 0.0;

        for (BillItem p : getBillItems()) {
            grossTotal += p.getNetRate() * p.getQty();

        }

        getReturnBill().setTotal(grossTotal);
        getReturnBill().setNetTotal(grossTotal);

        //  return grossTotal;
    }

    public void generateBillComponent(BillType billType) {
        for (PharmaceuticalBillItem i : getPharmaceuticalBillItemFacade().getPharmaceuticalBillItems(getBill())) {
            BillItem bi = new BillItem();
            bi.setBill(getReturnBill());
            bi.setReferenceBill(getBill());
            bi.setReferanceBillItem(i.getBillItem());
            bi.copy(i.getBillItem());
            bi.setQty(0.0);

            PharmaceuticalBillItem tmp = new PharmaceuticalBillItem();
            tmp.setBillItem(bi);
            tmp.copy(i);

            double rFund = getPharmacyRecieveBean().getTotalQty(i.getBillItem(), billType);

            double tmpQty = (Math.abs(i.getQtyInUnit())) - Math.abs(rFund);

            if (tmpQty <= 0) {
                continue;
            }

            tmp.setQtyInUnit((double) tmpQty);

            bi.setPharmaceuticalBillItem(tmp);

            getBillItems().add(bi);
        }

    }

//    private double calRemainingQty(PharmaceuticalBillItem i) {
//        if (i.getRemainingQty() == 0.0) {
////            if (i.getBillItem().getItem() instanceof Ampp) {
////                return (i.getQty()) * i.getBillItem().getItem().getDblValue();
////            } else {
////                return i.getQty();
////            }
//            return i.getQty();
//        } else {
//            return i.getRemainingQty();
//        }
//
//    }
    public PharmaceuticalBillItemFacade getPharmaceuticalBillItemFacade() {
        return pharmaceuticalBillItemFacade;
    }

    public void setPharmaceuticalBillItemFacade(PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade) {
        this.pharmaceuticalBillItemFacade = pharmaceuticalBillItemFacade;
    }

    public PharmaceuticalItemController getPharmaceuticalItemController() {
        return pharmaceuticalItemController;
    }

    public void setPharmaceuticalItemController(PharmaceuticalItemController pharmaceuticalItemController) {
        this.pharmaceuticalItemController = pharmaceuticalItemController;
    }

    public PharmacyController getPharmacyController() {
        return pharmacyController;
    }

    public void setPharmacyController(PharmacyController pharmacyController) {
        this.pharmacyController = pharmacyController;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public PharmacyBean getPharmacyBean() {
        return pharmacyBean;
    }

    public void setPharmacyBean(PharmacyBean pharmacyBean) {
        this.pharmacyBean = pharmacyBean;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public PharmacyCalculation getPharmacyRecieveBean() {
        return pharmacyRecieveBean;
    }

    public void setPharmacyRecieveBean(PharmacyCalculation pharmacyRecieveBean) {
        this.pharmacyRecieveBean = pharmacyRecieveBean;
    }

    public List<BillItem> getBillItems() {
        if (billItems == null) {

            billItems = new ArrayList<>();
        }
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

}
