/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.cashTransaction;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CashTransactionBean;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.WebUser;
import com.kajabuyahmis.entity.cashTransaction.CashTransaction;
import com.kajabuyahmis.entity.cashTransaction.Drawer;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.CashTransactionFacade;
import com.kajabuyahmis.facade.WebUserFacade;
import java.io.Serializable;
import java.util.Date;
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
public class CashOutController implements Serializable {

    private boolean printPreview;
    Bill bill;
    @EJB
    private CashTransactionBean cashTransactionBean;
    @EJB
    CashTransactionFacade cashTransactionFacade;
    @EJB
    private BillFacade billFacade;
    @EJB
    private BillNumberGenerator billNumberBean;
    @Inject
    private SessionController sessionController;
    private Drawer drawer;

    public CashTransactionFacade getCashTransactionFacade() {
        return cashTransactionFacade;
    }

    public void setCashTransactionFacade(CashTransactionFacade cashTransactionFacade) {
        this.cashTransactionFacade = cashTransactionFacade;
    }

    private boolean errorCheck() {
        if (getBill().getCashTransaction() == null) {
            return true;
        }

        return false;
    }

    public Bill getBill() {
        if (bill == null) {
            bill = new BilledBill();
            bill.setCashTransaction(new CashTransaction());
        }

        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    private void saveBill(CashTransaction cashTransaction) {
        double netTotal = 0;
        if (cashTransaction.getCashValue() != null) {
            netTotal += Math.abs(cashTransaction.getCashValue());
        }

        if (cashTransaction.getCreditCardValue() != null) {
            netTotal += Math.abs(cashTransaction.getCreditCardValue());
        }

        if (cashTransaction.getChequeValue() != null) {
            netTotal += Math.abs(cashTransaction.getChequeValue());
        }

        if (cashTransaction.getSlipValue() != null) {
            netTotal += Math.abs(cashTransaction.getSlipValue());
        }

        getBill().setNetTotal(0 - netTotal);

        getBill().setBillType(BillType.CashOut);
        getBill().setCreatedAt(new Date());
        getBill().setCreater(getSessionController().getLoggedUser());
        getBill().setDepartment(getSessionController().getDepartment());
        getBill().setInstitution(getSessionController().getInstitution());
        getBill().setFromWebUser(getSessionController().getLoggedUser());

        getBill().setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), getBill().getBillType(), BillClassType.BilledBill, BillNumberSuffix.CSOUT));
        getBill().setDeptId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getDepartment(), getBill().getBillType(), BillClassType.BilledBill, BillNumberSuffix.CSOUT));

        getBillFacade().create(getBill());

    }

    @EJB
    private WebUserFacade webUserFacade;

    public void settle() {
        if (errorCheck()) {
            return;
        }

        if (getBill().getCashTransaction().getCashValue() == null) {
            calTotal();
        }

        CashTransaction ct = getBill().getCashTransaction();
        getBill().setCashTransaction(null);
        saveBill(ct);

        getCashTransactionBean().saveCashOutTransaction(ct, getBill(), getSessionController().getLoggedUser());

        getBill().setCashTransaction(ct);
        getBillFacade().edit(getBill());

//        getCashTransactionBean().deductFromBallance(getSessionController().getLoggedUser().getDrawer(), ct);
        WebUser wb = getWebUserFacade().find(getSessionController().getLoggedUser().getId());
        getSessionController().setLoggedUser(wb);

//        if (getBill().getToWebUser() != null) {
//            getCashTransactionBean().addToBallance(getBill().getToWebUser().getDrawer(), dbl, ct);
//        }
        UtilityController.addSuccessMessage("Succesfully Cash Out");
        printPreview = true;

    }

    public void makeNull() {
        printPreview = false;
        bill = null;
        drawer = null;
    }

    public void calTotal() {
        double dbl = getCashTransactionBean().calTotal(getBill().getCashTransaction());
        getBill().getCashTransaction().setCashValue(dbl);
    }

    /**
     * Creates a new instance of BulkCashierController
     */
    public CashOutController() {
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public boolean getPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public CashTransactionBean getCashTransactionBean() {
        return cashTransactionBean;
    }

    public void setCashTransactionBean(CashTransactionBean cashTransactionBean) {
        this.cashTransactionBean = cashTransactionBean;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public Drawer getDrawer() {
        if (drawer == null) {
            drawer = getCashTransactionBean().getDrawer(getSessionController().getLoggedUser());
        }
        return drawer;
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    public WebUserFacade getWebUserFacade() {
        return webUserFacade;
    }

    public void setWebUserFacade(WebUserFacade webUserFacade) {
        this.webUserFacade = webUserFacade;
    }

}
