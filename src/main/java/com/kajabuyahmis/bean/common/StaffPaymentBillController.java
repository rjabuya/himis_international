package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.dataStructure.SearchKeyword;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CashTransactionBean;
import com.kajabuyahmis.ejb.CommonFunctions;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillComponent;
import com.kajabuyahmis.entity.BillFee;
import com.kajabuyahmis.entity.BillFeePayment;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.Payment;
import com.kajabuyahmis.entity.RefundBill;
import com.kajabuyahmis.entity.Speciality;
import com.kajabuyahmis.entity.Staff;
import com.kajabuyahmis.entity.WebUser;
import com.kajabuyahmis.facade.BillComponentFacade;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.BillFeeFacade;
import com.kajabuyahmis.facade.BillFeePaymentFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.CancelledBillFacade;
import com.kajabuyahmis.facade.PaymentFacade;
import com.kajabuyahmis.facade.RefundBillFacade;
import com.kajabuyahmis.facade.StaffFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class StaffPaymentBillController implements Serializable {

    @EJB
    private RefundBillFacade refundBillFacade;
    private List<BillComponent> billComponents;
    @EJB
    private CancelledBillFacade cancelledBillFacade;
    @EJB
    private BillComponentFacade billComponentFacade;
    @EJB
    BillFeeFacade billFeeFacade;
    private List<BillItem> billItems;
    private static final long serialVersionUID = 1L;
    private Date fromDate;
    private Date toDate;
    @Inject
    SessionController sessionController;
    @EJB
    private CommonFunctions commonFunctions;
    @EJB
    private BillFacade billFacade;
    @EJB
    private BillItemFacade billItemFacade;
    @EJB
    PaymentFacade paymentFacade;
    @EJB
    BillFeePaymentFacade BillFeePaymentFacade;
    List<Bill> selectedItems;
    private Bill current;
    private List<Bill> items = null;
    String selectText = "";
    Staff currentStaff;
    private List<BillFee> dueBillFeeReport;
    List<BillFee> dueBillFees;
    List<BillFee> payingBillFees;
    double totalDue;
    double totalPaying;
    @EJB
    BillNumberGenerator billNumberBean;
    private Boolean printPreview = false;
    PaymentMethod paymentMethod;
    Speciality speciality;
    @EJB
    StaffFacade staffFacade;
    private SearchKeyword searchKeyword;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<BillComponent> getBillComponents() {
        if (getCurrent() != null) {
            String sql = "SELECT b FROM BillComponent b WHERE b.retired=false and b.bill.id=" + getCurrent().getId();
            billComponents = getBillComponentFacade().findBySQL(sql);
            if (billComponents == null) {
                billComponents = new ArrayList<>();
            }
        }
        return billComponents;
    }
    private List<BillFee> billFees;
    private List<BillFee> tblBillFees;

    public List<BillFee> getBillFees() {
        if (getCurrent() != null) {
            if (billFees == null) {
                String sql = "SELECT b FROM BillFee b WHERE b.retired=false and b.bill.id=" + getCurrent().getId();
                billFees = getBillFeeFacade().findBySQL(sql);
                if (billFees == null) {
                    billFees = new ArrayList<>();
                }
            }
        }

        return billFees;
    }

    public void newPayment() {
        recreateModel();

    }

    private void recreateModel() {

        billFees = null;
        billItems = null;
        printPreview = false;
        billItems = null;
        selectedItems = null;
        items = null;
        dueBillFeeReport = null;
        dueBillFees = null;
        payingBillFees = null;
        billFees = null;
        /////////////////////    
        fromDate = null;
        toDate = null;
        current = null;
        selectText = "";
        currentStaff = null;
        totalDue = 0.0;
        totalPaying = 0.0;
        printPreview = false;
        paymentMethod = PaymentMethod.Cash;
        speciality = null;

    }

    public StaffFacade getStaffFacade() {
        return staffFacade;
    }

    public void setStaffFacade(StaffFacade staffFacade) {
        this.staffFacade = staffFacade;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
        currentStaff = null;
        dueBillFees = new ArrayList<>();
        payingBillFees = new ArrayList<>();
        totalPaying = 0.0;
        totalDue = 0.0;

    }

    public List<Staff> completeStaff(String query) {
        List<Staff> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<>();
        }

        HashMap hm = new HashMap();
        if (speciality != null) {
            sql = "select p from Staff p "
                    + " where p.retired=false "
                    + " and (upper(p.person.name) like :q "
                    + " or  upper(p.code) like :q ) "
                    + " and p.speciality=:sp "
                    + " order by p.person.name";
            hm.put("sp", getSpeciality());
        } else {
            sql = "select p from Staff p "
                    + " where p.retired=false "
                    + " and (upper(p.person.name) like :q "
                    + " or  upper(p.code) like :q )"
                    + " order by p.person.name";
        }
        //////// // System.out.println(sql);
        hm.put("q", "%" + query.toUpperCase() + "%");
        suggestions = getStaffFacade().findBySQL(sql, hm, 20);

        return suggestions;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }

    public double getTotalPaying() {
        return totalPaying;
    }

    public void setTotalPaying(double totalPaying) {
        this.totalPaying = totalPaying;
    }

    public void calculateDueFees() {
        if (currentStaff == null || currentStaff.getId() == null) {
            dueBillFees = new ArrayList<>();
        } else {
            String sql;
            HashMap h = new HashMap();
            sql = "select b from BillFee b where "
                    + " b.retired=false"
                    + " and (b.bill.billType=:btp or b.bill.billType=:btpc) "
                    + " and b.bill.cancelled=false "
//                    + " and b.bill.refunded=false "
                    + " and (b.feeValue - b.paidValue) > 0 "
                    + " and b.staff.id = " + currentStaff.getId();
            h.put("btp", BillType.OpdBill);
            h.put("btpc", BillType.CollectingCentreBill);

            dueBillFees = getBillFeeFacade().findBySQL(sql, h, TemporalType.TIMESTAMP);

            List<BillFee> removeingBillFees = new ArrayList<>();
            for (BillFee bf : dueBillFees) {
                h = new HashMap();
                sql = "SELECT bi FROM BillItem bi where "
                        + " bi.retired=false"
                        + " and bi.bill.cancelled=false "
                        + " and type(bi.bill)=:class "
                        + " and bi.referanceBillItem.id=" + bf.getBillItem().getId();
                h.put("class",RefundBill.class);
                BillItem rbi = getBillItemFacade().findFirstBySQL(sql,h);

                if (rbi != null) {
                    removeingBillFees.add(bf);
                }

            }
            dueBillFees.removeAll(removeingBillFees);

        }
    }

    public void calculateTotalDue() {
        totalDue = 0;
        for (BillFee f : dueBillFees) {
            totalDue = totalDue + f.getFeeValue() - f.getPaidValue();
        }
    }

    public void performCalculations() {
        calculateTotalDue();
        calculateTotalPay();
    }

    public void calculateTotalPay() {
        totalPaying = 0;

        for (BillFee f : payingBillFees) {
            //////// // System.out.println("totalPaying before " + totalPaying);
            //////// // System.out.println("fee val is " + f.getFeeValue());
            //////// // System.out.println("paid val is " + f.getPaidValue());
            totalPaying = totalPaying + (f.getFeeValue() - f.getPaidValue());
            //////// // System.out.println("totalPaying after " + totalPaying);
        }
        //////// // System.out.println("total pay is " + totalPaying);
    }

    public BillFeeFacade getBillFeeFacade() {
        return billFeeFacade;
    }

    public void setBillFeeFacade(BillFeeFacade billFeeFacade) {
        this.billFeeFacade = billFeeFacade;
    }

    public List<BillFee> getDueBillFees() {
        return dueBillFees;
    }

    public void setDueBillFees(List<BillFee> dueBillFees) {
        this.dueBillFees = dueBillFees;
    }

    public List<BillFee> getPayingBillFees() {
        return payingBillFees;
    }

    public void setPayingBillFees(List<BillFee> payingBillFees) {
        //////// // System.out.println("setting paying bill fees " + payingBillFees.size());
        this.payingBillFees = payingBillFees;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public Staff getCurrentStaff() {
        return currentStaff;

    }

    public void setCurrentStaff(Staff currentStaff) {

        this.currentStaff = currentStaff;

        dueBillFees = new ArrayList<BillFee>();
        payingBillFees = new ArrayList<BillFee>();
        totalPaying = 0.0;
        totalDue = 0.0;
        printPreview = false;

        calculateDueFees();
        performCalculations();

    }

    public List<Bill> getSelectedItems() {
        selectedItems = getFacade().findBySQL("select c from Bill c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        return selectedItems;
    }

    public void prepareAdd() {
        current = new BilledBill();
    }

    public void setSelectedItems(List<Bill> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private Bill createPaymentBill() {
        BilledBill tmp = new BilledBill();
        tmp.setBillDate(Calendar.getInstance().getTime());
        tmp.setBillTime(Calendar.getInstance().getTime());
        tmp.setBillType(BillType.PaymentBill);
        tmp.setCreatedAt(Calendar.getInstance().getTime());
        tmp.setCreater(getSessionController().getLoggedUser());
        tmp.setDepartment(getSessionController().getLoggedUser().getDepartment());

        tmp.setDeptId(getBillNumberBean().departmentBillNumberGenerator(getSessionController().getDepartment(), BillType.PaymentBill, BillClassType.BilledBill, BillNumberSuffix.PROPAY));
        tmp.setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), BillType.PaymentBill, BillClassType.BilledBill, BillNumberSuffix.PROPAY));

        tmp.setDiscount(0.0);
        tmp.setDiscountPercent(0.0);

        tmp.setInstitution(getSessionController().getLoggedUser().getInstitution());
        tmp.setNetTotal(0 - totalPaying);
        tmp.setPaymentMethod(paymentMethod);
        tmp.setStaff(currentStaff);
        tmp.setToStaff(currentStaff);
        tmp.setTotal(0 - totalPaying);

        return tmp;
    }

    private boolean errorCheck() {
        if (currentStaff == null) {
            UtilityController.addErrorMessage("Please select a Staff Memeber");
            return true;
        }
        performCalculations();
        if (totalPaying == 0) {
            UtilityController.addErrorMessage("Please select payments to update");
            return true;
        }
        if (paymentMethod == null) {
            UtilityController.addErrorMessage("Please select a payment method");
            return true;
        }

        return false;
    }

    public Boolean isPrintPreview() {
        return printPreview;
    }

    @EJB
    CashTransactionBean cashTransactionBean;

    public CashTransactionBean getCashTransactionBean() {
        return cashTransactionBean;
    }

    public void setCashTransactionBean(CashTransactionBean cashTransactionBean) {
        this.cashTransactionBean = cashTransactionBean;
    }

    public void settleBill() {
        if (errorCheck()) {
            return;
        }
        calculateTotalPay();
        Bill b = createPaymentBill();
        current = b;
        getBillFacade().create(b);
        Payment p = createPayment(b, paymentMethod);
        saveBillCompo(b, p);
        printPreview = true;

        WebUser wb = getCashTransactionBean().saveBillCashOutTransaction(b, getSessionController().getLoggedUser());
        getSessionController().setLoggedUser(wb);
        UtilityController.addSuccessMessage("Successfully Paid");
        //////// // System.out.println("Paid");
    }

    private void saveBillCompo(Bill b, Payment p) {
        for (BillFee bf : getPayingBillFees()) {
//            saveBillItemForPaymentBill(b, bf); //for create bill fees and billfee payments
            saveBillItemForPaymentBill(b, bf, p);
//            saveBillFeeForPaymentBill(b,bf); No need to add fees for this bill
            bf.setPaidValue(bf.getFeeValue());
            bf.setSettleValue(bf.getFeeValue());
            getBillFeeFacade().edit(bf);
            //////// // System.out.println("marking as paid");
        }
    }

    private void saveBillItemForPaymentBill(Bill b, BillFee bf) {
        BillItem i = new BillItem();
        i.setReferanceBillItem(bf.getBillItem());
        i.setReferenceBill(bf.getBill());
        i.setPaidForBillFee(bf);
        i.setBill(b);
        i.setCreatedAt(Calendar.getInstance().getTime());
        i.setCreater(getSessionController().getLoggedUser());
        i.setDiscount(0.0);
        i.setGrossValue(bf.getFeeValue());
//        if (bf.getBillItem() != null && bf.getBillItem().getItem() != null) {
//            i.setItem(bf.getBillItem().getItem());
//        }
        i.setNetValue(bf.getFeeValue());
        i.setQty(1.0);
        i.setRate(bf.getFeeValue());
        getBillItemFacade().create(i);
        b.getBillItems().add(i);
    }

    private void saveBillItemForPaymentBill(Bill b, BillFee bf, Payment p) {
        BillItem i = new BillItem();
        i.setReferanceBillItem(bf.getBillItem());
        i.setReferenceBill(bf.getBill());
        i.setPaidForBillFee(bf);
        i.setBill(b);
        i.setCreatedAt(Calendar.getInstance().getTime());
        i.setCreater(getSessionController().getLoggedUser());
        i.setDiscount(0.0);
        i.setGrossValue(bf.getFeeValue());
//        if (bf.getBillItem() != null && bf.getBillItem().getItem() != null) {
//            i.setItem(bf.getBillItem().getItem());
//        }
        i.setNetValue(bf.getFeeValue());
        i.setQty(1.0);
        i.setRate(bf.getFeeValue());
        getBillItemFacade().create(i);
        saveBillFee(i, p);
        b.getBillItems().add(i);
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public BillFacade getEjbFacade() {
        return billFacade;
    }

    public void setEjbFacade(BillFacade ejbFacade) {
        this.billFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public StaffPaymentBillController() {
    }

    public Bill getCurrent() {
        if (current == null) {
            current = new BilledBill();
        }
        return current;
    }

    public void setCurrent(Bill current) {
        currentStaff = null;
        dueBillFees = new ArrayList<BillFee>();
        payingBillFees = new ArrayList<BillFee>();
        totalPaying = 0.0;
        totalDue = 0.0;
        recreateModel();
        this.current = current;
    }

    public void delete() {
        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
//        getItems();
        current = null;
        getCurrent();
    }

    //for bill fee payments
    public Payment createPayment(Bill bill, PaymentMethod pm) {
        Payment p = new Payment();
        p.setBill(bill);
        setPaymentMethodData(p, pm);
        return p;
    }

    public void setPaymentMethodData(Payment p, PaymentMethod pm) {

        p.setInstitution(getSessionController().getInstitution());
        p.setDepartment(getSessionController().getDepartment());
        p.setCreatedAt(new Date());
        p.setCreater(getSessionController().getLoggedUser());
        p.setPaymentMethod(pm);

        p.setPaidValue(p.getBill().getNetTotal());

        if (p.getId() == null) {
            getPaymentFacade().create(p);
        }

    }

    public void createBillFeePaymentAndPayment(BillFee bf, Payment p) {
        BillFeePayment bfp = new BillFeePayment();
        bfp.setBillFee(bf);
        bfp.setAmount(bf.getSettleValue());
        bfp.setInstitution(getSessionController().getInstitution());
        bfp.setDepartment(getSessionController().getDepartment());
        bfp.setCreater(getSessionController().getLoggedUser());
        bfp.setCreatedAt(new Date());
        bfp.setPayment(p);
        getBillFeePaymentFacade().create(bfp);
    }

    public void saveBillFee(BillItem bi, Payment p) {
        BillFee bf = new BillFee();
        bf.setCreatedAt(Calendar.getInstance().getTime());
        bf.setCreater(getSessionController().getLoggedUser());
        bf.setBillItem(bi);
        bf.setPatienEncounter(bi.getBill().getPatientEncounter());
        bf.setPatient(bi.getBill().getPatient());
        bf.setFeeValue(0 - bi.getNetValue());
        bf.setFeeGrossValue(0 - bi.getGrossValue());
        bf.setSettleValue(0 - bi.getNetValue());
        bf.setCreatedAt(new Date());
        bf.setDepartment(getSessionController().getDepartment());
        bf.setInstitution(getSessionController().getInstitution());
        bf.setBill(bi.getBill());

        if (bf.getId() == null) {
            getBillFeeFacade().create(bf);
        }
        createBillFeePaymentAndPayment(bf, p);
    }

    //for bill fee payments
    private BillFacade getFacade() {
        return billFacade;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public Boolean getPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(Boolean printPreview) {
        this.printPreview = printPreview;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = getCommonFunctions().getEndOfDay(new Date());
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
        //  resetLists();
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = getCommonFunctions().getStartOfDay(new Date());
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        //  resetLists();
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public List<BillFee> getDueBillFeeReport() {
        String sql;
        Map temMap = new HashMap();
        sql = "select b from BillFee b where b.retired=false and b.bill.cancelled=false and (b.feeValue - b.paidValue) > 0 and b.bill.institution.id=" + getSessionController().getInstitution().getId() + " and b.bill.billDate between :fromDate and :toDate order by b.staff.id  ";
        //////// // System.out.println("sql is " + sql);
        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());

        dueBillFeeReport = getBillFeeFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);
        //////// // System.out.println(dueBillFeeReport.size());

        if (dueBillFeeReport == null) {
            dueBillFeeReport = new ArrayList<BillFee>();
        }

        return dueBillFeeReport;
    }

    private LazyDataModel<BillFee> dueBillFee;

    public List<BillFee> getDueBillFeeReportAll() {

        String sql;
        Map temMap = new HashMap();
        if (!getSelectText().equals("")) {
            sql = "select b from BillFee b where b.retired=false and (b.bill.billType!=:btp and b.bill.billType!=:btp2) and b.bill.cancelled=false and (b.feeValue - b.paidValue) > 0 and  b.bill.billDate between :fromDate and :toDate and upper(b.staff.person.name) like '%" + selectText.toUpperCase() + "%' order by b.staff.id  ";
        } else {
            sql = "select b from BillFee b where b.retired=false and (b.bill.billType!=:btp and b.bill.billType!=:btp2) and b.bill.cancelled=false and (b.feeValue - b.paidValue) > 0 and  b.bill.billDate between :fromDate and :toDate order by b.staff.id  ";
        }
        //////// // System.out.println("sql is " + sql);
        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("btp", BillType.ChannelPaid);
        temMap.put("btp2", BillType.ChannelCredit);

        dueBillFeeReport = getBillFeeFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);
        //////// // System.out.println(dueBillFeeReport.size());

        if (dueBillFeeReport == null) {
            dueBillFeeReport = new ArrayList<BillFee>();
        }

        return dueBillFeeReport;
    }

    public void setDueBillFeeReport(List<BillFee> dueBillFeeReport) {
        this.dueBillFeeReport = dueBillFeeReport;
    }

    public List<BillItem> getBillItems() {
        if (getCurrent() != null) {
            String sql = "SELECT b FROM BillItem b WHERE b.retired=false and b.bill.id=" + getCurrent().getId();
            billItems = getBillItemFacade().findBySQL(sql);
            if (billItems == null) {
                billItems = new ArrayList<BillItem>();
            }
        }

        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    public CancelledBillFacade getCancelledBillFacade() {
        return cancelledBillFacade;
    }

    public void setCancelledBillFacade(CancelledBillFacade cancelledBillFacade) {
        this.cancelledBillFacade = cancelledBillFacade;
    }

    public BillComponentFacade getBillComponentFacade() {
        return billComponentFacade;
    }

    public void setBillComponentFacade(BillComponentFacade billComponentFacade) {
        this.billComponentFacade = billComponentFacade;
    }

    public RefundBillFacade getRefundBillFacade() {
        return refundBillFacade;
    }

    public void setRefundBillFacade(RefundBillFacade refundBillFacade) {
        this.refundBillFacade = refundBillFacade;
    }

    public void setBillComponents(List<BillComponent> billComponents) {
        this.billComponents = billComponents;
    }

    public void setBillFees(List<BillFee> billFees) {
        this.billFees = billFees;
    }

    public void setItems(List<Bill> items) {
        this.items = items;
    }

    public LazyDataModel<BillFee> getDueBillFee() {
        return dueBillFee;
    }

    public void setDueBillFee(LazyDataModel<BillFee> dueBillFee) {
        this.dueBillFee = dueBillFee;
    }

    public SearchKeyword getSearchKeyword() {
        if (searchKeyword == null) {
            searchKeyword = new SearchKeyword();
        }
        return searchKeyword;
    }

    public void setSearchKeyword(SearchKeyword searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public List<BillFee> getTblBillFees() {
        return tblBillFees;
    }

    public void setTblBillFees(List<BillFee> tblBillFees) {
        this.tblBillFees = tblBillFees;
    }

    public PaymentFacade getPaymentFacade() {
        return paymentFacade;
    }

    public void setPaymentFacade(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    public BillFeePaymentFacade getBillFeePaymentFacade() {
        return BillFeePaymentFacade;
    }

    public void setBillFeePaymentFacade(BillFeePaymentFacade BillFeePaymentFacade) {
        this.BillFeePaymentFacade = BillFeePaymentFacade;
    }

}
