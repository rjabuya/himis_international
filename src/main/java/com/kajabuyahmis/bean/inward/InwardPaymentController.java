/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.inward;
import com.kajabuyahmis.bean.common.BillBeanController;
import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.bean.membership.PaymentSchemeController;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.dataStructure.PaymentMethodData;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CashTransactionBean;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.PatientEncounter;
import com.kajabuyahmis.entity.WebUser;
import com.kajabuyahmis.facade.BillFeeFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.BilledBillFacade;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class InwardPaymentController implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private BillNumberGenerator billNumberBean;
    private BilledBill current;
    @EJB
    private BilledBillFacade billedBillFacade;
    @EJB
    private BillItemFacade billItemFacade;
    @EJB
    private BillFeeFacade billFeeFacade;
    @Inject
    private SessionController sessionController;
    private boolean printPreview;
    private double due;
    String comment;

    public PaymentMethod[] getPaymentMethods() {
        return PaymentMethod.values();
    }

    @Inject
    private PaymentSchemeController paymentSchemeController;
    private PaymentMethodData paymentMethodData;

    public void bhtListener() {
        due = getFinalBillDue();

    }

    private double getFinalBillDue() {
        String sql = "Select b From BilledBill b where"
                + " b.retired=false "
                + " and b.cancelled=false "
                + " and b.billType=:btp "
                + " and b.patientEncounter=:pe "
                + " order by b.id desc";
        HashMap hm = new HashMap();
        hm.put("btp", BillType.InwardFinalBill);
        hm.put("pe", getCurrent().getPatientEncounter());

        Bill b = getBilledBillFacade().findFirstBySQL(sql, hm);

        if (b == null) {
            return 0;
        }

       return b.getNetTotal()-(b.getPaidAmount()+getCurrent().getPatientEncounter().getCreditPaidAmount());
        
//        double billValue = Math.abs(b.getNetTotal());
//        double paidByPatient = Math.abs(b.getPaidAmount());
//        double creditUsedAmount = Math.abs(getCurrent().getPatientEncounter().getCreditUsedAmount());
//        double creditPaidAmount = Math.abs(getCurrent().getPatientEncounter().getCreditPaidAmount());
//        double netCredit = creditUsedAmount - creditPaidAmount;
//
//        return billValue - (paidByPatient + netCredit);

    }

    @Inject
    private InwardBeanController inwardBean;

    private boolean errorCheck() {
        if (getCurrent().getPatientEncounter() == null) {
            UtilityController.addErrorMessage("Select BHT");
            return true;
        }

        if (getCurrent().getPaymentMethod() == null) {
            UtilityController.addErrorMessage("Select Payment Method");
            return true;
        }

        if (getPaymentSchemeController().errorCheckPaymentMethod(getCurrent().getPaymentMethod(), paymentMethodData)) {
            return true;
        }

//        if (due != 0) {
//
//            if ((due < getCurrent().getTotal())) {
//                double different = Math.abs((due - getCurrent().getTotal()));
//
//                if (different > 0.1) {
//                    UtilityController.addErrorMessage("U cant recieve payment thenn due");
//                    return true;
//                }
//            }
//        }
        return false;

    }

    @EJB
    CashTransactionBean cashTransactionBean;

    public CashTransactionBean getCashTransactionBean() {
        return cashTransactionBean;
    }

    public void setCashTransactionBean(CashTransactionBean cashTransactionBean) {
        this.cashTransactionBean = cashTransactionBean;
    }

    public void pay() {
        if (errorCheck()) {
            return;
        }

        saveBill();
        saveBillItem();

        getBillBean().updateInwardDipositList(getCurrent().getPatientEncounter(), getCurrent());

        if (getCurrent().getPatientEncounter().isPaymentFinalized()) {
            getInwardBean().updateFinalFill(getCurrent().getPatientEncounter());

            if (getCurrent().getPatientEncounter().getPaymentMethod() == PaymentMethod.Credit) {
                getInwardBean().updateCreditDetail(getCurrent().getPatientEncounter(), getCurrent().getPatientEncounter().getFinalBill().getNetTotal());
            }
        }

        WebUser wb = getCashTransactionBean().saveBillCashInTransaction(getCurrent(), getSessionController().getLoggedUser());
        getSessionController().setLoggedUser(wb);
        UtilityController.addSuccessMessage("Payment Bill Saved");
        printPreview = true;
    }

    public Bill pay(PaymentMethod paymentMethod, PatientEncounter patientEncounter, double value) {
        makeNull();
        getCurrent().setPaymentMethod(paymentMethod);
        getCurrent().setPatientEncounter(patientEncounter);
        getCurrent().setTotal(value);

        if (errorCheck()) {
            return null;
        }

        saveBill();
        saveBillItem();
        UtilityController.addSuccessMessage("Payment Bill Saved");

        Bill curr = getCurrent();

        makeNull();

        return curr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void makeNull() {
        current = null;
        printPreview = false;
        comment = null;
    }

    @Inject
    private BillBeanController billBean;

    private void saveBill() {
        getBillBean().setPaymentMethodData(getCurrent(), getCurrent().getPaymentMethod(), getPaymentMethodData());

        getCurrent().setInstitution(getSessionController().getInstitution());
        getCurrent().setDepartment(getSessionController().getDepartment());
        getCurrent().setBillType(BillType.InwardPaymentBill);
        getCurrent().setDeptId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getDepartment(), getCurrent().getBillType(), BillClassType.BilledBill, BillNumberSuffix.INWPAY));
        getCurrent().setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), getCurrent().getBillType(), BillClassType.BilledBill, BillNumberSuffix.INWPAY));

//        getCurrent().setForwardReferenceBill(getCurrent().getPatientEncounter().getFinalBill());
        getCurrent().setBillDate(new Date());
        getCurrent().setBillTime(new Date());

        double dbl = Math.abs(getCurrent().getTotal());
        getCurrent().setTotal(dbl);
        getCurrent().setNetTotal(dbl);
        getCurrent().setCreatedAt(new Date());
        getCurrent().setCreater(getSessionController().getLoggedUser());
        getCurrent().setComments(comment);

        if (getCurrent().getId() == null) {
            getBilledBillFacade().create(getCurrent());
        }

    }

    private void saveBillItem() {
        BillItem temBi = new BillItem();
        temBi.setBill(getCurrent());
        temBi.setGrossValue(getCurrent().getTotal());
        temBi.setNetValue(getCurrent().getTotal());
        temBi.setCreatedAt(new Date());
        temBi.setCreater(getSessionController().getLoggedUser());

        if (temBi.getId() == null) {
            getBillItemFacade().create(temBi);
        }

    }

    public BilledBillFacade getBilledBillFacade() {
        return billedBillFacade;
    }

    public void setBilledBillFacade(BilledBillFacade billedBillFacade) {
        this.billedBillFacade = billedBillFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public BillFeeFacade getBillFeeFacade() {
        return billFeeFacade;
    }

    public void setBillFeeFacade(BillFeeFacade billFeeFacade) {
        this.billFeeFacade = billFeeFacade;
    }

    public BilledBill getCurrent() {
        if (current == null) {
            current = new BilledBill();
            current.setBillType(BillType.InwardPaymentBill);
        }

        return current;
    }

    public void setCurrent(BilledBill current) {
        this.current = current;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public PaymentSchemeController getPaymentSchemeController() {
        return paymentSchemeController;
    }

    public void setPaymentSchemeController(PaymentSchemeController paymentSchemeController) {
        this.paymentSchemeController = paymentSchemeController;
    }

    public PaymentMethodData getPaymentMethodData() {
        if (paymentMethodData == null) {
            paymentMethodData = new PaymentMethodData();
        }
        return paymentMethodData;
    }

    public void setPaymentMethodData(PaymentMethodData paymentMethodData) {
        this.paymentMethodData = paymentMethodData;
    }

    public BillBeanController getBillBean() {
        return billBean;
    }

    public void setBillBean(BillBeanController billBean) {
        this.billBean = billBean;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public InwardBeanController getInwardBean() {
        return inwardBean;
    }

    public void setInwardBean(InwardBeanController inwardBean) {
        this.inwardBean = inwardBean;
    }

}
