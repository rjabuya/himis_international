package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.bean.collectingCentre.CollectingCentreBillController;
import com.kajabuyahmis.bean.membership.MembershipSchemeController;
import com.kajabuyahmis.bean.membership.PaymentSchemeController;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.DepartmentType;
import com.kajabuyahmis.data.FeeType;
import com.kajabuyahmis.data.InstitutionType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.Sex;
import com.kajabuyahmis.data.Title;
import com.kajabuyahmis.data.dataStructure.BillListWithTotals;
import com.kajabuyahmis.data.dataStructure.PaymentMethodData;
import com.kajabuyahmis.data.dataStructure.YearMonthDay;
import com.kajabuyahmis.ejb.BillEjb;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CashTransactionBean;
import com.kajabuyahmis.ejb.CommonFunctions;
import com.kajabuyahmis.ejb.StaffBean;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillComponent;
import com.kajabuyahmis.entity.BillEntry;
import com.kajabuyahmis.entity.BillFee;
import com.kajabuyahmis.entity.BillFeePayment;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.BillSession;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.CancelledBill;
import com.kajabuyahmis.entity.Category;
import com.kajabuyahmis.entity.Department;
import com.kajabuyahmis.entity.Doctor;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.Patient;
import com.kajabuyahmis.entity.Payment;
import com.kajabuyahmis.entity.PaymentScheme;
import com.kajabuyahmis.entity.Person;
import com.kajabuyahmis.entity.PriceMatrix;
import com.kajabuyahmis.entity.Staff;
import com.kajabuyahmis.entity.WebUser;
import com.kajabuyahmis.entity.membership.MembershipScheme;
import com.kajabuyahmis.facade.BatchBillFacade;
import com.kajabuyahmis.facade.BillComponentFacade;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.BillFeeFacade;
import com.kajabuyahmis.facade.BillFeePaymentFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.BillSessionFacade;
import com.kajabuyahmis.facade.InstitutionFacade;
import com.kajabuyahmis.facade.PatientEncounterFacade;
import com.kajabuyahmis.facade.PatientFacade;
import com.kajabuyahmis.facade.PaymentFacade;
import com.kajabuyahmis.facade.PersonFacade;
import com.kajabuyahmis.facade.util.JsfUtil;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class BillController implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * EJBs
     */
    @EJB
    BillNumberGenerator billNumberGenerator;
    @EJB
    private BillFacade billFacade;
    @EJB
    private BillItemFacade billItemFacade;
    @EJB
    private InstitutionFacade institutionFacade;
    @EJB
    private PatientEncounterFacade patientEncounterFacade;
    @EJB
    BillEjb billEjb;
    @EJB
    PaymentFacade PaymentFacade;
    @EJB
    BillFeePaymentFacade billFeePaymentFacade;
    /**
     * Controllers
     */
    @Inject
    SessionController sessionController;
    @Inject
    CommonController commonController;
    @Inject
    PaymentSchemeController paymentSchemeController;
    @Inject
    ApplicationController applicationController;
    @Inject
    private EnumController enumController;
    @Inject
    CollectingCentreBillController collectingCentreBillController;
    /**
     * Class Vairables
     */
    private boolean printPreview;
    private String patientTabId = "tabNewPt";
    //Interface Data
    private PaymentScheme paymentScheme;
    private PaymentMethod paymentMethod;
    private Patient newPatient;
    private Patient searchedPatient;
    private Doctor referredBy;
    private Institution referredByInstitution;
    String referralId;
    private Institution creditCompany;
    private Institution collectingCentre;
    private Staff staff;
    Staff toStaff;
    private double total;
    private double discount;
    private double vat;
    private double netTotal;
    double netPlusVat;
    private double cashPaid;
    private double cashBalance;
    double cashRemain = cashPaid;
    private BillItem currentBillItem;
    //Bill Items
    private List<BillComponent> lstBillComponents;
    private List<BillFee> lstBillFees;
    private List<BillItem> lstBillItems;
    private List<BillEntry> lstBillEntries;
    private Integer index;
    boolean fromOpdEncounter = false;
    String opdEncounterComments = "";
    int patientSearchTab = 0;
    String comment;
    double opdPaymentCredit;
    BilledBill opdBill;
    Date fromDate;
    Date toDate;
    Department department;
    Institution institution;
    Category category;
    //Print Last Bill
    Bill billPrint;
    List<Bill> billsPrint;
    private List<BillComponent> lstBillComponentsPrint;
    private List<BillFee> lstBillFeesPrint;
    private List<BillItem> lstBillItemsPrint;
    private List<BillEntry> lstBillEntriesPrint;
    BillType billType;

    public BillType getBillType() {
        return billType;
    }

    public void setBillType(BillType billType) {
        this.billType = billType;
    }

    public double getCashRemain() {
        return cashRemain;
    }

    public void setCashRemain(double cashRemain) {
        this.cashRemain = cashRemain;
    }

    @Inject
    private BillBeanController billBean;
    @EJB
    CommonFunctions commonFunctions;
    @EJB
    private PersonFacade personFacade;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private BillComponentFacade billComponentFacade;
    @EJB
    private BillFeeFacade billFeeFacade;
    //Temprory Variable
    private Patient tmpPatient;
    List<Bill> bills;
    List<Bill> selectedBills;
    Double grosTotal;
    Bill bill;
    boolean foreigner = false;
    Date sessionDate;
    String strTenderedValue;
    private YearMonthDay yearMonthDay;
    private PaymentMethodData paymentMethodData;
    @EJB
    private CashTransactionBean cashTransactionBean;

    @Inject
    SearchController searchController;

    public String toAddNewCollectingCentre() {
        return "/lab/collecting_centre";
    }

    public List<Bill> validBillsOfBatchBill(Bill batchBill) {
        String j = "Select b from Bill b where b.backwardReferenceBill=:bb and b.cancelled=false";
        Map m = new HashMap();
        m.put("bb", batchBill);
        return billFacade.findBySQL(j, m);
    }

    public List<Bill> getSelectedBills() {
        return selectedBills;
    }

    public void setSelectedBills(List<Bill> selectedBills) {
        this.selectedBills = selectedBills;
    }

    public void calculateSelectedBillTotals() {
        BillListWithTotals bt = billEjb.calculateBillTotals(selectedBills);
        grosTotal = bt.getGrossTotal();
        netTotal = bt.getNetTotal();
        discount = bt.getDiscount();
        vat = bt.getVat();
        netPlusVat = vat + netTotal;
    }

    public void clear() {
        opdBill = new BilledBill();
        printPreview = false;
        opdPaymentCredit = 0.0;
        comment = null;
        searchController.createTableByKeywordToPayBills();
    }

    public void clearPharmacy() {
        opdBill = new BilledBill();
        printPreview = false;
        opdPaymentCredit = 0.0;
        comment = null;
        searchController.createTablePharmacyCreditToPayBills();
    }

    public void saveBillOPDCredit() {

        BilledBill temp = new BilledBill();

        if (opdPaymentCredit == 0) {
            UtilityController.addErrorMessage("Please Select Correct Paid Amount");
            return;
        }
        if (opdPaymentCredit > opdBill.getBalance()) {
            UtilityController.addErrorMessage("Please Enter Correct Paid Amount");
            return;
        }

        temp.setReferenceBill(opdBill);
        temp.setTotal(opdPaymentCredit);
        temp.setPaidAmount(opdPaymentCredit);
        temp.setNetTotal(opdPaymentCredit);

        temp.setDeptId(getBillNumberGenerator().departmentBillNumberGenerator(getSessionController().getDepartment(), getSessionController().getDepartment(), BillType.CashRecieveBill, BillClassType.BilledBill));
        temp.setInsId(getBillNumberGenerator().institutionBillNumberGenerator(getSessionController().getInstitution(), getSessionController().getDepartment(), BillType.CashRecieveBill, BillClassType.BilledBill, BillNumberSuffix.NONE));
        temp.setBillType(BillType.CashRecieveBill);

        temp.setDepartment(getSessionController().getLoggedUser().getDepartment());
        temp.setInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        temp.setFromDepartment(getSessionController().getLoggedUser().getDepartment());
        temp.setFromInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        temp.setToDepartment(getSessionController().getLoggedUser().getDepartment());

        temp.setComments(comment);

        getBillBean().setPaymentMethodData(temp, paymentMethod, getPaymentMethodData());

        temp.setBillDate(new Date());
        temp.setBillTime(new Date());
        temp.setPaymentMethod(paymentMethod);
        temp.setCreatedAt(new Date());
        temp.setCreater(getSessionController().getLoggedUser());
        getFacade().create(temp);
        //create bill fee payments
        //create bill fee payments
        //create bill fee payments
        //create bill fee payments
        reminingCashPaid = opdPaymentCredit;

        Payment p = createPayment(temp, paymentMethod);

        String sql = "Select bi From BillItem bi where bi.retired=false and bi.bill.id=" + opdBill.getId();
        List<BillItem> billItems = getBillItemFacade().findBySQL(sql);

        for (BillItem bi : billItems) {
            sql = "Select bf From BillFee bf where bf.retired=false and bf.billItem.id=" + bi.getId();

            List<BillFee> billFees = getBillFeeFacade().findBySQL(sql);

            calculateBillfeePayments(billFees, p);
        }
        opdBill.setBalance(opdBill.getBalance() - opdPaymentCredit);
        opdBill.setCashPaid(calBillPaidValue(opdBill));
        opdBill.setNetTotal(calBillPaidValue(opdBill));
        getBillFacade().edit(opdBill);

        JsfUtil.addSuccessMessage("Paid");
        opdBill = temp;
        printPreview = true;

    }

    public void saveBillPharmacyCredit() {

        BilledBill temp = new BilledBill();

        if (opdPaymentCredit == 0) {
            UtilityController.addErrorMessage("Please Select Correct Paid Amount");
            return;
        }
        if (opdPaymentCredit > (opdBill.getNetTotal() - opdBill.getPaidAmount())) {
            UtilityController.addErrorMessage("Please Enter Correct Paid Amount");
            return;
        }

        temp.setReferenceBill(opdBill);
        temp.setTotal(opdPaymentCredit);
        temp.setPaidAmount(opdPaymentCredit);
        temp.setNetTotal(opdPaymentCredit);
        ////// // System.out.println("opdBill.getPaidAmount() = " + opdBill.getPaidAmount());
        ////// // System.out.println("opdPaymentCredit = " + opdPaymentCredit);
        opdBill.setPaidAmount(opdPaymentCredit + opdBill.getPaidAmount());
        ////// // System.out.println("opdBill.getPaidAmount() = " + opdBill.getPaidAmount());
        getBillFacade().edit(opdBill);

        temp.setDeptId(getBillNumberGenerator().departmentBillNumberGenerator(getSessionController().getDepartment(), getSessionController().getDepartment(), BillType.CashRecieveBill, BillClassType.BilledBill));
        temp.setInsId(getBillNumberGenerator().institutionBillNumberGenerator(getSessionController().getInstitution(), getSessionController().getDepartment(), BillType.CashRecieveBill, BillClassType.BilledBill, BillNumberSuffix.NONE));
        temp.setBillType(BillType.CashRecieveBill);

        temp.setDepartment(getSessionController().getLoggedUser().getDepartment());
        temp.setInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        temp.setFromDepartment(getSessionController().getLoggedUser().getDepartment());
        temp.setFromInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        temp.setToDepartment(getSessionController().getLoggedUser().getDepartment());

        temp.setComments(comment);

        getBillBean().setPaymentMethodData(temp, paymentMethod, getPaymentMethodData());

        temp.setBillDate(new Date());
        temp.setBillTime(new Date());
        temp.setPaymentMethod(paymentMethod);
        temp.setCreatedAt(new Date());
        temp.setCreater(getSessionController().getLoggedUser());
        getFacade().create(temp);

        JsfUtil.addSuccessMessage("Paid");
        opdBill = temp;
        printPreview = true;

    }

    public BillNumberGenerator getBillNumberGenerator() {
        return billNumberGenerator;
    }

    public StaffBean getStaffBean() {
        return staffBean;
    }

    public void searchPatientListener() {
        //   createPaymentSchemeItems();
        calTotals();
    }

    public Institution getReferredByInstitution() {
        return referredByInstitution;
    }

    public void setReferredByInstitution(Institution referredByInstitution) {
        this.referredByInstitution = referredByInstitution;
    }

//    public int getRecurseCount() {
//        return recurseCount;
//    }
//
//    public void setRecurseCount(int recurseCount) {
//        this.recurseCount = recurseCount;
//    }
    public boolean findByFilter(String property, String value) {
        String sql = "Select b From Bill b where b.retired=false and upper(b." + property + ") like '%" + value.toUpperCase() + " %'";
        Bill b = getBillFacade().findFirstBySQL(sql);
        //System.err.println("SQL " + sql);
        //System.err.println("Bill " + b);
        if (b != null) {
            return true;
        } else {
            return false;
        }
    }

    public void feeChangeListener(BillFee bf) {
        if (bf.getFeeGrossValue() == null) {
            bf.setFeeGrossValue(0.0);
//            return;
        }

        lstBillItems = null;
        getLstBillItems();
        bf.setTmpChangedValue(bf.getFeeGrossValue());
        calTotals();
    }

    public String getStrTenderedValue() {
        return strTenderedValue;
    }

    public void setStrTenderedValue(String strTenderedValue) {
        this.strTenderedValue = strTenderedValue;
        try {
            cashPaid = Double.parseDouble(strTenderedValue);
        } catch (NumberFormatException e) {
            //////// // System.out.println("Error in converting tendered value. \n " + e.getMessage());
        }
    }

    public Title[] getTitle() {
        return Title.values();
    }

    public Sex[] getSex() {
        return Sex.values();
    }

    public List<Bill> completeOpdCreditBill(String qry) {
        List<Bill> a = null;
        String sql;
        HashMap hash = new HashMap();
        if (qry != null) {
            sql = "select c from BilledBill c "
                    + " where abs(c.netTotal)-abs(c.paidAmount)>:val "
                    + " and c.billType= :btp "
                    + " and c.paymentMethod= :pm "
                    + " and c.cancelledBill is null "
                    + " and c.refundedBill is null "
                    + " and c.retired=false "
                    + " and (upper(c.insId) like :q or"
                    + " upper(c.patient.person.name) like :q "
                    + " or upper(c.creditCompany.name) like :q ) "
                    + " order by c.creditCompany.name";
            hash.put("btp", BillType.OpdBill);
            hash.put("pm", PaymentMethod.Credit);
            hash.put("val", 0.1);
            hash.put("q", "%" + qry.toUpperCase() + "%");
            a = getFacade().findBySQL(sql, hash);
        }
        if (a == null) {
            a = new ArrayList<>();
        }
        return a;
    }

    public List<Bill> completePharmacyCreditBill(String qry) {
        List<Bill> a = null;
        String sql;
        HashMap hash = new HashMap();
        if (qry != null) {
            sql = "select b from BilledBill b "
                    + " where (abs(b.netTotal)-abs(b.paidAmount))>:val "
                    + " and b.billType in :btps "
                    + " and b.paymentMethod= :pm "
                    + " and b.institution=:ins "
                    //                    + " and b.department=:dep "
                    + " and b.retired=false "
                    + " and b.refunded=false "
                    + " and b.cancelled=false "
                    + " and b.toStaff is null "
                    + " and ( upper(b.insId) like :q or "
                    + " upper(b.deptId) like :q or "
                    + " upper(b.toInstitution.name) like :q ) "
                    + " order by b.deptId ";
            hash.put("btps", Arrays.asList(new BillType[]{BillType.PharmacyWholeSale, BillType.PharmacySale}));
            hash.put("pm", PaymentMethod.Credit);
            hash.put("val", 0.1);
            hash.put("q", "%" + qry.toUpperCase() + "%");
            hash.put("ins", getSessionController().getInstitution());
//            hash.put("dep", getSessionController().getDepartment());
//            //// // System.out.println("hash = " + hash);
//            //// // System.out.println("sql = " + sql);
//            //// // System.out.println("getSessionController().getInstitution().getName() = " + getSessionController().getInstitution().getName());
//            //// // System.out.println("getSessionController().getDepartment().getName() = " + getSessionController().getDepartment().getName());
            a = getFacade().findBySQL(sql, hash);
        }
        if (a == null) {
            a = new ArrayList<>();
        }
        return a;
    }

    public List<Bill> completeBillFromDealor(String qry) {
        List<Bill> a = null;
        String sql;
        HashMap hash = new HashMap();
        BillType[] billTypesArrayBilled = {BillType.PharmacyGrnBill, BillType.PharmacyPurchaseBill, BillType.StoreGrnBill, BillType.StorePurchase};
        List<BillType> billTypesListBilled = Arrays.asList(billTypesArrayBilled);
        if (qry != null) {
            sql = "select c from BilledBill c "
                    + "where  abs(c.netTotal)-abs(c.paidAmount)>:val "
                    + " and c.billType in :bts "
                    + " and c.createdAt is not null "
                    + " and c.deptId is not null "
                    + " and c.cancelledBill is null "
                    + " and c.retired=false "
                    + " and c.paymentMethod=:pm  "
                    + " and ((upper(c.deptId) like :q ) "
                    + " or (upper(c.fromInstitution.name)  like :q ))"
                    + " order by c.fromInstitution.name";
            hash.put("bts", billTypesListBilled);
            hash.put("pm", PaymentMethod.Credit);
            hash.put("val", 0.1);
            hash.put("q", "%" + qry.toUpperCase() + "%");
            //     hash.put("pm", PaymentMethod.Credit);
            a = getFacade().findBySQL(sql, hash, 20);
        }
        if (a == null) {
            a = new ArrayList<>();
        }
        return a;
    }

    public List<Bill> completeBillFromDealorStore(String qry) {
        List<Bill> a = null;
        String sql;
        HashMap hash = new HashMap();
        if (qry != null) {
            sql = "select c from BilledBill c "
                    + "where  abs(c.netTotal)-abs(c.paidAmount)>:val "
                    + " and (c.billType= :btp1 or c.billType= :btp2  )"
                    + " and c.createdAt is not null "
                    + " and c.deptId is not null "
                    + " and c.cancelledBill is null "
                    + " and c.retired=false "
                    + " and c.paymentMethod=:pm  "
                    + " and c.fromInstitution.institutionType=:insTp  "
                    + " and ((upper(c.deptId) like :q ) "
                    + " or (upper(c.fromInstitution.name)  like :q ))"
                    + " order by c.fromInstitution.name";
            hash.put("btp1", BillType.PharmacyGrnBill);
            hash.put("btp2", BillType.PharmacyPurchaseBill);
            hash.put("pm", PaymentMethod.Credit);
            hash.put("insTp", InstitutionType.StoreDealor);
            hash.put("val", 0.1);
            hash.put("q", "%" + qry.toUpperCase() + "%");
            //     hash.put("pm", PaymentMethod.Credit);
            a = getFacade().findBySQL(sql, hash, 10);
        }
        if (a == null) {
            a = new ArrayList<>();
        }
        return a;
    }

    public List<Bill> completeSurgeryBills(String qry) {

        String sql;
        Map temMap = new HashMap();
        sql = "select b from BilledBill b "
                + " where b.billType = :billType "
                + " and b.cancelled=false "
                + " and b.retired=false "
                + " and b.patientEncounter.discharged=false ";

        sql += " and  ((upper(b.patientEncounter.patient.person.name) like :q )";
        sql += " or  (upper(b.patientEncounter.bhtNo) like :q )";
        sql += " or  (upper(b.insId) like :q )";
        sql += " or  (upper(b.procedure.item.name) like :q ))";
        sql += " order by b.insId desc  ";

        temMap.put("billType", BillType.SurgeryBill);
        temMap.put("q", "%" + qry.toUpperCase() + "%");
        List<Bill> tmps = getBillFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP, 20);

        return tmps;
    }

    public List<Bill> getDealorBills(Institution institution, List<BillType> billTypes) {
        String sql;
        HashMap hash = new HashMap();

        sql = "select c from BilledBill c where "
                + " abs(c.netTotal)-abs(c.paidAmount)>:val"
                + " and c.billType in :bts"
                + " and c.createdAt is not null "
                + " and c.deptId is not null "
                + " and c.cancelled=false"
                + " and c.retired=false"
                + " and c.paymentMethod=:pm  "
                + " and c.fromInstitution=:ins "
                + " order by c.id ";
        hash.put("bts", billTypes);
        hash.put("pm", PaymentMethod.Credit);
        hash.put("val", 0.1);
        hash.put("ins", institution);
        //     hash.put("pm", PaymentMethod.Credit);
        List<Bill> bill = getFacade().findBySQL(sql, hash);

        if (bill == null) {
            bill = new ArrayList<>();
        }

        return bill;
    }

    public List<Bill> getCreditBills(Institution institution) {
        String sql;
        HashMap hash = new HashMap();

        sql = "select c from BilledBill c  where"
                + " abs(c.netTotal)-abs(c.paidAmount)>:val "
                + " and c.billType= :btp"
                + " and c.createdAt is not null "
                + " and c.deptId is not null "
                + " and c.cancelled=false"
                + " and c.retired=false"
                + " and c.paymentMethod=:pm  "
                + " and c.creditCompany=:ins "
                + " order by c.id ";
        hash.put("btp", BillType.OpdBill);
        hash.put("pm", PaymentMethod.Credit);
        hash.put("val", 0.1);
        hash.put("ins", institution);
        //     hash.put("pm", PaymentMethod.Credit);
        List<Bill> bill = getFacade().findBySQL(sql, hash);

        if (bill == null) {
            bill = new ArrayList<>();
        }

        return bill;
    }

    public List<Bill> getCreditBillsPharmacy(Institution institution) {
        String sql;
        HashMap hash = new HashMap();

        sql = "select b from BilledBill b  where"
                + " (abs(b.netTotal)-abs(b.paidAmount))>:val "
                + " and b.billType in :btps"
                + " and b.createdAt is not null "
                + " and b.deptId is not null "
                + " and b.cancelled=false"
                + " and b.retired=false"
                + " and b.paymentMethod=:pm  "
                + " and b.toInstitution=:company "
                + " and b.institution=:ins "
                //                + " and b.department=:dep "
                + " order by b.id ";
        hash.put("btps", Arrays.asList(new BillType[]{BillType.PharmacyWholeSale, BillType.PharmacySale}));
        hash.put("pm", PaymentMethod.Credit);
        hash.put("val", 0.1);
        hash.put("company", institution);
        hash.put("ins", getSessionController().getInstitution());
//        hash.put("dep", getSessionController().getDepartment());
        //     hash.put("pm", PaymentMethod.Credit);
        List<Bill> bill = getFacade().findBySQL(sql, hash);

        if (bill == null) {
            bill = new ArrayList<>();
        }

        return bill;
    }

    public List<Bill> getCreditBills(Institution institution, Date fd, Date td) {
        String sql;
        HashMap m = new HashMap();

        sql = "select c from BilledBill c  where"
                + " abs(c.netTotal)-abs(c.paidAmount)>:val "
                + " and c.billType= :btp"
                + " and c.createdAt between :fd and :td "
                + " and c.deptId is not null "
                + " and c.cancelled=false"
                + " and c.retired=false"
                + " and c.paymentMethod=:pm  "
                + " and c.creditCompany=:ins "
                + " order by c.id ";
        m.put("btp", BillType.OpdBill);
        m.put("pm", PaymentMethod.Credit);
        m.put("val", 0.1);
        m.put("ins", institution);
        m.put("fd", fd);
        m.put("td", td);
        List<Bill> bill = getFacade().findBySQL(sql, m, TemporalType.TIMESTAMP);

        if (bill == null) {
            bill = new ArrayList<>();
        }
        return bill;
    }

    public List<Bill> getBills(Date fromDate, Date toDate, BillType billType1, BillType billType2, Institution institution) {
        String sql;
        HashMap hm = new HashMap();
        sql = "Select b From Bill b where"
                + "  b.retired=false"
                + "  and b.createdAt between :frm and :to"
                + " and (b.fromInstitution=:ins "
                + " or b.toInstitution=:ins) "
                + " and (b.billType=:tp1"
                + " or b.billType=:tp2)";
        hm.put("frm", fromDate);
        hm.put("to", toDate);
        hm.put("ins", institution);
        hm.put("tp1", billType1);
        hm.put("tp2", billType2);
        return getBillFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);

    }

    public void getOpdBills() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.OpdBill};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, null);
        if (r == null) {
            r = new BillListWithTotals();
            bills = r.getBills();
            netTotal = r.getNetTotal();
            discount = r.getDiscount();
            grosTotal = r.getGrossTotal();
            vat = r.getVat();
            return;
        }
        if (r.getBills() != null) {
            bills = r.getBills();
        }
        if (r.getNetTotal() != null) {
            netTotal = r.getNetTotal();
        }
        if (r.getDiscount() != null) {
            discount = r.getDiscount();
        }
        if (r.getVat() != null) {
            vat = r.getVat();
        }
        if (r.getGrossTotal() != null) {
            grosTotal = r.getGrossTotal();
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "List of bills raised(/opd_bill_report.xhtml)");
    }

    public void onLineSettleBills() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.OpdBill, billType.InwardPaymentBill};
        PaymentMethod[] paymentMethods = {PaymentMethod.OnlineSettlement};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, paymentMethods);
        if (r == null) {
            r = new BillListWithTotals();
            bills = r.getBills();
            netTotal = r.getNetTotal();
            discount = r.getDiscount();
            grosTotal = r.getGrossTotal();
            vat = r.getVat();
            return;
        }
        if (r.getBills() != null) {
            bills = r.getBills();
        }
        if (r.getNetTotal() != null) {
            netTotal = r.getNetTotal();
        }
        if (r.getDiscount() != null) {
            discount = r.getDiscount();
        }
        if (r.getVat() != null) {
            vat = r.getVat();
        }
        if (r.getGrossTotal() != null) {
            grosTotal = r.getGrossTotal();
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "List of bills raised(/opd_bill_report.xhtml)");
    }

    public void getPharmacySaleBills() {
        Date startTime = new Date();

        BillType[] billTypes;
        if (billType == null) {
            billTypes = new BillType[]{BillType.PharmacySale, BillType.PharmacyWholeSale};
        } else {
            billTypes = new BillType[]{billType};
        }

        BillListWithTotals r = null;

        if (paymentMethod == null) {
            r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, null);
        } else {
            PaymentMethod[] pms = new PaymentMethod[]{
                paymentMethod,};
            r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, pms);
        }

        if (r == null) {
            r = new BillListWithTotals();
            bills = r.getBills();
            netTotal = r.getNetTotal();
            discount = r.getDiscount();
            vat = r.getVat();
            grosTotal = r.getGrossTotal();
            return;
        }
        if (r.getBills() != null) {
            bills = r.getBills();
        }
        if (r.getNetTotal() != null) {
            netTotal = r.getNetTotal();
        }
        if (r.getDiscount() != null) {
            discount = r.getDiscount();
        }
        if (r.getVat() != null) {
            vat = r.getVat();
        }
        if (r.getGrossTotal() != null) {
            grosTotal = r.getGrossTotal();
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Pharmacy/Reports/Bill Lists/List of pharmacy bill(/faces/pharmacy/list_of_all_sale_bills.xhtml)");
    }

    public Double getGrosTotal() {
        return grosTotal;
    }

    public void setGrosTotal(Double grosTotal) {
        this.grosTotal = grosTotal;
    }

    public void getPharamacyWholeSaleCreditBills() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.PharmacyWholeSale};
        PaymentMethod[] paymentMethods = {PaymentMethod.Credit};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, paymentMethods);
        bills = r.getBills();
        netTotal = r.getNetTotal();
        discount = r.getDiscount();
        grosTotal = r.getGrossTotal();
        vat = r.getVat();
        commonController.printReportDetails(fromDate, toDate, startTime, "Pharmacy/Reports/Summeries/Pharmacy wholesale report/Pharmacy wholeale credit bills(/faces/pharmacy_wholesale/pharmacy_report_credit.xhtml)");
    }

    public void getPharmacyBills() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.PharmacySale};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, null);
        bills = r.getBills();
        netTotal = r.getNetTotal();
        discount = r.getDiscount();
        grosTotal = r.getGrossTotal();
        vat = r.getVat();
        commonController.printReportDetails(fromDate, toDate, startTime, "Pharmacy/Reports/Summeries/Pharmacy all sale report/Pharmacy sale report(/faces/pharmacy/pharmacy_bill_report.xhtml)");
    }

    public void getPharmacyBillsBilled() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.PharmacyPre};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, null);
        bills = r.getBills();
        netTotal = r.getNetTotal();
        discount = r.getDiscount();
        grosTotal = r.getGrossTotal();
        vat = r.getVat();
        commonController.printReportDetails(fromDate, toDate, startTime, "Pharmacy/Reports/Summeries/Pharmacy all sale report/Pharmacy sale report(/faces/pharmacy/pharmacy_bill_report.xhtml)");
    }

    public void getPharmacyWholeBills() {
        Date startTime = new Date();

        BillType[] billTypes = {BillType.PharmacySale};
        BillListWithTotals r = billEjb.findBillsAndTotals(fromDate, toDate, billTypes, null, department, institution, null);
        bills = r.getBills();
        netTotal = r.getNetTotal();
        discount = r.getDiscount();
        grosTotal = r.getGrossTotal();
        vat = r.getVat();
        commonController.printReportDetails(fromDate, toDate, startTime, "Pharmacy/Reports/Summeries/Pharmacy all sale report/Pharmacy wholesale report(/faces/pharmacy_wholesale/pharmacy_whole_bill_report.xhtml)");
    }

    public BillEjb getBillEjb() {
        return billEjb;
    }

    public void setBillEjb(BillEjb billEjb) {
        this.billEjb = billEjb;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = CommonFunctions.getStartOfDay(new Date());
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = CommonFunctions.getEndOfDay(new Date());
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SearchController getSearchController() {
        return searchController;
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }

    public MembershipSchemeController getMembershipSchemeController() {
        return membershipSchemeController;
    }

    public void setMembershipSchemeController(MembershipSchemeController membershipSchemeController) {
        this.membershipSchemeController = membershipSchemeController;
    }

    public Date getSessionDate() {
        if (sessionDate == null) {
            sessionDate = Calendar.getInstance().getTime();
        }
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public boolean isForeigner() {
        return foreigner;
    }

    public void setForeigner(boolean foreigner) {
        this.foreigner = foreigner;
    }

    public List<Bill> getBills() {
        if (bills == null) {
            bills = new ArrayList<>();
        }
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    private void savePatient() {
        switch (getPatientTabId()) {
            case "tabNewPt":
                getNewPatient().setPhn(applicationController.createNewPersonalHealthNumber(getSessionController().getInstitution()));
                getNewPatient().setCreatedInstitution(getSessionController().getInstitution());
                getNewPatient().setCreater(getSessionController().getLoggedUser());
                getNewPatient().setCreatedAt(new Date());
                getNewPatient().getPerson().setCreater(getSessionController().getLoggedUser());
                getNewPatient().getPerson().setCreatedAt(new Date());
                try {
                    getPersonFacade().create(getNewPatient().getPerson());
                } catch (Exception e) {
                    getPersonFacade().edit(getNewPatient().getPerson());
                }
                try {
                    getPatientFacade().create(getNewPatient());
                } catch (Exception e) {
                    getPatientFacade().edit(getNewPatient());
                }
                tmpPatient = getNewPatient();
                break;
            case "tabSearchPt":
                tmpPatient = getSearchedPatient();
                break;
        }
    }

    public boolean putToBills() {
        bills = new ArrayList<>();
        Set<Department> billDepts = new HashSet<>();
        for (BillEntry e : lstBillEntries) {
            billDepts.add(e.getBillItem().getItem().getDepartment());
        }

        for (Department d : billDepts) {
            Bill myBill = new BilledBill();
            myBill = saveBill(d, myBill);

            if (myBill == null) {
                return false;
            }

            List<BillEntry> tmp = new ArrayList<>();

            for (BillEntry e : lstBillEntries) {
                if (Objects.equals(e.getBillItem().getItem().getDepartment().getId(), d.getId())) {
//                    BillItem bi = getBillBean().saveBillItem(myBill, e, getSessionController().getLoggedUser());
                    //for create Bill fee Payments
                    BillItem bi = getBillBean().saveBillItem(myBill, e, getSessionController().getLoggedUser());
                    //getBillBean().calculateBillItem(myBill, e);
                    myBill.getBillItems().add(bi);
                    tmp.add(e);
                }
            }

            if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
                myBill.setCashPaid(cashPaid);
            }

            getBillFacade().edit(myBill);

            getBillBean().calculateBillItems(myBill, tmp);
            createPaymentsForBills(myBill, tmp);

            bills.add(myBill);
        }

        return true;
    }

    public void setPrintigBill() {
        ////// // System.out.println("In Print");
        billPrint = bill;
        billsPrint = bills;
        lstBillComponentsPrint = lstBillComponents;
        lstBillEntriesPrint = lstBillEntries;
        lstBillFeesPrint = lstBillFees;
        lstBillItemsPrint = lstBillItems;
        ////// // System.out.println("Out Print");
    }

    public void settleBill() {
        Date startTime = new Date();
        if (errorCheck()) {
            return;
        }

        savePatient();

        if (getBillBean().checkDepartment(getLstBillEntries()) == 1) {
            BilledBill temp = new BilledBill();
            Bill b = saveBill(lstBillEntries.get(0).getBillItem().getItem().getDepartment(), temp);

            if (b == null) {
                return;
            }

            List<BillItem> list = new ArrayList<>();
            for (BillEntry billEntry : getLstBillEntries()) {
                list.add(getBillBean().saveBillItem(b, billEntry, getSessionController().getLoggedUser()));
                //for Create Bill Fee Payments
//                list.add(getBillBean().saveBillItem(b, billEntry, getSessionController().getLoggedUser(),p));
            }

            b.setBillItems(list);
            b.setBillTotal(b.getNetTotal());

            getBillFacade().edit(b);
            getBillBean().calculateBillItems(b, getLstBillEntries());

            if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
                b.setCashPaid(cashPaid);
                if (cashPaid >= b.getTransSaleBillTotalMinusDiscount()) {
                    b.setBalance(0.0);
                    b.setNetTotal(b.getTransSaleBillTotalMinusDiscount());
                } else {
                    b.setBalance(b.getTransSaleBillTotalMinusDiscount() - b.getCashPaid());
                    b.setNetTotal(b.getCashPaid());
                }
            }

            b.setVat(b.getVat());
            b.setVatPlusNetTotal(b.getNetTotal() + b.getVat());

            createPaymentsForBills(b, getLstBillEntries());

            getBillFacade().edit(b);
            getBills().add(b);

        } else {
            boolean result = putToBills();
            if (result == false) {
                return;
            }
        }

        saveBatchBill();
        saveBillItemSessions();

        if (toStaff != null && getPaymentMethod() == PaymentMethod.Credit) {
            staffBean.updateStaffCredit(toStaff, netPlusVat);
            UtilityController.addSuccessMessage("User Credit Updated");
        }

        UtilityController.addSuccessMessage("Bill Saved");
        setPrintigBill();
        checkBillValues();
        printPreview = true;

        commonController.printReportDetails(null, null, startTime, "OPD Billing(/faces/opd_bill.xhtml)");
    }

    public boolean checkBillValues(Bill b) {
        if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
            return false;
        }

        Double[] billItemValues = billBean.fetchBillItemValues(b);
        double billItemTotal = billItemValues[0];
        double billItemDiscount = billItemValues[1];
        double billItemNetTotal = billItemValues[2];
        double billItemVatPlusNetValue = billItemValues[3] + billItemValues[2];

        //// // System.out.println("b.getVatPlusNetTotal() = " + b.getVatPlusNetTotal());
        //// // System.out.println("roundOff(b.getVatPlusNetTotal()) = " + roundOff(b.getVatPlusNetTotal()));
        //// // System.out.println("billItemVatPlusNetValue = " + billItemVatPlusNetValue);
        //// // System.out.println("roundOff(billItemVatPlusNetValue) = " + roundOff(billItemVatPlusNetValue));

        if (billItemTotal != b.getTotal() || billItemDiscount != b.getDiscount() || billItemNetTotal != b.getNetTotal() || roundOff(billItemVatPlusNetValue) != roundOff(b.getVatPlusNetTotal())) {
            return true;
        }

        Double[] billFeeValues = billBean.fetchBillFeeValues(b);
        double billFeeTotal = billFeeValues[0];
        double billFeeDiscount = billFeeValues[1];
        double billFeeNetTotal = billFeeValues[2];
        double billFeeVatPlusNetValue = billFeeValues[3] + billFeeValues[2];

        //// // System.out.println("b.getVatPlusNetTotal() = " + b.getVatPlusNetTotal());
        //// // System.out.println("billItemVatPlusNetValue = " + roundOff(billItemVatPlusNetValue));

        if (billFeeTotal != b.getTotal() || billFeeDiscount != b.getDiscount() || billFeeNetTotal != b.getNetTotal() || roundOff(billItemVatPlusNetValue) != roundOff(b.getVatPlusNetTotal())) {
            return true;
        }


        return false;
    }

    public void checkBillValues() {
        for (Bill b : getBills()) {
            boolean flag = checkBillValues(b);
            b.setTransError(flag);
        }
    }

    @EJB
    StaffBean staffBean;

    private void saveBillItemSessions() {
        for (BillEntry be : lstBillEntries) {
            be.getBillItem().setBillSession(getServiceSessionBean().createBillSession(be.getBillItem()));
            if (be.getBillItem().getBillSession() != null) {
                getBillSessionFacade().create(be.getBillItem().getBillSession());
            }
        }
    }
    @EJB
    private BatchBillFacade batchBillFacade;

    private void saveBatchBill() {
        Bill tmp = new BilledBill();
        tmp.setBillType(BillType.OpdBathcBill);
        tmp.setPaymentScheme(paymentScheme);
        tmp.setPaymentMethod(paymentMethod);
        tmp.setCreatedAt(new Date());
        tmp.setCreater(getSessionController().getLoggedUser());
        getBillFacade().create(tmp);

        double dbl = 0;
        double reminingCashPaid = cashPaid;
        for (Bill b : bills) {
            b.setBackwardReferenceBill(tmp);
            dbl += b.getNetTotal();

            if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
                b.setCashPaid(reminingCashPaid);

                if (reminingCashPaid > b.getTransSaleBillTotalMinusDiscount()) {
                    b.setBalance(0.0);
                    b.setNetTotal(b.getTransSaleBillTotalMinusDiscount());
                } else {
                    b.setBalance(b.getTotal() - b.getCashPaid());
                    b.setNetTotal(reminingCashPaid);
                }
            }
            reminingCashPaid = reminingCashPaid - b.getNetTotal();

            getBillFacade().edit(b);

            tmp.getForwardReferenceBills().add(b);
        }

        tmp.setNetTotal(dbl);
        getBillFacade().edit(tmp);

        WebUser wb = getCashTransactionBean().saveBillCashInTransaction(tmp, getSessionController().getLoggedUser());
        getSessionController().setLoggedUser(wb);
    }

    @Inject
    private BillSearch billSearch;

    public void cancellAll() {
        Bill tmp = new CancelledBill();
        tmp.setCreatedAt(new Date());
        tmp.setCreater(getSessionController().getLoggedUser());
        getBillFacade().create(tmp);

        Bill billedBill = null;
        for (Bill b : bills) {
            billedBill = b.getBackwardReferenceBill();
            getBillSearch().setBill((BilledBill) b);
            getBillSearch().setPaymentMethod(b.getPaymentMethod());
            getBillSearch().setComment("Batch Cancell");
            //////// // System.out.println("ggg : " + getBillSearch().getComment());
            getBillSearch().cancelBill();
        }

        tmp.copy(billedBill);
        tmp.setBilledBill(billedBill);

        WebUser wb = getCashTransactionBean().saveBillCashOutTransaction(tmp, getSessionController().getLoggedUser());
        getSessionController().setLoggedUser(wb);
    }

    public void dateChangeListen() {
        getNewPatient().getPerson().setDob(getCommonFunctions().guessDob(yearMonthDay));

    }

    private Bill saveBill(Department bt, Bill temp) {
        temp.setBillType(BillType.OpdBill);
        temp.setDepartment(getSessionController().getDepartment());
        temp.setInstitution(getSessionController().getInstitution());
        temp.setToDepartment(bt);
        temp.setToInstitution(bt.getInstitution());

        temp.setFromDepartment(getSessionController().getLoggedUser().getDepartment());
        temp.setFromInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        temp.setStaff(staff);
        temp.setToStaff(toStaff);
        temp.setReferredBy(referredBy);
        temp.setReferralNumber(referralId);
        temp.setReferredByInstitution(referredByInstitution);
        temp.setCreditCompany(creditCompany);
        temp.setCollectingCentre(collectingCentre);
        temp.setComments(comment);

        getBillBean().setPaymentMethodData(temp, paymentMethod, getPaymentMethodData());

        temp.setBillDate(new Date());
        temp.setBillTime(new Date());
        temp.setPatient(tmpPatient);

        temp.setMembershipScheme(membershipSchemeController.fetchPatientMembershipScheme(tmpPatient, getSessionController().getApplicationPreference().isMembershipExpires()));

        temp.setPaymentScheme(getPaymentScheme());
        temp.setPaymentMethod(paymentMethod);
        temp.setCreatedAt(new Date());
        temp.setCreater(getSessionController().getLoggedUser());

        //SETTING INS ID
        recurseCount = 0;
        String insId = generateBillNumberInsId(temp);

        if (insId.equals("")) {
            return null;
        }
        temp.setInsId(insId);
        if (temp.getId() == null) {
            getFacade().create(temp);
        } else {
            getFacade().edit(temp);
        }

        //Department ID (DEPT ID)
        String deptId = getBillNumberGenerator().departmentBillNumberGenerator(temp.getDepartment(), temp.getToDepartment(), temp.getBillType(), BillClassType.BilledBill);
        temp.setDeptId(deptId);

        if (temp.getId() == null) {
            getFacade().create(temp);
        } else {
            getFacade().edit(temp);
        }
        return temp;

    }

    int recurseCount = 0;

    private String generateBillNumberInsId(Bill bill) {

        ////// // System.out.println("getBillNumberGenerator() = " + getBillNumberGenerator());
        ////// // System.out.println("bill = " + bill);
        ////// // System.out.println("bill.getInstitution() = " + bill.getInstitution());
        String insId = getBillNumberGenerator().institutionBillNumberGenerator(bill.getInstitution(), bill.getToDepartment(), bill.getBillType(), BillClassType.BilledBill, BillNumberSuffix.NONE);
//        try {
//            insId = getBillNumberGenerator().institutionBillNumberGenerator(bill, bill.getToDepartment(), BillClassType.BilledBill, BillNumberSuffix.NONE);
//        } catch (Exception e) {
//            if (recurseCount < 50) {
//                insId = generateBillNumberInsId(bill);
//                recurseCount++;
//            }
//        }

        return insId;
    }

    private boolean checkPatientAgeSex() {
        if (getPatientTabId().equals("tabNewPt")) {
            if (getNewPatient().getPerson().getName() == null || getNewPatient().getPerson().getName().trim().equals("") || getNewPatient().getPerson().getSex() == null || getNewPatient().getPerson().getDob() == null) {
                UtilityController.addErrorMessage("Can not bill without Patient Name, Age or Sex.");
                return true;
            }
            if (!com.kajabuyahmis.java.CommonFunctions.checkAgeSex(getNewPatient().getPerson().getDob(), getNewPatient().getPerson().getSex(), getNewPatient().getPerson().getTitle())) {
                UtilityController.addErrorMessage("Mismatch in Title and Gender. Please Check the Title, Age and Sex");
                return true;
            }
        }
        return false;
    }

    private boolean institutionReferranceNumberExist() {
        String jpql;
        HashMap m = new HashMap();
        jpql = "Select b from Bill b where "
                + "b.retired = false and "
                + "upper(b.referralNumber) =:rid ";
        m.put("rid", referralId.toUpperCase());
        List<Bill> tempBills = getFacade().findBySQL(jpql, m);
        if (tempBills == null || tempBills.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean errorCheck() {
        if (getLstBillEntries().isEmpty()) {
            UtilityController.addErrorMessage("No Items are added to the bill to settle");
            return true;
        }

        if (getPatientTabId().equals("tabSearchPt")) {
            if (getSearchedPatient() == null) {
                UtilityController.addErrorMessage("Plese Select Patient");
                return true;
            }
        }

        if (getPatientTabId().equals("tabNewPt")) {
            if (getNewPatient() == null) {
                UtilityController.addErrorMessage("New Patient is NULL. Programming Error. Contact Developer.");
                return true;
            }
            if (getNewPatient().getPerson() == null) {
                UtilityController.addErrorMessage("New Patient's Person is NULL. Programming Error. Contact Developer.");
                return true;
            }
            if (getNewPatient().getPerson().getName() == null
                    || getNewPatient().getPerson().getName().trim().equals("")) {
                UtilityController.addErrorMessage("Can not bill without a name for the new Patient !");
                return true;
            }

            if (!sessionController.getApplicationPreference().isOpdSettleWithoutPatientPhoneNumber()) {
                if (getNewPatient().getPerson().getPhone() == null) {
                    UtilityController.addErrorMessage("Please Enter a Phone Number");
                    return true;
                }
                if (getNewPatient().getPerson().getPhone().trim().equals("")) {
                    UtilityController.addErrorMessage("Please Enter a Phone Number");
                    return true;
                }
            }

        }

//
//        if (!sessionController.getApplicationPreference().getCanSettleOpdBillWithoutReferringDoctor()) {
//            for (BillEntry be : getLstBillEntries()) {
//                if (be.getBillItem().getItem() instanceof Investigation) {
//                    if (referredBy == null && referredByInstitution == null) {
//                        UtilityController.addErrorMessage("Please Select a Refering Doctor or a Referring Institute. It is Requierd for Investigations.");
//                        return true;
//                    }
//                }
//            }
//        }
//            if (getStrTenderedValue() == null) {
//                UtilityController.addErrorMessage("Please Enter Tenderd Amount");
//                return true;
//            }
//            if (cashPaid < (vat + netTotal)) {
//                UtilityController.addErrorMessage("Please Enter Correct Tenderd Amount");
//                return true;
//            }
//        if (referredByInstitution != null && referredByInstitution.getInstitutionType() != InstitutionType.CollectingCentre) {
//            if (referralId == null || referralId.trim().equals("")) {
//                JsfUtil.addErrorMessage("Please Enter Referrance Number");
//                return true;
//            } else if (institutionReferranceNumberExist()) {
//
//                JsfUtil.addErrorMessage("Alredy Entered");
//                return true;
//            }
//
//        }
        boolean checkAge = false;
        for (BillEntry be : getLstBillEntries()) {
            if (be.getBillItem().getItem().getDepartment().getDepartmentType() == DepartmentType.Lab) {
                //  //System.err.println("ttttt");
                checkAge = true;
                break;
            }
        }

        if (checkAge && checkPatientAgeSex()) {
            return true;
        }

        if (getPaymentMethod() == null) {
            UtilityController.addErrorMessage("Select Payment Scheme");
            return true;
        }

        if (getPaymentSchemeController().errorCheckPaymentMethod(paymentMethod, getPaymentMethodData())) {
            return true;
        }

        if (paymentMethod != null && paymentMethod == PaymentMethod.Credit) {
            if (toStaff == null && creditCompany == null && collectingCentre == null) {
                UtilityController.addErrorMessage("Please select Staff Member under welfare or credit company or Collecting centre.");
                return true;
            }
            if (toStaff != null && creditCompany != null) {
                UtilityController.addErrorMessage("Both staff member and a company is selected. Please select either Staff Member under welfare or credit company.");
                return true;
            }
            if (toStaff != null) {
                if (toStaff.getAnnualWelfareUtilized() + netTotal > toStaff.getAnnualWelfareQualified()) {
                    UtilityController.addErrorMessage("No enough walfare credit.");
                    return true;
                }
            }
        }

//        if ((getCreditCompany() != null || toStaff != null) && (paymentMethod != PaymentMethod.Credit && paymentMethod != PaymentMethod.Cheque && paymentMethod != PaymentMethod.Slip)) {
//            UtilityController.addErrorMessage("Check Payment method");
//            return true;
//        }
        if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {

            if (cashPaid == 0.0) {
                UtilityController.addErrorMessage("Please enter the paid amount");
                return true;
            }

        }

//        if (getPaymentSchemeController().checkPaid(paymentScheme.getPaymentMethod(), getCashPaid(), getNetTotal())) {
//            return true;
//        }
        return false;
    }

    public PaymentSchemeController getPaymentSchemeController() {
        return paymentSchemeController;
    }

    public void setPaymentSchemeController(PaymentSchemeController paymentSchemeController) {
        this.paymentSchemeController = paymentSchemeController;
    }

    List<BillSession> billSessions;
    @EJB
    BillSessionFacade billSessionFacade;
    @Inject
    ServiceSessionFunctions serviceSessionBean;

    public List<BillSession> getBillSessions() {
        return billSessions;
    }

    public void fillBillSessions(SelectEvent event) {
        ////// // System.out.println("event = " + event);
        ////// // System.out.println("this = filling bill sessions");
        if (lastBillItem != null && lastBillItem.getItem() != null) {
            billSessions = getServiceSessionBean().getBillSessions(lastBillItem.getItem(), getSessionDate());
            ////// // System.out.println("billSessions = " + billSessions);
        } else ////// // System.out.println("billSessions = " + billSessions);
        if (billSessions == null || !billSessions.isEmpty()) {
            ////// // System.out.println("new array");
            billSessions = new ArrayList<>();
        }
    }

    public void fillBillSessionsLstner() {
        if (lastBillItem != null && lastBillItem.getItem() != null) {
            billSessions = getServiceSessionBean().getBillSessions(lastBillItem.getItem(), getSessionDate());
        } else if (billSessions == null || !billSessions.isEmpty()) {
            billSessions = new ArrayList<>();
        }
    }

    public ServiceSessionFunctions getServiceSessionBean() {
        return serviceSessionBean;
    }

    public void setServiceSessionBean(ServiceSessionFunctions serviceSessionBean) {
        this.serviceSessionBean = serviceSessionBean;
    }

    public BillSessionFacade getBillSessionFacade() {
        return billSessionFacade;
    }

    public void setBillSessionFacade(BillSessionFacade billSessionFacade) {
        this.billSessionFacade = billSessionFacade;
    }

    public void setBillSessions(List<BillSession> billSessions) {
        this.billSessions = billSessions;
    }

    public void addToBill() {

        if (getCurrentBillItem() == null) {
            UtilityController.addErrorMessage("Nothing to add");
            return;
        }
        if (getCurrentBillItem().getItem() == null) {
            UtilityController.addErrorMessage("Please select an Item");
            return;
        }
        if (getCurrentBillItem().getItem().getTotal() == 0.0) {
            UtilityController.addErrorMessage("Please corect item fee");
            return;
        }

        if (getCurrentBillItem().getItem().getDepartment() == null) {
            UtilityController.addErrorMessage("Please set Department to Item");
            return;
        }

        if (getCurrentBillItem().getItem().getCategory() == null) {
            UtilityController.addErrorMessage("Please set Category to Item");
            return;
        }
        if (getCurrentBillItem().getItem().getPriority() != null) {
            getCurrentBillItem().setPriority(getCurrentBillItem().getItem().getPriority());
        }
        if (getCurrentBillItem().getQty() == null) {
            getCurrentBillItem().setQty(1.0);
        }
        double qty = getCurrentBillItem().getQty();
        for (int i = 0; i < qty; i++) {
            BillItem bi = new BillItem();
            bi.copy(getCurrentBillItem());
            bi.setSessionDate(sessionDate);
//        New Session
            //   getCurrentBillItem().setBillSession(getServiceSessionBean().createBillSession(getCurrentBillItem()));
//        New Session
            //   getCurrentBillItem().setBillSession(getServiceSessionBean().createBillSession(getCurrentBillItem()));
            lastBillItem = bi;
            BillEntry addingEntry = new BillEntry();
            addingEntry.setBillItem(bi);
            addingEntry.setLstBillComponents(getBillBean().billComponentsFromBillItem(bi));
            addingEntry.setLstBillFees(getBillBean().billFeefromBillItem(bi));
            addingEntry.setLstBillSessions(getBillBean().billSessionsfromBillItem(bi));
            getLstBillEntries().add(addingEntry);
            bi.setRate(getBillBean().billItemRate(addingEntry));
            bi.setQty(1.0);
            bi.setNetValue(bi.getRate() * bi.getQty()); // Price == Rate as Qty is 1 here

            if (bi.getItem().isVatable()) {
                bi.setVat(bi.getNetValue() * bi.getItem().getVatPercentage() / 100);
            }

            bi.setVatPlusNetValue(bi.getNetValue() + bi.getVat());

            calTotals();

            if (bi.getNetValue() == 0.0) {
                UtilityController.addErrorMessage("Please enter the rate");
                return;
            }
        }
        clearBillItemValues();
        //UtilityController.addSuccessMessage("Item Added");
    }

    public void clearBillItemValues() {
        currentBillItem = null;

        recreateBillItems();
    }

    private void clearBillValues() {
        setNewPatient(null);
        setSearchedPatient(null);
        setReferredBy(null);
        setReferredByInstitution(null);
        setReferralId(null);
        setSessionDate(null);
        setCreditCompany(null);
        setCollectingCentre(null);
        setYearMonthDay(null);
        setBills(null);
        setPaymentScheme(null);
        paymentMethod = PaymentMethod.Cash;
        paymentMethodData = null;
        currentBillItem = null;
        setLstBillComponents(null);
        setLstBillEntries(null);
        setLstBillFees(null);
        setStaff(null);
        setToStaff(null);
        setComment(null);
        lstBillEntries = new ArrayList<>();
        setForeigner(false);
        setSessionDate(Calendar.getInstance().getTime());
        calTotals();

        setCashPaid(0.0);
        setDiscount(0.0);
        setCashBalance(0.0);

        setStrTenderedValue("");

        patientTabId = "tabNewPt";

        fromOpdEncounter = false;
        opdEncounterComments = "";
        patientSearchTab = 0;
    }

    private void clearBillValuesForMember() {
        setNewPatient(null);
        setReferredBy(null);
        setReferredByInstitution(null);
        setReferralId(null);
        setSessionDate(null);
        setCreditCompany(null);
        setCollectingCentre(null);
        setYearMonthDay(null);
        setBills(null);
        setPaymentScheme(null);
        paymentMethod = PaymentMethod.Cash;
        paymentMethodData = null;
        currentBillItem = null;
        setLstBillComponents(null);
        setLstBillEntries(null);
        setLstBillFees(null);
        setStaff(null);
        setToStaff(null);
        setComment(null);
        lstBillEntries = new ArrayList<>();
        setForeigner(false);
        setSessionDate(Calendar.getInstance().getTime());
        calTotals();

        setCashPaid(0.0);
        setDiscount(0.0);
        setCashBalance(0.0);

        setStrTenderedValue("");

        patientTabId = "tabSearchPt";

        fromOpdEncounter = false;
        opdEncounterComments = "";
        patientSearchTab = 1;
    }

    private void recreateBillItems() {
        //Only remove Total and BillComponenbts,Fee and Sessions. NOT bill Entries
        lstBillComponents = null;
        lstBillFees = null;
        lstBillItems = null;

        //billTotal = 0.0;
    }

    @Inject
    PriceMatrixController priceMatrixController;

    public PriceMatrixController getPriceMatrixController() {
        return priceMatrixController;
    }

    public void setPriceMatrixController(PriceMatrixController priceMatrixController) {
        this.priceMatrixController = priceMatrixController;
    }

    @Inject
    MembershipSchemeController membershipSchemeController;

    public void calTotals() {
//     //   ////// // System.out.println("calculating totals");
        if (paymentMethod == null) {
            return;
        }

        if (toStaff != null) {
            paymentScheme = null;
            creditCompany = null;
        }

        double billDiscount = 0.0;
        double billGross = 0.0;
        double billNet = 0.0;
        double billVat = 0.0;

        MembershipScheme membershipScheme = membershipSchemeController.fetchPatientMembershipScheme(getSearchedPatient(), getSessionController().getApplicationPreference().isMembershipExpires());


        for (BillEntry be : getLstBillEntries()) {
            //////// // System.out.println("bill item entry");
            double entryGross = 0.0;
            double entryDis = 0.0;
            double entryNet = 0.0;
            double entryVat = 0.0;
            double entryVatPlusNet = 0.0;

            BillItem bi = be.getBillItem();

            for (BillFee bf : be.getLstBillFees()) {
                Department department = null;
                Item item = null;
                PriceMatrix priceMatrix;
                Category category = null;

                if (bf.getBillItem() != null && bf.getBillItem().getItem() != null) {
                    department = bf.getBillItem().getItem().getDepartment();

                    item = bf.getBillItem().getItem();
                }

                //Membership Scheme
                if (membershipScheme != null) {
                    priceMatrix = getPriceMatrixController().getOpdMemberDisCount(paymentMethod, membershipScheme, department, category);
                    getBillBean().setBillFees(bf, isForeigner(), paymentMethod, membershipScheme, bi.getItem(), priceMatrix);
                    ////// // System.out.println("priceMetrix = " + priceMatrix);

                } else {
                    //Payment  Scheme && Credit Company
                    priceMatrix = getPriceMatrixController().getPaymentSchemeDiscount(paymentMethod, paymentScheme, department, item);
                    getBillBean().setBillFees(bf, isForeigner(), paymentMethod, paymentScheme, getCreditCompany(), priceMatrix);
                }

                if (bf.getBillItem().getItem().isVatable()) {
                    if (!(bf.getFee().getFeeType() == FeeType.CollectingCentre && collectingCentreBillController.getCollectingCentre() != null)) {
                        bf.setFeeVat(bf.getFeeValue() * bf.getBillItem().getItem().getVatPercentage() / 100);
                        bf.setFeeVat(roundOff(bf.getFeeVat()));
                    }
                }
                bf.setFeeVatPlusValue(bf.getFeeValue() + bf.getFeeVat());

                entryGross += bf.getFeeGrossValue();
                entryNet += bf.getFeeValue();
                entryDis += bf.getFeeDiscount();
                entryVat += bf.getFeeVat();
                entryVatPlusNet += bf.getFeeVatPlusValue();

                //////// // System.out.println("fee net is " + bf.getFeeValue());
            }

            bi.setDiscount(entryDis);
            bi.setGrossValue(entryGross);
            bi.setNetValue(entryNet);
            bi.setVat(entryVat);
            bi.setVatPlusNetValue(roundOff(entryVatPlusNet));

            //// // System.out.println("item is = " + bi.getItem().getName());
            //// // System.out.println("item gross is = " + bi.getGrossValue());

            billGross += bi.getGrossValue();
            billNet += bi.getNetValue();
            billDiscount += bi.getDiscount();
            billVat += bi.getVat();
            //     billDis = billDis + entryDis;
        }
        setDiscount(billDiscount);
        setTotal(billGross);
        setNetTotal(billNet);
        setVat(billVat);
        setNetPlusVat(getVat() + getNetTotal());

        if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
            ////// // System.out.println("cashPaid = " + cashPaid);
            ////// // System.out.println("billNet = " + billNet);
            if (cashPaid >= (billNet + billVat)) {
                ////// // System.out.println("fully paid = ");
                setDiscount(billDiscount);
                setTotal(billGross);
                setNetTotal(billNet + billVat);
                setCashBalance(cashPaid - (billNet + billVat) - billDiscount);
                ////// // System.out.println("cashBalance = " + cashBalance);
            } else {
                ////// // System.out.println("half paid = ");
                setDiscount(billDiscount);
                setTotal(billGross);
                setNetTotal(cashPaid);
                setCashBalance((billNet + billVat) - cashPaid - billDiscount);
                ////// // System.out.println("cashBalance = " + cashBalance);
            }
            cashRemain = cashPaid;
        }

    }

    public void feeChanged() {
        lstBillItems = null;
        getLstBillItems();
        calTotals();
        //  feeChanged = false;

    }

    public void markAsForeigner() {
        setForeigner(true);
        calTotals();
    }

    public void markAsLocal() {
        setForeigner(false);
        calTotals();
    }

    long startIdForVat;
    BillType billTypeForVat;
    String txtBillNoForVat;

    public long getStartIdForVat() {
        return startIdForVat;
    }

    public void setStartIdForVat(long startIdForVat) {
        this.startIdForVat = startIdForVat;
    }

    public BillType getBillTypeForVat() {
        return billTypeForVat;
    }

    public void setBillTypeForVat(BillType billTypeForVat) {
        this.billTypeForVat = billTypeForVat;
    }

    public String getTxtBillNoForVat() {
        return txtBillNoForVat;
    }

    public void setTxtBillNoForVat(String txtBillNoForVat) {
        this.txtBillNoForVat = txtBillNoForVat;
    }

    public void addVatToOldBills() {
        String j = "select b from Bill b "
                + "where b.billType=:bt "
                + " and b.id > :id ";
        Map m = new HashMap();
        m.put("bt", billTypeForVat);
        m.put("id", startIdForVat);
        List<Bill> bs = getFacade().findBySQL(j, m, 1000);
        txtBillNoForVat = "";
        for (Bill b : bs) {
            if (b.getVatPlusNetTotal() == 0.00) {
                b.setVatPlusNetTotal(b.getNetTotal());
                getFacade().edit(b);
                startIdForVat = b.getId();
                txtBillNoForVat = txtBillNoForVat + "\n" + "Ind Id = " + b.getInsId();
            }
        }
    }

    public void prepareNewBill() {
        clearBillItemValues();
        clearBillValues();
        setPrintPreview(true);
        printPreview = false;
        paymentMethodData = null;
        paymentScheme = null;
        paymentMethod = PaymentMethod.Cash;
        collectingCentreBillController.setCollectingCentre(null);
    }

    public void prepareNewBillForMember() {
        clearBillItemValues();
        clearBillValuesForMember();
        setPrintPreview(true);
        printPreview = false;
        paymentMethodData = null;
        paymentScheme = null;
        paymentMethod = PaymentMethod.Cash;
        collectingCentreBillController.setCollectingCentre(null);
    }

    public void makeNull() {
        clearBillItemValues();
        clearBillValues();
        paymentMethod = null;
        printPreview = false;

    }

    public void removeBillItem() {

        //TODO: Need to add Logic
        //////// // System.out.println(getIndex());
        if (getIndex() != null) {
            //  boolean remove;
            BillEntry temp = getLstBillEntries().get(getIndex());
            //////// // System.out.println("Removed Item:" + temp.getBillItem().getNetValue());
            recreateList(temp);
            // remove = getLstBillEntries().remove(getIndex());

            //  getLstBillEntries().remove(index);
            ////////// // System.out.println("Is Removed:" + remove);
            calTotals();

        }

    }

    public void recreateList(BillEntry r) {
        List<BillEntry> temp = new ArrayList<>();
        for (BillEntry b : getLstBillEntries()) {
            if (b.getBillItem().getItem() != r.getBillItem().getItem()) {
                temp.add(b);
                //////// // System.out.println(b.getBillItem().getNetValue());
            }
        }
        lstBillEntries = temp;
        lstBillComponents = getBillBean().billComponentsFromBillEntries(lstBillEntries);
        lstBillFees = getBillBean().billFeesFromBillEntries(lstBillEntries);
    }

    public void onTabChange(TabChangeEvent event) {
        setPatientTabId(event.getTab().getId());
        if (!getPatientTabId().equals("tabSearchPt")) {
            if (fromOpdEncounter == false) {
                setSearchedPatient(null);
            }
        }
        calTotals();
    }

    public void createPaymentsForBills(Bill b, List<BillEntry> billEntrys) {
        Payment p = createPayment(b, b.getPaymentMethod());
        createBillFeePaymentsByPaymentsAndBillEntry(p, billEntrys);
    }

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

    double reminingCashPaid = 0.0;

    public void createBillFeePaymentsByPaymentsAndBillEntry(Payment p, List<BillEntry> billEntrys) {

        double dbl = 0;
        double pid = 0;
        reminingCashPaid = cashPaid;

        for (BillEntry be : billEntrys) {


            if ((reminingCashPaid != 0.0) || !getSessionController().getLoggedPreference().isPartialPaymentOfOpdPreBillsAllowed()) {

                calculateBillfeePayments(be.getLstBillFees(), p);

            }

        }

    }

    public void calculateBillfeePayments(List<BillFee> billFees, Payment p) {
        for (BillFee bf : billFees) {

            if (getSessionController().getLoggedPreference().isPartialPaymentOfOpdPreBillsAllowed() || getSessionController().getLoggedPreference().isPartialPaymentOfOpdBillsAllowed()) {
                if (Math.abs((bf.getFeeValue() - bf.getSettleValue())) > 0.1) {
                    if (reminingCashPaid >= (bf.getFeeValue() - bf.getSettleValue())) {
                        System.err.println("in");
                        //// // System.out.println("In If reminingCashPaid = " + reminingCashPaid);
                        //// // System.out.println("bf.getPaidValue() = " + bf.getSettleValue());
                        double d = (bf.getFeeValue() - bf.getSettleValue());
                        bf.setSettleValue(bf.getFeeValue());
                        setBillFeePaymentAndPayment(d, bf, p);
                        getBillFeeFacade().edit(bf);
                        reminingCashPaid -= d;
                    } else {
                        System.err.println("IN");
                        bf.setSettleValue(bf.getSettleValue() + reminingCashPaid);
                        setBillFeePaymentAndPayment(reminingCashPaid, bf, p);
                        getBillFeeFacade().edit(bf);
                        reminingCashPaid = 0.0;
                    }
                }
            } else {
                bf.setSettleValue(bf.getFeeValue());
                setBillFeePaymentAndPayment(bf.getFeeValue(), bf, p);
                getBillFeeFacade().edit(bf);
            }
        }
    }

    public void setBillFeePaymentAndPayment(double amount, BillFee bf, Payment p) {
        BillFeePayment bfp = new BillFeePayment();
        bfp.setBillFee(bf);
        bfp.setAmount(amount);
        bfp.setInstitution(bf.getBillItem().getItem().getInstitution());
        bfp.setDepartment(bf.getBillItem().getItem().getDepartment());
        bfp.setCreater(getSessionController().getLoggedUser());
        bfp.setCreatedAt(new Date());
        bfp.setPayment(p);
        getBillFeePaymentFacade().create(bfp);
    }

    public double calBillPaidValue(Bill b) {
        String sql;

        sql = "select sum(bfp.amount) from BillFeePayment bfp where "
                + " bfp.retired=false "
                + " and bfp.billFee.bill.id=" + b.getId();

        double d = getBillFeePaymentFacade().findDoubleByJpql(sql);

        return d;
    }

    private double roundOff(double d) {
        DecimalFormat newFormat = new DecimalFormat("#.##");
        try {
            return Double.valueOf(newFormat.format(d));
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateReferingDoctor() {
        if (bill.getReferredBy() == null) {
            JsfUtil.addErrorMessage("Empty Doctor");
            return;
        }
        billFacade.edit(bill);
        JsfUtil.addSuccessMessage("Ref Doctor Updated");
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

    public BillController() {
    }

    private BillFacade getFacade() {
        return billFacade;
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public PaymentScheme getPaymentScheme() {
//        if (paymentScheme == null) {
//            paymentScheme = new PaymentScheme();
//        }
        return paymentScheme;
    }

    public void setPaymentScheme(PaymentScheme paymentScheme) {
        this.paymentScheme = paymentScheme;
        //    getEnumController().setPaymentScheme(paymentScheme);

    }

    public void changeListener() {
        calTotals();
    }

    public String getPatientTabId() {
        return patientTabId;
    }

    public void setPatientTabId(String patientTabId) {
        this.patientTabId = patientTabId;
    }

    public Patient getNewPatient() {
        if (newPatient == null) {
            newPatient = new Patient();
            Person p = new Person();

            newPatient.setPerson(p);
        }
        return newPatient;
    }

    public void setNewPatient(Patient newPatient) {
        this.newPatient = newPatient;
    }

    public Patient getSearchedPatient() {
        return searchedPatient;
    }

    public void setSearchedPatient(Patient searchedPatient) {
        this.searchedPatient = searchedPatient;
    }

    public Doctor getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(Doctor referredBy) {
        this.referredBy = referredBy;
    }

    public Institution getCreditCompany() {
        return creditCompany;
    }

    public void setCreditCompany(Institution creditCompany) {
        this.creditCompany = creditCompany;

    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public List<BillComponent> getLstBillComponents() {
        if (lstBillComponents == null) {
            lstBillComponents = getBillBean().billComponentsFromBillEntries(getLstBillEntries());
        }

        return lstBillComponents;
    }

    public void setLstBillComponents(List<BillComponent> lstBillComponents) {
        this.lstBillComponents = lstBillComponents;
    }

    public List<BillFee> getLstBillFees() {
        if (lstBillFees == null) {
            lstBillFees = getBillBean().billFeesFromBillEntries(getLstBillEntries());
        }

        return lstBillFees;
    }

    public void setLstBillFees(List<BillFee> lstBillFees) {
        this.lstBillFees = lstBillFees;
    }

    public List<BillItem> getLstBillItems() {
        if (lstBillItems == null) {
            lstBillItems = new ArrayList<>();
        }
        return lstBillItems;
    }

    public void setLstBillItems(List<BillItem> lstBillItems) {
        this.lstBillItems = lstBillItems;
    }

    public List<BillEntry> getLstBillEntries() {
        if (lstBillEntries == null) {
            lstBillEntries = new ArrayList<>();
        }
        return lstBillEntries;
    }

    public void setLstBillEntries(List<BillEntry> lstBillEntries) {
        this.lstBillEntries = lstBillEntries;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public double getCashPaid() {
        return cashPaid;
    }

    public void setCashPaid(double cashPaid) {
        this.cashPaid = cashPaid;
//        cashBalance = cashPaid - getNetTotal();
    }

    public double getCashBalance() {
        cashBalance = cashPaid - (netTotal + vat);
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public BillItem getCurrentBillItem() {
        if (currentBillItem == null) {
            currentBillItem = new BillItem();
            currentBillItem.setQty(1.0);
        }

        return currentBillItem;
    }
    BillItem lastBillItem;

    public BillItem getLastBillItem() {
        if (lastBillItem == null) {
            if (getCurrentBillItem() != null) {
                lastBillItem = getCurrentBillItem();
            }
        }
        return lastBillItem;
    }

    public void setLastBillItem(BillItem lastBillItem) {
        this.lastBillItem = lastBillItem;
    }

    public void setCurrentBillItem(BillItem currentBillItem) {
        this.currentBillItem = currentBillItem;
        lastBillItem = currentBillItem;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public BillBeanController getBillBean() {
        return billBean;
    }

    public void setBillBean(BillBeanController billBean) {
        this.billBean = billBean;

    }

    public PersonFacade getPersonFacade() {
        return personFacade;
    }

    public void setPersonFacade(PersonFacade personFacade) {
        this.personFacade = personFacade;
    }

    public PatientFacade getPatientFacade() {
        return patientFacade;
    }

    public void setPatientFacade(PatientFacade patientFacade) {
        this.patientFacade = patientFacade;
    }

    public BillComponentFacade getBillComponentFacade() {
        return billComponentFacade;
    }

    public void setBillComponentFacade(BillComponentFacade billComponentFacade) {
        this.billComponentFacade = billComponentFacade;
    }

    public BillFeeFacade getBillFeeFacade() {
        return billFeeFacade;
    }

    public void setBillFeeFacade(BillFeeFacade billFeeFacade) {
        this.billFeeFacade = billFeeFacade;
    }

    private Patient getTmpPatient() {
        return tmpPatient;
    }

    public void setTmpPatient(Patient tmpPatient) {
        this.tmpPatient = tmpPatient;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;

    }

    public BatchBillFacade getBatchBillFacade() {
        return batchBillFacade;
    }

    public void setBatchBillFacade(BatchBillFacade batchBillFacade) {
        this.batchBillFacade = batchBillFacade;
    }

    public BillSearch getBillSearch() {
        return billSearch;
    }

    public void setBillSearch(BillSearch billSearch) {
        this.billSearch = billSearch;
    }

    public YearMonthDay getYearMonthDay() {
        if (yearMonthDay == null) {
            yearMonthDay = new YearMonthDay();
        }
        return yearMonthDay;
    }

    public void setYearMonthDay(YearMonthDay yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }

    public EnumController getEnumController() {
        return enumController;
    }

    public void setEnumController(EnumController enumController) {
        this.enumController = enumController;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public List<Bill> completeAppointmentBill(String query) {
        List<Bill> suggestions;
        String sql;
        HashMap hm = new HashMap();

        sql = "select p from BilledBill p where p.retired=false and "
                + "p.cancelled=false and p.refunded=false and p.billType=:btp "
                + " and (upper(p.patient.person.name)  "
                + "like :q or upper(p.insId)  "
                + "like :q) order by p.insId";
        //////// // System.out.println(sql);
        hm.put("q", "%" + query.toUpperCase() + "%");
        hm.put("btp", BillType.InwardAppointmentBill);
        suggestions = getFacade().findBySQL(sql, hm);

        return suggestions;

    }

    public PatientEncounterFacade getPatientEncounterFacade() {
        return patientEncounterFacade;
    }

    public void setPatientEncounterFacade(PatientEncounterFacade patientEncounterFacade) {
        this.patientEncounterFacade = patientEncounterFacade;
    }

    public CashTransactionBean getCashTransactionBean() {
        return cashTransactionBean;
    }

    public void setCashTransactionBean(CashTransactionBean cashTransactionBean) {
        this.cashTransactionBean = cashTransactionBean;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isFromOpdEncounter() {
        return fromOpdEncounter;
    }

    public void setFromOpdEncounter(boolean fromOpdEncounter) {
        this.fromOpdEncounter = fromOpdEncounter;
    }

    public String getOpdEncounterComments() {
        return opdEncounterComments;
    }

    public void setOpdEncounterComments(String opdEncounterComments) {
        this.opdEncounterComments = opdEncounterComments;
    }

    public int getPatientSearchTab() {
        return patientSearchTab;
    }

    public void setPatientSearchTab(int patientSearchTab) {
        this.patientSearchTab = patientSearchTab;
    }

    public Bill getBill() {
        if (bill == null) {
            bill = new Bill();
            Bill b = new Bill();

            bill.setBilledBill(b);
        }
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Staff getToStaff() {
        return toStaff;
    }

    public void setToStaff(Staff toStaff) {
        this.toStaff = toStaff;
    }

    public Bill getBillPrint() {
        return billPrint;
    }

    public void setBillPrint(Bill billPrint) {
        this.billPrint = billPrint;
    }

    public List<BillComponent> getLstBillComponentsPrint() {
        return lstBillComponentsPrint;
    }

    public void setLstBillComponentsPrint(List<BillComponent> lstBillComponentsPrint) {
        this.lstBillComponentsPrint = lstBillComponentsPrint;
    }

    public List<BillFee> getLstBillFeesPrint() {
        return lstBillFeesPrint;
    }

    public void setLstBillFeesPrint(List<BillFee> lstBillFeesPrint) {
        this.lstBillFeesPrint = lstBillFeesPrint;
    }

    public List<BillItem> getLstBillItemsPrint() {
        return lstBillItemsPrint;
    }

    public void setLstBillItemsPrint(List<BillItem> lstBillItemsPrint) {
        this.lstBillItemsPrint = lstBillItemsPrint;
    }

    public List<BillEntry> getLstBillEntriesPrint() {
        return lstBillEntriesPrint;
    }

    public void setLstBillEntriesPrint(List<BillEntry> lstBillEntriesPrint) {
        this.lstBillEntriesPrint = lstBillEntriesPrint;
    }

    public int getRecurseCount() {
        return recurseCount;
    }

    public void setRecurseCount(int recurseCount) {
        this.recurseCount = recurseCount;
    }

    public List<Bill> getBillsPrint() {
        return billsPrint;
    }

    public void setBillsPrint(List<Bill> billsPrint) {
        this.billsPrint = billsPrint;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public double getOpdPaymentCredit() {
        return opdPaymentCredit;
    }

    public void setOpdPaymentCredit(double opdPaymentCredit) {
        this.opdPaymentCredit = opdPaymentCredit;
    }

    public BilledBill getOpdBill() {
        return opdBill;
    }

    public void setOpdBill(BilledBill opdBill) {
        this.opdBill = opdBill;
    }

    public PaymentFacade getPaymentFacade() {
        return PaymentFacade;
    }

    public void setPaymentFacade(PaymentFacade PaymentFacade) {
        this.PaymentFacade = PaymentFacade;
    }

    public BillFeePaymentFacade getBillFeePaymentFacade() {
        return billFeePaymentFacade;
    }

    public void setBillFeePaymentFacade(BillFeePaymentFacade billFeePaymentFacade) {
        this.billFeePaymentFacade = billFeePaymentFacade;
    }

    public Institution getCollectingCentre() {
        return collectingCentre;
    }

    public void setCollectingCentre(Institution collectingCentre) {
        this.collectingCentre = collectingCentre;
    }

    /**
     *
     */
    @FacesConverter("bill")
    public static class BillControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BillController controller = (BillController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "billController");
            return controller.getBillFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Bill) {
                Bill o = (Bill) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + BillController.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Bill.class)
    public static class BillConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BillController controller = (BillController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "billController");
            return controller.getBillFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Bill) {
                Bill o = (Bill) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + BillController.class.getName());
            }
        }
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public double getNetPlusVat() {
        return netPlusVat;
    }

    public void setNetPlusVat(double netPlusVat) {
        this.netPlusVat = netPlusVat;
    }

}