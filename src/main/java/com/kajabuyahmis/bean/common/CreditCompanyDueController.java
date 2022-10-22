/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.dataStructure.DealerDueDetailRow;
import com.kajabuyahmis.data.dataStructure.InstitutionBills;
import com.kajabuyahmis.data.dataStructure.InstitutionEncounters;
import com.kajabuyahmis.data.table.String1Value5;
import com.kajabuyahmis.ejb.CommonFunctions;
import com.kajabuyahmis.ejb.CreditBean;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.PatientEncounter;
import com.kajabuyahmis.entity.inward.Admission;
import com.kajabuyahmis.entity.inward.AdmissionType;
import com.kajabuyahmis.facade.AdmissionFacade;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.InstitutionFacade;
import com.kajabuyahmis.facade.PatientEncounterFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class CreditCompanyDueController implements Serializable {

    private Date fromDate;
    private Date toDate;
    Admission patientEncounter;
    boolean withOutDueUpdate;
    Institution creditCompany;

    ////////////
    private List<InstitutionBills> items;
    private List<InstitutionEncounters> institutionEncounters;
    List<PatientEncounter> patientEncounters;
    private List<String1Value5> creditCompanyAge;
    private List<String1Value5> filteredList;
    @EJB
    private InstitutionFacade institutionFacade;
    @EJB
    private BillFacade billFacade;
    @EJB
    private CommonFunctions commonFunctions;
    @EJB
    AdmissionFacade admissionFacade;
    @Inject
    CommonController commonController;

    double finalTotal;
    double finalPaidTotal;
    double finalPaidTotalPatient;
    double finalTransPaidTotal;
    double finalTransPaidTotalPatient;

    public List<PatientEncounter> getPatientEncounters() {
        return patientEncounters;
    }

    public void setPatientEncounters(List<PatientEncounter> patientEncounters) {
        this.patientEncounters = patientEncounters;
    }

    public void makeNull() {
        fromDate = null;
        toDate = null;
        items = null;
        institutionEncounters = null;
        creditCompanyAge = null;
        filteredList = null;
    }

    public void createAgeTable() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBills(true);

        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setValues(ins, newRow);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Payments/Receieve/Credit Company/Due age(/faces/credit/credit_company_opd_due_age.xhtml)");
    }

    public void createAgeTablePharmacy() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBillsPharmacy(true);

        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setValuesPharmacy(ins, newRow);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Payments/Receieve/Credit Company/Due age(/faces/credit/credit_company_opd_due_age.xhtml)");
    }

    public void createAgeAccessTable() {
        Date startTime = new Date();

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBills(false);

        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setValuesAccess(ins, newRow);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/OPD Dues and Access/OPD credit excess/Excess age(/faces/credit/credit_company_opd_access_age.xhtml)");

    }

    public void createInwardAgeTable() {
        Date startTime = new Date();

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBht(true, PaymentMethod.Credit);
        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setInstitution(ins);
            setInwardValues(ins, newRow, PaymentMethod.Credit);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/Dues/Due Age(/faces/credit/inward_due_age.xhtml)");

    }

    List<DealerDueDetailRow> dealerDueDetailRows;

    public List<DealerDueDetailRow> getDealerDueDetailRows() {
        return dealerDueDetailRows;
    }

    public void setDealerDueDetailRows(List<DealerDueDetailRow> dealerDueDetailRows) {
        this.dealerDueDetailRows = dealerDueDetailRows;
    }

    public void createInwardAgeDetailAnalysis() {
        Date startTime = new Date();

        dealerDueDetailRows = new ArrayList<>();
        createInwardAgeTable();
        Institution dealer = null;
        for (String1Value5 s : creditCompanyAge) {
            DealerDueDetailRow row = new DealerDueDetailRow();
            if (dealer == null || dealer != s.getInstitution()) {
                dealer = s.getInstitution();
                row.setDealer(dealer);
                row.setZeroToThirty(s.getValue1());
                row.setThirtyToSixty(s.getValue2());
                row.setSixtyToNinty(s.getValue3());
                row.setMoreThanNinty(s.getValue4());
                dealerDueDetailRows.add(row);
            }

            int rowsForDealer = 0;
            if (rowsForDealer < s.getValue1PatientEncounters().size()) {
                rowsForDealer = s.getValue1PatientEncounters().size();
            }
            if (rowsForDealer < s.getValue2PatientEncounters().size()) {
                rowsForDealer = s.getValue2PatientEncounters().size();
            }
            if (rowsForDealer < s.getValue3PatientEncounters().size()) {
                rowsForDealer = s.getValue3PatientEncounters().size();
            }
            if (rowsForDealer < s.getValue4PatientEncounters().size()) {
                rowsForDealer = s.getValue4PatientEncounters().size();
            }

            for (int i = 0; i < rowsForDealer; i++) {
                DealerDueDetailRow rowi = new DealerDueDetailRow();
                if (s.getValue1PatientEncounters().size() > i) {
                    rowi.setZeroToThirtyEncounter(s.getValue1PatientEncounters().get(i));
                }
                if (s.getValue2PatientEncounters().size() > i) {
                    rowi.setThirtyToSixtyEncounter(s.getValue2PatientEncounters().get(i));
                }
                if (s.getValue3PatientEncounters().size() > i) {
                    rowi.setSixtyToNintyEncounter(s.getValue3PatientEncounters().get(i));
                }
                if (s.getValue4PatientEncounters().size() > i) {
                    rowi.setMoreThanNintyEncounter(s.getValue4PatientEncounters().get(i));
                }
                dealerDueDetailRows.add(rowi);
            }

        }

        creditCompanyAge = new ArrayList<>();

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/Dues/Due age detail(/faces/credit/inward_due_age_credit_company_detail.xhtml)");
    }

    public void createInwardCashAgeTable() {
        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBht(true, PaymentMethod.Cash);
        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setInwardValues(ins, newRow, PaymentMethod.Cash);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

    }

    public void createInwardAgeTableAccess() {
        Date startTime = new Date();

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBht(false, PaymentMethod.Credit);

        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setInwardValuesAccess(ins, newRow, PaymentMethod.Credit);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/excess/excess age (/faces/credit/credit_company_inward_access_age.xhtml)");
    }

    public void createInwardCashAgeTableAccess() {
        Date startTime = new Date();

        makeNull();
        Set<Institution> setIns = new HashSet<>();

        List<Institution> list = getCreditBean().getCreditCompanyFromBht(false, PaymentMethod.Cash);

        setIns.addAll(list);

        creditCompanyAge = new ArrayList<>();
        for (Institution ins : setIns) {
            if (ins == null) {
                continue;
            }

            String1Value5 newRow = new String1Value5();
            newRow.setString(ins.getName());
            setInwardValuesAccess(ins, newRow, PaymentMethod.Cash);

            if (newRow.getValue1() != 0
                    || newRow.getValue2() != 0
                    || newRow.getValue3() != 0
                    || newRow.getValue4() != 0) {
                creditCompanyAge.add(newRow);
            }
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/excess/excess age (/faces/credit/cash_inward_access_age.xhtml)");

    }

    private void setValues(Institution inst, String1Value5 dataTable5Value) {

        List<Bill> lst = getCreditBean().getCreditBills(inst, true);
        for (Bill b : lst) {

            Long dayCount = getCommonFunctions().getDayCountTillNow(b.getCreatedAt());

            double finalValue = (b.getNetTotal() + b.getPaidAmount());


            if (dayCount < 30) {
                dataTable5Value.setValue1(dataTable5Value.getValue1() + finalValue);
            } else if (dayCount < 60) {
                dataTable5Value.setValue2(dataTable5Value.getValue2() + finalValue);
            } else if (dayCount < 90) {
                dataTable5Value.setValue3(dataTable5Value.getValue3() + finalValue);
            } else {
                dataTable5Value.setValue4(dataTable5Value.getValue4() + finalValue);
            }

        }

    }

    private void setValuesPharmacy(Institution inst, String1Value5 dataTable5Value) {

        List<Bill> lst = getCreditBean().getCreditBillsPharmacy(inst, true);
        for (Bill b : lst) {

            Long dayCount = getCommonFunctions().getDayCountTillNow(b.getCreatedAt());

            double finalValue = (b.getNetTotal() + b.getPaidAmount());


            if (dayCount < 30) {
                dataTable5Value.setValue1(dataTable5Value.getValue1() + finalValue);
            } else if (dayCount < 60) {
                dataTable5Value.setValue2(dataTable5Value.getValue2() + finalValue);
            } else if (dayCount < 90) {
                dataTable5Value.setValue3(dataTable5Value.getValue3() + finalValue);
            } else {
                dataTable5Value.setValue4(dataTable5Value.getValue4() + finalValue);
            }

        }

    }

    private void setValuesAccess(Institution inst, String1Value5 dataTable5Value) {

        List<Bill> lst = getCreditBean().getCreditBills(inst, false);
        for (Bill b : lst) {

            Long dayCount = getCommonFunctions().getDayCountTillNow(b.getCreatedAt());

            double finalValue = (b.getNetTotal() + b.getPaidAmount());


            if (dayCount < 30) {
                dataTable5Value.setValue1(dataTable5Value.getValue1() + finalValue);
            } else if (dayCount < 60) {
                dataTable5Value.setValue2(dataTable5Value.getValue2() + finalValue);
            } else if (dayCount < 90) {
                dataTable5Value.setValue3(dataTable5Value.getValue3() + finalValue);
            } else {
                dataTable5Value.setValue4(dataTable5Value.getValue4() + finalValue);
            }

        }

    }

    private void setInwardValues(Institution inst, String1Value5 dataTable5Value, PaymentMethod paymentMethod) {
        List<PatientEncounter> lst = getCreditBean().getCreditPatientEncounters(inst, true, paymentMethod);
        for (PatientEncounter b : lst) {
            Long dayCount = getCommonFunctions().getDayCountTillNow(b.getCreatedAt());
            b.setTransDayCount(dayCount);
            double finalValue = b.getFinalBill().getNetTotal() - (Math.abs(b.getFinalBill().getPaidAmount()) + Math.abs(b.getCreditPaidAmount()));
            if (dayCount < 30) {
                dataTable5Value.setValue1(dataTable5Value.getValue1() + finalValue);
                dataTable5Value.getValue1PatientEncounters().add(b);
            } else if (dayCount < 60) {
                dataTable5Value.setValue2(dataTable5Value.getValue2() + finalValue);
                dataTable5Value.getValue2PatientEncounters().add(b);
            } else if (dayCount < 90) {
                dataTable5Value.setValue3(dataTable5Value.getValue3() + finalValue);
                dataTable5Value.getValue3PatientEncounters().add(b);
            } else {
                dataTable5Value.setValue4(dataTable5Value.getValue4() + finalValue);
                dataTable5Value.getValue4PatientEncounters().add(b);
            }

        }

    }

    private void setInwardValuesAccess(Institution inst, String1Value5 dataTable5Value, PaymentMethod paymentMethod) {

        List<PatientEncounter> lst = getCreditBean().getCreditPatientEncounters(inst, false, paymentMethod);
        for (PatientEncounter b : lst) {

            Long dayCount = getCommonFunctions().getDayCountTillNow(b.getCreatedAt());

            double finalValue = (Math.abs(b.getCreditUsedAmount()) - Math.abs(b.getCreditPaidAmount()));

            if (dayCount < 30) {
                dataTable5Value.setValue1(dataTable5Value.getValue1() + finalValue);
            } else if (dayCount < 60) {
                dataTable5Value.setValue2(dataTable5Value.getValue2() + finalValue);
            } else if (dayCount < 90) {
                dataTable5Value.setValue3(dataTable5Value.getValue3() + finalValue);
            } else {
                dataTable5Value.setValue4(dataTable5Value.getValue4() + finalValue);
            }

        }

    }

    public CreditCompanyDueController() {
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = com.kajabuyahmis.java.CommonFunctions.getStartOfMonth(new Date());
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = getCommonFunctions().getEndOfDay(new Date());
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @EJB
    private CreditBean creditBean;

    public void createOpdCreditDue() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitution(BillType.OpdBill, getFromDate(), getToDate(), true);
        items = new ArrayList<>();
        for (Institution ins : setIns) {
            List<Bill> bills = getCreditBean().getCreditBills(ins, BillType.OpdBill, getFromDate(), getToDate(), true);
            InstitutionBills newIns = new InstitutionBills();
            newIns.setInstitution(ins);
            newIns.setBills(bills);

            for (Bill b : bills) {
                newIns.setTotal(newIns.getTotal() + b.getNetTotal());
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getPaidAmount());
            }

            items.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Payments/Receieve/Credit Company/OPD/Due search(/faces/credit/credit_company_opd_due.xhtml)");

    }

    public void createPharmacyCreditDue() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitutionPharmacy(Arrays.asList(new BillType[]{BillType.PharmacyWholeSale, BillType.PharmacySale}), getFromDate(), getToDate(), true);
        items = new ArrayList<>();
        for (Institution ins : setIns) {
            List<Bill> bills = getCreditBean().getCreditBillsPharmacy(ins, Arrays.asList(new BillType[]{BillType.PharmacyWholeSale, BillType.PharmacySale}), getFromDate(), getToDate(), true);
            InstitutionBills newIns = new InstitutionBills();
            newIns.setInstitution(ins);
            newIns.setBills(bills);

            for (Bill b : bills) {
                newIns.setTotal(newIns.getTotal() + b.getNetTotal());
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getPaidAmount());
            }

            items.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Payments/Receieve/Credit Company/OPD/Due search(/faces/credit/credit_company_opd_due.xhtml)");

    }

    public void createOpdCreditDueBillItem() {
        Date startTime = new Date();

        List<Institution> setIns = new ArrayList<>();
        if (creditCompany != null) {
            setIns.add(creditCompany);
        } else {
            setIns.addAll(getCreditBean().getCreditInstitution(BillType.OpdBill, getFromDate(), getToDate(), true));
        }
        items = new ArrayList<>();
        for (Institution ins : setIns) {
            List<BillItem> billItems = getCreditBean().getCreditBillItems(ins, BillType.OpdBill, getFromDate(), getToDate(), true);
            InstitutionBills newIns = new InstitutionBills();
            newIns.setInstitution(ins);
            newIns.setBillItems(billItems);

            for (BillItem bi : billItems) {
                newIns.setTotal(newIns.getTotal() + bi.getNetValue());
            }

            items.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/OPD Dues and Access/OPD Dues and Access/Due Search(Bill item)(/faces/credit/credit_company_opd_due_by_bill_item.xhtml)");

    }

    public void createOpdCreditAccess() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitution(BillType.OpdBill, getFromDate(), getToDate(), false);
        items = new ArrayList<>();
        for (Institution ins : setIns) {
            List<Bill> bills = getCreditBean().getCreditBills(ins, BillType.OpdBill, getFromDate(), getToDate(), false);
            InstitutionBills newIns = new InstitutionBills();
            newIns.setInstitution(ins);
            newIns.setBills(bills);

            for (Bill b : bills) {
                newIns.setTotal(newIns.getTotal() + b.getNetTotal());
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getPaidAmount());
            }

            items.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/OPD Dues and Access/OPD credit excess/Excess search(/faces/credit/credit_company_opd_access.xhtml)");
    }

    public void createInwardCreditDue() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitutionByPatientEncounter(getFromDate(), getToDate(), PaymentMethod.Credit, true);
        institutionEncounters = new ArrayList<>();
        finalTotal = 0.0;
        finalPaidTotal = 0.0;
        finalPaidTotalPatient = 0.0;
        finalTransPaidTotal = 0.0;
        finalTransPaidTotalPatient = 0.0;
        for (Institution ins : setIns) {
            List<PatientEncounter> lst = getCreditBean().getCreditPatientEncounter(ins, getFromDate(), getToDate(), PaymentMethod.Credit, true);

            InstitutionEncounters newIns = new InstitutionEncounters();
            newIns.setInstitution(ins);
            newIns.setPatientEncounters(lst);
            for (PatientEncounter b : lst) {
                b.setTransPaidByPatient(createInwardPaymentTotal(b, getFromDate(), getToDate(), BillType.InwardPaymentBill));
                b.setTransPaidByCompany(createInwardPaymentTotalCredit(b, getFromDate(), getToDate(), BillType.CashRecieveBill));
                newIns.setTotal(newIns.getTotal() + b.getFinalBill().getNetTotal());
                newIns.setPaidTotalPatient(newIns.getPaidTotalPatient() + b.getFinalBill().getPaidAmount());
                newIns.setTransPaidTotalPatient(newIns.getTransPaidTotalPatient() + b.getTransPaidByPatient());
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getPaidByCreditCompany());
                newIns.setTransPaidTotal(newIns.getTransPaidTotal() + b.getTransPaidByCompany());
            }
            finalTotal += newIns.getTotal();
            finalPaidTotal += newIns.getPaidTotal();
            finalPaidTotalPatient += newIns.getPaidTotalPatient();
            finalTransPaidTotal += newIns.getTransPaidTotal();
            finalTransPaidTotalPatient += newIns.getTransPaidTotalPatient();

            institutionEncounters.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/Inward Dues And Access/Due search(Credit company)(/faces/credit/inward_due_search_credit_company.xhtml)");

    }

    public double createInwardPaymentTotal(PatientEncounter pe, Date fd, Date td, BillType bt) {

        String sql;
        Map m = new HashMap();
        sql = "select sum(b.netTotal) from BilledBill b where "
                + " b.billType=:billType "
                + " and b.createdAt between :fromDate and :toDate "
                + " and b.retired=false "
                + " and b.refunded=false "
                + " and b.cancelled=false "
                + " and b.patientEncounter=:pe ";

        m.put("pe", pe);
        m.put("billType", bt);
        m.put("toDate", td);
        m.put("fromDate", fd);

        return getBillFacade().findDoubleByJpql(sql, m, TemporalType.TIMESTAMP);

    }

    public double createInwardPaymentTotalCredit(PatientEncounter pe, Date fd, Date td, BillType bt) {

        String sql;
        Map m = new HashMap();
        sql = "select sum(bi.netValue) from BillItem bi where "
                + " bi.bill.billType=:billType "
                + " and type(bi.bill)=:cl "
                + " and bi.bill.createdAt between :fromDate and :toDate "
                + " and bi.bill.retired=false "
                + " and bi.bill.refunded=false "
                + " and bi.bill.cancelled=false "
                + " and bi.patientEncounter=:pe ";

        m.put("pe", pe);
        m.put("billType", bt);
        m.put("toDate", td);
        m.put("fromDate", fd);
        m.put("cl", BilledBill.class);
//        //// // System.out.println("sql = " + sql);
        return getBillFacade().findDoubleByJpql(sql, m, TemporalType.TIMESTAMP);

    }

    AdmissionType admissionType;
    PaymentMethod paymentMethod;
    @EJB
    PatientEncounterFacade patientEncounterFacade;

    public AdmissionType getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(AdmissionType admissionType) {
        this.admissionType = admissionType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PatientEncounterFacade getPatientEncounterFacade() {
        return patientEncounterFacade;
    }

    public void setPatientEncounterFacade(PatientEncounterFacade patientEncounterFacade) {
        this.patientEncounterFacade = patientEncounterFacade;
    }

    public void createInwardCashDue() {
        Date startTime = new Date();

        HashMap m = new HashMap();
        String sql = " Select b from PatientEncounter b"
                + " where b.retired=false "
                + " and b.paymentFinalized=true "
                + " and b.dateOfDischarge between :fd and :td "
                + " and (abs(b.finalBill.netTotal)-(abs(b.finalBill.paidAmount)+abs(b.creditPaidAmount))) > 0.1 ";

        if (admissionType != null) {
            sql += " and b.admissionType =:ad ";
            m.put("ad", admissionType);
        }
        if (institution != null) {
            sql += " and b.creditCompany =:ins ";
            m.put("ins", institution);
        }

        if (paymentMethod != null) {
            sql += " and b.paymentMethod =:pm ";
            m.put("pm", paymentMethod);
        }

        sql += " order by  b.dateOfDischarge";

        m.put("fd", fromDate);
        m.put("td", toDate);
        patientEncounters = patientEncounterFacade.findBySQL(sql, m, TemporalType.TIMESTAMP);

        if (patientEncounters == null) {
            return;
        }
        billed = 0;
        paidByPatient = 0;
        paidByCompany = 0;
        for (PatientEncounter p : patientEncounters) {
            billed += p.getFinalBill().getNetTotal();
            paidByPatient += p.getFinalBill().getPaidAmount();
            paidByCompany += p.getPaidByCreditCompany();

        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/Dues/Due search(/faces/credit/inward_due_search.xhtml)");
    }

    double billed;
    double paidByPatient;
    double paidByCompany;

    public double getBilled() {
        return billed;
    }

    public void setBilled(double billed) {
        this.billed = billed;
    }

    public double getPaidByPatient() {
        return paidByPatient;
    }

    public void setPaidByPatient(double paidByPatient) {
        this.paidByPatient = paidByPatient;
    }

    public double getPaidByCompany() {
        return paidByCompany;
    }

    public void setPaidByCompany(double paidByCompany) {
        this.paidByCompany = paidByCompany;
    }

    public void createInwardCreditAccess() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitutionByPatientEncounter(getFromDate(), getToDate(), PaymentMethod.Credit, false);

        institutionEncounters = new ArrayList<>();
        for (Institution ins : setIns) {
            List<PatientEncounter> lst = getCreditBean().getCreditPatientEncounter(ins, getFromDate(), getToDate(), PaymentMethod.Credit, false);
            InstitutionEncounters newIns = new InstitutionEncounters();
            newIns.setInstitution(ins);
            newIns.setPatientEncounters(lst);

            for (PatientEncounter b : lst) {
//                newIns.setTotal(newIns.getTotal() + b.getCreditUsedAmount());
//                newIns.setPaidTotal(newIns.getPaidTotal() + b.getCreditPaidAmount());
                b.getFinalBill().setNetTotal(com.kajabuyahmis.java.CommonFunctions.round(b.getFinalBill().getNetTotal()));
                b.setCreditPaidAmount(Math.abs(b.getCreditPaidAmount()));
                b.setCreditPaidAmount(com.kajabuyahmis.java.CommonFunctions.round(b.getCreditPaidAmount()));
                b.getFinalBill().setPaidAmount(com.kajabuyahmis.java.CommonFunctions.round(b.getFinalBill().getPaidAmount()));
                b.setTransPaid(b.getFinalBill().getPaidAmount()+b.getCreditPaidAmount());
                //// // System.out.println("b.getTransPaid() = " + b.getTransPaid());
                b.setTransPaid(com.kajabuyahmis.java.CommonFunctions.round(b.getTransPaid()));
                
                
                newIns.setTotal(newIns.getTotal() + b.getFinalBill().getNetTotal());
//                newIns.setPaidTotal(newIns.getPaidTotal() + (Math.abs(b.getCreditPaidAmount()) + Math.abs(b.getFinalBill().getPaidAmount())));
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getTransPaid());

            }
            newIns.setTotal(com.kajabuyahmis.java.CommonFunctions.round(newIns.getTotal()));
            newIns.setPaidTotal(com.kajabuyahmis.java.CommonFunctions.round(newIns.getPaidTotal()));
            institutionEncounters.add(newIns);
        }
        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/excess/excess Search(/faces/credit/credit_company_inward_access.xhtml)");

    }

    public void createInwardCashAccess() {
        Date startTime = new Date();

        List<Institution> setIns = getCreditBean().getCreditInstitutionByPatientEncounter(getFromDate(), getToDate(), PaymentMethod.Cash, false);

        institutionEncounters = new ArrayList<>();
        for (Institution ins : setIns) {
            List<PatientEncounter> lst = getCreditBean().getCreditPatientEncounter(ins, getFromDate(), getToDate(), PaymentMethod.Cash, false);
            InstitutionEncounters newIns = new InstitutionEncounters();
            newIns.setInstitution(ins);
            newIns.setPatientEncounters(lst);

            for (PatientEncounter b : lst) {
                newIns.setTotal(newIns.getTotal() + b.getCreditUsedAmount());
                newIns.setPaidTotal(newIns.getPaidTotal() + b.getCreditPaidAmount());
            }

            institutionEncounters.add(newIns);
        }

        commonController.printReportDetails(fromDate, toDate, startTime, "Reports/Inward Dues And Access/excess/excess Search(/faces/credit/cash_inward_access.xhtml)");
    }

    public List<InstitutionBills> getItems() {
        return items;
    }

    private Institution institution;

    public List<Bill> getItems2() {
        String sql;
        HashMap hm;

        sql = "Select b From Bill b where b.retired=false and b.createdAt "
                + "  between :frm and :to and b.creditCompany=:cc "
                + " and b.paymentMethod= :pm and b.billType=:tp";
        hm = new HashMap();
        hm.put("frm", getFromDate());
        hm.put("to", getToDate());
        hm.put("cc", getInstitution());
        hm.put("pm", PaymentMethod.Credit);
        hm.put("tp", BillType.OpdBill);
        return getBillFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);

    }

    public double getCreditTotal() {
        String sql;
        HashMap hm;

        sql = "Select sum(b.netTotal) From Bill b where b.retired=false and b.createdAt "
                + "  between :frm and :to and b.creditCompany=:cc "
                + " and b.paymentMethod= :pm and b.billType=:tp";
        hm = new HashMap();
        hm.put("frm", getFromDate());
        hm.put("to", getToDate());
        hm.put("cc", getInstitution());
        hm.put("pm", PaymentMethod.Credit);
        hm.put("tp", BillType.OpdBill);
        return getBillFacade().findDoubleByJpql(sql, hm, TemporalType.TIMESTAMP);

    }

//    public List<Admission> completePatientDishcargedNotFinalized(String query) {
//        List<Admission> suggestions;
//        String sql;
//        HashMap h = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//            sql = "select c from Admission c where c.retired=false and "
//                    + " ( c.paymentFinalized is null or c.paymentFinalized=false )"
//                    + " and ( (upper(c.bhtNo) like :q )or (upper(c.patient.person.name)"
//                    + " like :q) ) order by c.bhtNo";
//            //////// // System.out.println(sql);
//            //      h.put("btp", BillType.InwardPaymentBill);
//            h.put("q", "%" + query.toUpperCase() + "%");
//            //suggestions = admissionFacade().findBySQL(sql, h);
//        }
//        //return suggestions;
//    }
    public void setItems(List<InstitutionBills> items) {
        this.items = items;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public CreditBean getCreditBean() {
        return creditBean;
    }

    public void setCreditBean(CreditBean creditBean) {
        this.creditBean = creditBean;
    }

    public List<String1Value5> getCreditCompanyAge() {
        return creditCompanyAge;
    }

    public void setCreditCompanyAge(List<String1Value5> creditCompanyAge) {
        this.creditCompanyAge = creditCompanyAge;
    }

    public List<String1Value5> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(List<String1Value5> filteredList) {
        this.filteredList = filteredList;
    }

    public List<InstitutionEncounters> getInstitutionEncounters() {
        return institutionEncounters;
    }

    public void setInstitutionEncounters(List<InstitutionEncounters> institutionEncounters) {
        this.institutionEncounters = institutionEncounters;
    }

    public Admission getPatientEncounter() {
        return patientEncounter;
    }

    public void setPatientEncounter(Admission patientEncounter) {
        this.patientEncounter = patientEncounter;
    }

    public AdmissionFacade getAdmissionFacade() {
        return admissionFacade;
    }

    public void setAdmissionFacade(AdmissionFacade admissionFacade) {
        this.admissionFacade = admissionFacade;
    }

    public boolean isWithOutDueUpdate() {
        return withOutDueUpdate;
    }

    public void setWithOutDueUpdate(boolean withOutDueUpdate) {
        this.withOutDueUpdate = withOutDueUpdate;
    }

    public Institution getCreditCompany() {
        return creditCompany;
    }

    public void setCreditCompany(Institution creditCompany) {
        this.creditCompany = creditCompany;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getFinalPaidTotal() {
        return finalPaidTotal;
    }

    public void setFinalPaidTotal(double finalPaidTotal) {
        this.finalPaidTotal = finalPaidTotal;
    }

    public double getFinalPaidTotalPatient() {
        return finalPaidTotalPatient;
    }

    public void setFinalPaidTotalPatient(double finalPaidTotalPatient) {
        this.finalPaidTotalPatient = finalPaidTotalPatient;
    }

    public double getFinalTransPaidTotal() {
        return finalTransPaidTotal;
    }

    public void setFinalTransPaidTotal(double finalTransPaidTotal) {
        this.finalTransPaidTotal = finalTransPaidTotal;
    }

    public double getFinalTransPaidTotalPatient() {
        return finalTransPaidTotalPatient;
    }

    public void setFinalTransPaidTotalPatient(double finalTransPaidTotalPatient) {
        this.finalTransPaidTotalPatient = finalTransPaidTotalPatient;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

}
