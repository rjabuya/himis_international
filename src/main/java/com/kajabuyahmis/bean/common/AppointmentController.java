/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.bean.membership.PaymentSchemeController;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.Sex;
import com.kajabuyahmis.data.Title;
import com.kajabuyahmis.data.dataStructure.PaymentMethodData;
import com.kajabuyahmis.data.dataStructure.YearMonthDay;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CommonFunctions;
import com.kajabuyahmis.entity.Appointment;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.Patient;
import com.kajabuyahmis.entity.Person;
import com.kajabuyahmis.facade.AppointmentFacade;
import com.kajabuyahmis.facade.BillComponentFacade;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.BillFeeFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.PatientFacade;
import com.kajabuyahmis.facade.PatientInvestigationFacade;
import com.kajabuyahmis.facade.PersonFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 Informatics)
 */
@Named
@SessionScoped
public class AppointmentController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    
    @Inject
    CommonController commonController;
    @EJB
    private BillFacade billFacade;
    @EJB
    private BillItemFacade billItemFacade;

    @EJB
    private PatientInvestigationFacade patientInvestigationFacade;
    @Inject
    private BillBeanController billBean;
    @EJB
    CommonFunctions commonFunctions;
    @EJB
    private PersonFacade personFacade;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private BillNumberGenerator billNumberBean;
    @EJB
    private BillComponentFacade billComponentFacade;
    @EJB
    private BillFeeFacade billFeeFacade;
    @EJB
    private AppointmentFacade appointmentFacade;

    private boolean printPreview;

    private Patient newPatient;
    private Patient searchedPatient;
    //private String creditCardRefNo;
    //  private String chequeRefNo;
    private String patientTabId = "tabNewPt";
    private String ageText = "";
    private Bill currentBill;
    private Appointment currentAppointment;
    private YearMonthDay yearMonthDay;
    private PaymentMethodData paymentMethodData;

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
            sql = "select c from BilledBill c where c.paidAmount is null and c.billType= :btp and c.paymentMethod= :pm and c.cancelledBill is null and c.refundedBill is null and c.retired=false and upper(c.insId) like '%" + qry.toUpperCase() + "%' order by c.creditCompany.name";
            hash.put("btp", BillType.OpdBill);
            hash.put("pm", PaymentMethod.Credit);
            a = getFacade().findBySQL(sql, hash, TemporalType.TIME);
        }
        if (a == null) {
            a = new ArrayList<Bill>();
        }
        return a;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    private Patient savePatient() {
        if (getPatientTabId().equals("tabNewPt")) {
            getNewPatient().setCreater(getSessionController().getLoggedUser());
            getNewPatient().setCreatedAt(new Date());

            getNewPatient().getPerson().setCreater(getSessionController().getLoggedUser());
            getNewPatient().getPerson().setCreatedAt(new Date());

            getPersonFacade().create(getNewPatient().getPerson());
            getPatientFacade().create(getNewPatient());
            return getNewPatient();

        } else if (getPatientTabId().equals("tabSearchPt")) {
            return getSearchedPatient();
        }

        return null;
    }

    private void saveAppointment(Patient p) {
        getCurrentAppointment().setCreatedAt(new Date());
        getCurrentAppointment().setCreater(getSessionController().getLoggedUser());
        getCurrentAppointment().setPatient(p);
        getCurrentAppointment().setBill(getCurrentBill());
        getAppointmentFacade().create(getCurrentAppointment());
        //      currentAppointment=null;
    }

    public void settleBill() {
        Date startTime = new Date();
        Date fromDate = new Date();
        Date toDate = new Date();
        if (errorCheck()) {
            return;
        }

        Patient p = savePatient();

        saveBill(p);
        saveAppointment(p);
        //  getBillBean().saveBillItems(b, getLstBillEntries(), getSessionController().getLoggedUser());
        // getBillBean().calculateBillItems(b, getLstBillEntries());
        //     getBills().add(b);

        UtilityController.addSuccessMessage("Bill Saved");
        printPreview = true;
        
        
        commonController.printReportDetails(fromDate, toDate, startTime, "Inward appoinment(/faces/inward/inward_appointment.xhtml)");
    }

    private void saveBill(Patient p) {

        //getCurrentBill().setDeptId(getBillNumberBean().departmentBillNumberGenerator(getSessionController().getDepartment(),getCurrentAppointment(), BillNumberSuffix.INWSERBillNumberSuffix.INWSER);
//        getCurrentBill().setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), BillType.Appointment));
        //  getCurrentBill().setBillType(BillType.OpdBill);
        getCurrentBill().setDepartment(getSessionController().getLoggedUser().getDepartment());
        getCurrentBill().setInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        getCurrentBill().setPatient(p);
        // getCurrentBill().setAppointment(getCurrentAppointment());
        //     getCurrentBill().setFromDepartment(getSessionController().getLoggedUser().getDepartment());
        //    getCurrentBill().setFromInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        getBillBean().setPaymentMethodData(getCurrentBill(), getCurrentBill().getPaymentMethod(), getPaymentMethodData());

        getCurrentBill().setBillDate(new Date());
        getCurrentBill().setBillTime(new Date());
        //   getCurrentBill().setPatient(tmpPatient);
//        temp.setPatientEncounter(patientEncounter);
        //   temp.setPaymentScheme(getPaymentScheme());

        getCurrentBill().setCreatedAt(new Date());
        getCurrentBill().setCreater(sessionController.getLoggedUser());
        getFacade().create(getCurrentBill());
        //return getCurrentBill();

    }

    private boolean checkPatientAgeSex() {

        if (getPatientTabId().toString().equals("tabNewPt")) {

            if (getNewPatient().getPerson().getName() == null || getNewPatient().getPerson().getName().trim().equals("") || getNewPatient().getPerson().getSex() == null || getAgeText() == null) {
                UtilityController.addErrorMessage("Can not bill without Patient Name, Age or Sex.");
                return true;
            }

            if (!com.kajabuyahmis.java.CommonFunctions.checkAgeSex(getNewPatient().getPerson().getDob(), getNewPatient().getPerson().getSex(), getNewPatient().getPerson().getTitle())) {
                UtilityController.addErrorMessage("Check Title,Age,Sex");
                return true;
            }
            
            

            if (getNewPatient().getPerson().getPhone().length() < 1) {
                UtilityController.addErrorMessage("Phone Number is Required it should be fill");
                return true;
            }

        }

        return false;

    }

    @Inject
    private PaymentSchemeController paymentSchemeController;

    private boolean errorCheck() {

        if (checkPatientAgeSex()) {
            return true;
        }

        if (getPatientTabId().toString().equals("tabSearchPt")) {
            if (getSearchedPatient() == null) {
                UtilityController.addErrorMessage("Plese Select Patient");
            }
        }

        if (getPatientTabId().toString().equals("tabNewPt")) {

            if (getNewPatient().getPerson().getName() == null || getNewPatient().getPerson().getName().trim().equals("")) {
                UtilityController.addErrorMessage("Can not bill without Patient Name");
                return true;
            }

        }

     
        if (getCurrentBill().getPaymentMethod() == null) {
            return true;
        }

        if (getPaymentSchemeController().errorCheckPaymentMethod(getCurrentBill().getPaymentMethod(), paymentMethodData)) {
            return true;
        }
//       
        return false;
    }

    public String prepareNewBill() {
        currentBill = null;

        setPrintPreview(true);
        printPreview = false;
        return "";
    }

    public void onTabChange(TabChangeEvent event) {
        setPatientTabId(event.getTab().getId());

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

    public AppointmentController() {
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

    public String getAgeText() {
        ageText = getNewPatient().getAge();
        if (ageText.startsWith("0 days")) {
            return "";
        }
        return ageText;
    }

    public void setAgeText(String ageText) {
        this.ageText = ageText;
        getNewPatient().getPerson().setDob(getCommonFunctions().guessDob(ageText));
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

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;

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

    public PatientInvestigationFacade getPatientInvestigationFacade() {
        return patientInvestigationFacade;
    }

    public void setPatientInvestigationFacade(PatientInvestigationFacade patientInvestigationFacade) {
        this.patientInvestigationFacade = patientInvestigationFacade;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;

    }

    public Bill getCurrentBill() {
        if (currentBill == null) {
            currentBill = new BilledBill();
            currentBill.setBillType(BillType.InwardAppointmentBill);
        }
        return currentBill;
    }

    public void setCurrentBill(Bill currentBill) {
        this.currentBill = currentBill;
    }

    public Appointment getCurrentAppointment() {
        if (currentAppointment == null) {
            currentAppointment = new Appointment();
        }
        return currentAppointment;
    }

    public void setCurrentAppointment(Appointment currentAppointment) {
        this.currentAppointment = currentAppointment;
    }

    public AppointmentFacade getAppointmentFacade() {
        return appointmentFacade;
    }

    public void setAppointmentFacade(AppointmentFacade appointmentFacade) {
        this.appointmentFacade = appointmentFacade;
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

    public PaymentMethodData getPaymentMethodData() {
        if (paymentMethodData == null) {
            paymentMethodData = new PaymentMethodData();
        }
        return paymentMethodData;
    }

    public void setPaymentMethodData(PaymentMethodData paymentMethodData) {
        this.paymentMethodData = paymentMethodData;
    }

    public PaymentSchemeController getPaymentSchemeController() {
        return paymentSchemeController;
    }

    public void setPaymentSchemeController(PaymentSchemeController paymentSchemeController) {
        this.paymentSchemeController = paymentSchemeController;
    }

    /**
     *
     */
    @FacesConverter(forClass = Bill.class)
    public static class BillControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AppointmentController controller = (AppointmentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "appointmentController");
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
                        + object.getClass().getName() + "; expected type: " + AppointmentController.class.getName());
            }
        }
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }
    
}
