/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.hr;

import com.kajabuyahmis.bean.common.CommonController;
import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.hr.PaysheetComponentType;
import com.kajabuyahmis.data.hr.ReportKeyWord;
import com.kajabuyahmis.ejb.HumanResourceBean;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.Staff;
import com.kajabuyahmis.entity.hr.PaysheetComponent;
import com.kajabuyahmis.entity.hr.StaffBasics;
import com.kajabuyahmis.entity.hr.StaffEmployment;
import com.kajabuyahmis.entity.hr.StaffPaysheetComponent;
import com.kajabuyahmis.facade.PaysheetComponentFacade;
import com.kajabuyahmis.facade.StaffEmploymentFacade;
import com.kajabuyahmis.facade.StaffFacade;
import com.kajabuyahmis.facade.StaffPaysheetComponentFacade;
import com.kajabuyahmis.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class StaffBasicController implements Serializable {

    private StaffPaysheetComponent current;
    ////////////////
    private List<StaffPaysheetComponent> filteredStaffPaysheet;
    private List<StaffPaysheetComponent> items;
    private List<StaffPaysheetComponent> repeatedComponent;
    private List<StaffPaysheetComponent> selectedStaffComponent;
    /////////////////
    @EJB
    HumanResourceBean humanResourceBean;
    @EJB
    private StaffPaysheetComponentFacade staffPaysheetComponentFacade;
    @EJB
    private PaysheetComponentFacade paysheetComponentFacade;
    @EJB
    private StaffEmploymentFacade staffEmploymentFacade;
    @EJB
    private StaffFacade staffFacade;
    ////////
    @Inject
    private SessionController sessionController;
    private Date fromDate;
    private Date toDate;
    private ReportKeyWord reportKeyWord;
    Institution staffInstitution;
    @Inject
    CommonController commonController;

    public void removeAll() {
        for (StaffPaysheetComponent spc : getSelectedStaffComponent()) {
            spc.setRetired(true);
            spc.setRetiredAt(new Date());
            spc.setRetirer(getSessionController().getLoggedUser());
            getStaffPaysheetComponentFacade().edit(spc);
        }

        makeNull();
    }

    public void onEdit(RowEditEvent event) {
        StaffPaysheetComponent tmp = (StaffPaysheetComponent) event.getObject();
        tmp.setLastEditedAt(new Date());
        tmp.setLastEditor(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(tmp);

    }

    private boolean errorCheck() {
        if (getCurrent().getStaff() == null) {
            UtilityController.addErrorMessage("Select Staff");
            return true;
        }

        if (getCurrent().getFromDate() == null) {
            UtilityController.addErrorMessage("Select From Date");
            return true;
        }

        if (getCurrent().getToDate() == null) {
            UtilityController.addErrorMessage("Select To Date");
            return true;
        }

        if (humanResourceBean.checkStaff(getCurrent(), getCurrent().getPaysheetComponent(), getCurrent().getStaff(), getCurrent().getFromDate(), getCurrent().getToDate())) {
            UtilityController.addErrorMessage("There is Some component in Same Date Range");
            return true;
        }

        return false;
    }

    public void remove() {
        getCurrent().setRetired(true);
        getCurrent().setRetiredAt(new Date());
        getCurrent().setRetirer(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(getCurrent());

        items = null;
        Staff s = getCurrent().getStaff();
        current = null;
        getCurrent(s);
    }

    private void updateExistingSalary() {
        String sql = "Select s From StaffPaysheetComponent s "
                + " where s.retired=false"
                + " and s.paysheetComponent.componentType=:tp "
                + " and s.staff=:st "
                + " and s.fromDate<:dt and s.toDate is null";

        HashMap hm = new HashMap();
        hm.put("tp", PaysheetComponentType.BasicSalary);
        hm.put("st", getCurrent().getStaff());
        hm.put("dt", getCurrent().getFromDate());
        List<StaffPaysheetComponent> tmp = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        for (StaffPaysheetComponent ss : tmp) {
            ss.setToDate(getCurrent().getFromDate());
            getStaffPaysheetComponentFacade().edit(ss);
        }
    }

    public void save() {

        if (errorCheck()) {
            return;
        }

        getCurrent().setCreatedAt(new Date());
        getCurrent().setCreater(getSessionController().getLoggedUser());

        if (getCurrent().getId() == null) {
            getStaffPaysheetComponentFacade().create(getCurrent());
        } else {
            staffPaysheetComponentFacade.edit(current);
        }

//        updateStaffEmployment();
//        updateExistingSalary();
        Staff s = getCurrent().getStaff();
        Date fd = getCurrent().getFromDate();
        Date td = getCurrent().getToDate();
        current = null;
        items = null;
        getCurrent(s, fd, td);
        JsfUtil.addSuccessMessage("Sucessfully Saved...");
        JsfUtil.addSuccessMessage("Staff Name - " + s.getPerson().getName());

    }

    private void updateStaffEmployment() {

        if (getCurrent().getStaff().getStaffEmployment() == null) {
            //   //////// // System.out.println("ceate A :");
            StaffEmployment se = new StaffEmployment();
            se.setCreatedAt(new Date());
            se.setCreater(getSessionController().getLoggedUser());
            se.setFromDate(new Date());
            se.setStaff(getCurrent().getStaff());
            getStaffEmploymentFacade().create(se);

            getCurrent().getStaff().setStaffEmployment(se);
            getStaffFacade().edit(getCurrent().getStaff());
        }
        //   //////// // System.out.println("ceate B :");
        createComponent();
        // //////// // System.out.println("ceate C :");
        getStaffEmploymentFacade().edit(getCurrent().getStaff().getStaffEmployment());
    }

    private void createComponent() {

        //////// // System.out.println("ceate D :" + getCurrent().getStaff().getStaffEmployment());     
        StaffBasics tmp = new StaffBasics();
        tmp.setCreatedAt(new Date());
        tmp.setCreater(getSessionController().getLoggedUser());
        tmp.setFromDate(new Date());
        tmp.setBasic(getCurrent());
        tmp.setStaffEmployment(getCurrent().getStaff().getStaffEmployment());
        getCurrent().getStaff().getStaffEmployment().getStaffBasics().add(tmp);

    }

    public void makeNull() {
        current = null;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;
    }

    public void makeNullWithoutFromDate() {
        current = null;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;

    }

    public void makeItemNul() {
        items = null;
    }

    public void createBasicTable() {
        HashMap hm = new HashMap();
        String sql;
        sql = "Select ss from StaffPaysheetComponent ss"
                + " where ss.retired=false "
                + " and ss.paysheetComponent.componentType=:tp";

        if (getFromDate() != null) {
            sql += " and ((ss.fromDate <=:fd "
                    + " and ss.toDate >=:fd) or ss.fromDate >=:fd) ";
            hm.put("fd", getFromDate());
        }

        if (getReportKeyWord().getStaff() != null) {
            sql += " and ss.staff=:stf ";
            hm.put("stf", getReportKeyWord().getStaff());
        }

        if (getReportKeyWord().getDepartment() != null) {
            sql += " and ss.staff.workingDepartment=:dep ";
            hm.put("dep", getReportKeyWord().getDepartment());
        }

        if (getReportKeyWord().getInstitution() != null) {
            sql += " and ss.staff.institution=:ins";
            hm.put("ins", getReportKeyWord().getInstitution());
        }

        if (getReportKeyWord().getStaffCategory() != null) {
            sql += " and ss.staff.staffCategory=:stfCat";
            hm.put("stfCat", getReportKeyWord().getStaffCategory());
        }

        if (getReportKeyWord().getDesignation() != null) {
            sql += " and ss.staff.designation=:des";
            hm.put("des", getReportKeyWord().getDesignation());
        }

        if (getReportKeyWord().getRoster() != null) {
            sql += " and ss.staff.roster=:rs ";
            hm.put("rs", getReportKeyWord().getRoster());
        }

        sql += " order by ss.staff.codeInterger ";

        hm.put("tp", PaysheetComponentType.BasicSalary);

        items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!getRepeatedComponent().isEmpty()) {
            for (StaffPaysheetComponent sp : items) {
                for (StaffPaysheetComponent err : getRepeatedComponent()) {
                    if (sp.getId().equals(err.getId())) {
                        sp.setExist(true);
                        //////// // System.out.println("settin");
                    }
                }
            }
        }
    }

    public List<StaffPaysheetComponent> getItems() {

        return items;
    }

    PaysheetComponent paysheetComponent;

    PaysheetComponent paysheetComponent2;

    public PaysheetComponent getPaysheetComponent() {
        return paysheetComponent;
    }

    public PaysheetComponent getPaysheetComponent2() {
        return paysheetComponent2;
    }

    public void setPaysheetComponent2(PaysheetComponent paysheetComponent2) {
        this.paysheetComponent2 = paysheetComponent2;
    }

    public void setPaysheetComponent(PaysheetComponent paysheetComponent) {
        this.paysheetComponent = paysheetComponent;
    }

    double totalStaffPaySheetComponentValue = 0.0;

    public void createTable() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        String sql = "Select s"
                + " from StaffPaysheetComponent s"
                + " where s.retired=false"
                + " and s.staffPaySheetComponentValue!=0"
                + " and s.fromDate>= :cu";
//                + " and s.toDate>:cu";

        HashMap hm = new HashMap();

        hm.put("cu", date);
        if (paysheetComponent != null) {
            sql += " and s.paysheetComponent=:tp ";
            hm.put("tp", paysheetComponent);
        }

        if (paysheetComponent2 != null) {
            sql += " and s.paysheetComponent=:tp2 ";
            hm.put("tp2", paysheetComponent2);
        }

        if (getReportKeyWord().getStaff() != null) {
            sql += " and s.staff=:stf ";
            hm.put("stf", getReportKeyWord().getStaff());
        }

        if (getReportKeyWord().getInstitution() != null) {
            sql += " and s.staff.workingDepartment.institution=:ins ";
            hm.put("ins", getReportKeyWord().getInstitution());
        }

        if (getReportKeyWord().getBank() != null) {
            sql += " and s.bankBranch=:bk ";
            hm.put("bk", getReportKeyWord().getBank());
        }

        if (getReportKeyWord().getInstitutionBank() != null) {
            sql += " and s.bankBranch.institution=:insbk ";
            hm.put("insbk", getReportKeyWord().getInstitutionBank());
        }

        if (getReportKeyWord().getDepartment() != null) {
            sql += " and s.staff.workingDepartment=:dep ";
            hm.put("dep", getReportKeyWord().getDepartment());
        }

        if (getReportKeyWord().getStaffCategory() != null) {
            sql += " and s.staff.staffCategory=:stfCat";
            hm.put("stfCat", getReportKeyWord().getStaffCategory());
        }

        if (getReportKeyWord().getDesignation() != null) {
            sql += " and s.staff.designation=:des";
            hm.put("des", getReportKeyWord().getDesignation());
        }

        if (getReportKeyWord().getRoster() != null) {
            sql += " and s.staff.roster=:rs ";
            hm.put("rs", getReportKeyWord().getRoster());
        }

        sql += " order by s.staff.codeInterger,s.paysheetComponent.orderNo";
        items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);
        calTotal(items);

        commonController.printReportDetails(fromDate, toDate, startTime, "HR/Reports/Salary Report/staff paysheet component list(/faces/hr/hr_staff_paysheet_component_list.xhtml)");

    }

    public void calTotal(List<StaffPaysheetComponent> staffPaysheetComponents) {
        totalStaffPaySheetComponentValue = 0.0;
        for (StaffPaysheetComponent spc : staffPaysheetComponents) {
            totalStaffPaySheetComponentValue += spc.getStaffPaySheetComponentValue();
        }
    }

    public List<StaffPaysheetComponent> getItems2() {
        if (items == null) {
            String sql = "Select s from StaffPaysheetComponent s"
                    + " where s.retired=false "
                    + " and s.paysheetComponent.componentType=:tp "
                    + " order by s.staff.codeInterger";
            //and (s.toDate>= :td or s.toDate is null)
            HashMap hm = new HashMap();
            // hm.put("td", getToDate());
            //    hm.put("fd", getFromDate());
            //  hm.put("st", getCurrent().getStaff());
            hm.put("tp", PaysheetComponentType.BasicSalary);

            items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        }

        return items;
    }

    private Date date;

    public void resetDate() {

        for (StaffPaysheetComponent stf : items) {
            Calendar date = Calendar.getInstance();
            if (stf.getFromDate() != null) {
                date.setTime(stf.getFromDate());
                date.set(Calendar.YEAR, 2014);
                date.set(Calendar.MONTH, 01);
                stf.setFromDate(date.getTime());
            }

            if (stf.getToDate() != null) {

                date.setTime(stf.getToDate());
                date.setTime(stf.getToDate());
                date.set(Calendar.YEAR, 2015);
                date.set(Calendar.MONTH, 11);
                stf.setToDate(date.getTime());
            }

            getStaffPaysheetComponentFacade().edit(stf);

        }
    }

    public PaysheetComponent getBasicCompnent() {
        String sql = "Select pc From PaysheetComponent pc where pc.retired=false and pc.componentType=:tp";
        HashMap hm = new HashMap();
        hm.put("tp", PaysheetComponentType.BasicSalary);

        return getPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

    }

    public StaffBasicController() {
    }

    public StaffPaysheetComponent getCurrent() {
        if (current == null) {
            current = new StaffPaysheetComponent();
            current.setPaysheetComponent(getBasicCompnent());
        }
        return current;
    }

    public StaffPaysheetComponent getCurrent(Staff s) {
        if (current == null) {
            current = new StaffPaysheetComponent();
            current.setPaysheetComponent(getBasicCompnent());
            current.setStaff(s);
        }
        return current;
    }

    public StaffPaysheetComponent getCurrent(Staff s, Date fd, Date td) {
        if (current == null) {
            current = new StaffPaysheetComponent();
            current.setPaysheetComponent(getBasicCompnent());
            current.setStaff(s);
            current.setFromDate(fd);
            current.setToDate(td);
        }
        return current;
    }

    public void setCurrent(StaffPaysheetComponent current) {
        this.current = current;
    }

    public StaffPaysheetComponentFacade getStaffPaysheetComponentFacade() {
        return staffPaysheetComponentFacade;
    }

    public void setStaffPaysheetComponentFacade(StaffPaysheetComponentFacade staffPaysheetComponentFacade) {
        this.staffPaysheetComponentFacade = staffPaysheetComponentFacade;
    }

    public PaysheetComponentFacade getPaysheetComponentFacade() {
        return paysheetComponentFacade;
    }

    public void setPaysheetComponentFacade(PaysheetComponentFacade paysheetComponentFacade) {
        this.paysheetComponentFacade = paysheetComponentFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public List<StaffPaysheetComponent> getFilteredStaffPaysheet() {
        return filteredStaffPaysheet;
    }

    public void setFilteredStaffPaysheet(List<StaffPaysheetComponent> filteredStaffPaysheet) {
        this.filteredStaffPaysheet = filteredStaffPaysheet;
    }

    public List<StaffPaysheetComponent> getRepeatedComponent() {
        if (repeatedComponent == null) {
            repeatedComponent = new ArrayList<>();
        }
        return repeatedComponent;
    }

    public void setRepeatedComponent(List<StaffPaysheetComponent> repeatedComponent) {
        this.repeatedComponent = repeatedComponent;
    }

    public List<StaffPaysheetComponent> getSelectedStaffComponent() {
        if (selectedStaffComponent == null) {
            selectedStaffComponent = new ArrayList<>();
        }
        return selectedStaffComponent;
    }

    public void setSelectedStaffComponent(List<StaffPaysheetComponent> selectedStaffComponent) {
        this.selectedStaffComponent = selectedStaffComponent;
    }

    public StaffEmploymentFacade getStaffEmploymentFacade() {
        return staffEmploymentFacade;
    }

    public void setStaffEmploymentFacade(StaffEmploymentFacade staffEmploymentFacade) {
        this.staffEmploymentFacade = staffEmploymentFacade;
    }

    public StaffFacade getStaffFacade() {
        return staffFacade;
    }

    public void setStaffFacade(StaffFacade staffFacade) {
        this.staffFacade = staffFacade;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public ReportKeyWord getReportKeyWord() {
        if (reportKeyWord == null) {
            reportKeyWord = new ReportKeyWord();
        }
        return reportKeyWord;
    }

    public void setReportKeyWord(ReportKeyWord reportKeyWord) {
        this.reportKeyWord = reportKeyWord;
    }

    public double getTotalStaffPaySheetComponentValue() {
        return totalStaffPaySheetComponentValue;
    }

    public void setTotalStaffPaySheetComponentValue(double totalStaffPaySheetComponentValue) {
        this.totalStaffPaySheetComponentValue = totalStaffPaySheetComponentValue;
    }

    public Institution getStaffInstitution() {
        return staffInstitution;
    }

    public void setStaffInstitution(Institution staffInstitution) {
        this.staffInstitution = staffInstitution;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

}
