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
import com.kajabuyahmis.data.DepartmentType;
import com.kajabuyahmis.entity.Department;
import com.kajabuyahmis.entity.inward.TimedItem;
import com.kajabuyahmis.facade.DepartmentFacade;
import com.kajabuyahmis.facade.SpecialityFacade;
import com.kajabuyahmis.facade.TimedItemFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class TimedItemController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private TimedItemFacade ejbFacade;
    @EJB
    private SpecialityFacade specialityFacade;
    @Inject
    private BillBeanController billBean;
    List<TimedItem> selectedItems;
    private TimedItem current;
    private List<TimedItem> items = null;
    String selectText = "";
    String bulkText = "";
    boolean billedAs;
    boolean reportedAs;
    @EJB
    private DepartmentFacade departmentFacade;

    public List<Department> getInstitutionDepatrments() {
        List<Department> d;
        //////System.out.println("gettin ins dep ");
        if(current==null){
             return new ArrayList<>();
        }
        if (getCurrent().getInstitution() == null) {
            return new ArrayList<>();
        } else {
            String sql = "Select d From Department d where d.retired=false and d.institution=:ins order by d.name";
            HashMap hm = new HashMap();
            hm.put("ins", getCurrent().getInstitution());
            d = getDepartmentFacade().findBySQL(sql, hm);
        }

        return d;
    }

    public List<TimedItem> completeInvest(String query) {
        List<TimedItem> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<TimedItem>();
        } else {
            sql = "select c from TimedItem c where c.retired=false and upper(c.name) like '%" + query.toUpperCase() + "%' order by c.name";
            //////System.out.println(sql);
            suggestions = getFacade().findBySQL(sql);
        }
        return suggestions;
    }

    DepartmentType departmentType;

    public DepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(DepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    public String inwardTimedServiceConsume() {
        departmentType = null;
        return "/inward/inward_timed_service_consume";
    }
    
    public String inwardTimedServiceConsumeInward() {
        departmentType =DepartmentType.Inward;
        return "/inward/inward_timed_service_consume";
    }
    
    public String inwardTimedServiceConsumeTheatre() {
        departmentType = DepartmentType.Theatre;
        return "/theater/inward_timed_service_consume_surgery";
    }

    public List<TimedItem> completeTimedService(String query) {
        List<TimedItem> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<TimedItem>();
        } else {
            if (departmentType == null) {
                sql = "select c from TimedItem c "
                        + " where c.retired=false and upper(c.name) like '%" + query.toUpperCase() + "%' "
                        + " order by c.name";
                suggestions = getFacade().findBySQL(sql);
            } else {
                sql = "select c from TimedItem c "
                        + " where c.retired=false and upper(c.name) like '%" + query.toUpperCase() + "%' "
                        + " and c.departmentType=:dt "
                        + " order by c.name";
                Map m = new HashMap();
                m.put("dt", departmentType);
                suggestions = getFacade().findBySQL(sql,m);
            }
        }
        return suggestions;
    }

    public boolean isBilledAs() {
        return billedAs;
    }

    public void setBilledAs(boolean billedAs) {
        this.billedAs = billedAs;
    }

    public boolean isReportedAs() {
        return reportedAs;
    }

    public void setReportedAs(boolean reportedAs) {
        this.reportedAs = reportedAs;
    }

    public BillBeanController getBillBean() {
        return billBean;
    }

    public void setBillBean(BillBeanController billBean) {
        this.billBean = billBean;
    }

    public void correctIx() {
        List<TimedItem> allItems = getEjbFacade().findAll();
        for (TimedItem i : allItems) {
            i.setPrintName(i.getName());
            i.setFullName(i.getName());
            i.setShortName(i.getName());
            i.setDiscountAllowed(Boolean.TRUE);
            i.setUserChangable(false);
            i.setTotal(getBillBean().totalFeeforItem(i));
            getEjbFacade().edit(i);
        }

    }

    public void correctIx1() {
        List<TimedItem> allItems = getEjbFacade().findAll();
        for (TimedItem i : allItems) {
            i.setBilledAs(i);
            i.setReportedAs(i);
            getEjbFacade().edit(i);
        }

    }

    public String getBulkText() {

        return bulkText;
    }

    public void setBulkText(String bulkText) {
        this.bulkText = bulkText;
    }

    public List<TimedItem> getSelectedItems() {

        if (selectText.trim().equals("")) {
            selectedItems = getFacade().findBySQL("select c from TimedItem c where c.retired=false order by c.name");
        } else {
            String sql = "select c from TimedItem c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name";
            selectedItems = getFacade().findBySQL(sql);

        }
        return selectedItems;
    }

    public List<TimedItem> getSelectedTheatreItems() {
        String sql = "select c from TimedItem c "
                + " where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' "
                + " and c.departmentType=:dt "
                + " order by c.name";
        Map m = new HashMap();
        m.put("dt", DepartmentType.Theatre);
        selectedItems = getFacade().findBySQL(sql, m);
        ////System.out.println("selectedItems = " + selectedItems);
        return selectedItems;
    }

    public List<TimedItem> getSelectedInwardItems() {
        String sql = "select c from TimedItem c "
                + " where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' "
                + " and c.departmentType=:dt "
                + " order by c.name";
        Map m = new HashMap();
        m.put("dt", DepartmentType.Inward);
        selectedItems = getFacade().findBySQL(sql, m);
        ////System.out.println("selectedItems = " + selectedItems);
        return selectedItems;
    }

    public void prepareAdd() {
        current = new TimedItem();
    }

    public void prepareInwardTimeServiceAdd() {
        current = new TimedItem();
        current.setDepartmentType(DepartmentType.Inward);
    }

    public void prepareTheatreTimeServiceAdd() {
        current = new TimedItem();
        current.setDepartmentType(DepartmentType.Theatre);
    }

    public void bulkUpload() {
        List<String> lstLines = Arrays.asList(getBulkText().split("\\r?\\n"));
        for (String s : lstLines) {
            List<String> w = Arrays.asList(s.split(","));
            try {
                String code = w.get(0);
                String ix = w.get(1);
                String ic = w.get(2);
                String f = w.get(4);
                //////System.out.println(code + " " + ix + " " + ic + " " + f);

                TimedItem tix = new TimedItem();
                tix.setCode(code);
                tix.setName(ix);
                tix.setDepartment(null);

            } catch (Exception e) {
            }

        }
    }

    public void setSelectedItems(List<TimedItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
        selectedItems = null;
    }

    public void saveSelected() {
        if(current==null){
            UtilityController.addErrorMessage("Nothing to save. Please click add button.");
            return;
        }
        if (getCurrent().getDepartment() == null) {
            UtilityController.addErrorMessage("Please Select Department");
            return;
        }

        if (getCurrent().getInwardChargeType() == null) {
            UtilityController.addErrorMessage("Please selelct Inward Charge Type");
            return;
        }

        if (getCurrent().getId() == null) {
//            if (billedAs == false) {
//                //////System.out.println("2");
//                getCurrent().setBilledAs(getCurrent());
//
//            }
//            if (reportedAs == false) {
//                getCurrent().setReportedAs(getCurrent());
//            }
            ////System.out.println("current.getDepartmentType() = " + current.getDepartmentType());
            getCurrent().setCreatedAt(new Date());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Updated Successfully");
        } else {
//            
//            getFacade().create(getCurrent());
//            if (billedAs == false) {
//                getCurrent().setBilledAs(getCurrent());
//            }
//            if (reportedAs == false) {
//                getCurrent().setReportedAs(getCurrent());
//            }
            getFacade().edit(getCurrent());
            ////System.out.println("current.getDepartmentType() = " + current.getDepartmentType());
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        recreateModel();
        //  getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public TimedItemFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(TimedItemFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public TimedItemController() {
    }

    public TimedItem getCurrent() {
//        if (current == null) {
//            current = new TimedItem();
//        }
        return current;
    }

    public void setCurrent(TimedItem current) {
        this.current = current;
//        if (current != null) {
//            if (current.getBilledAs() == current) {
//                billedAs = false;
//            } else {
//                billedAs = true;
//            }
//            if (current.getReportedAs() == current) {
//                reportedAs = false;
//            } else {
//                reportedAs = true;
//            }
//        }
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
        getItems();
        current = null;
        getCurrent();
    }

    private TimedItemFacade getFacade() {
        return ejbFacade;
    }

    public List<TimedItem> getItems() {
        items = getFacade().findAll("name", true);
        return items;
    }

    public SpecialityFacade getSpecialityFacade() {
        return specialityFacade;
    }

    public void setSpecialityFacade(SpecialityFacade specialityFacade) {
        this.specialityFacade = specialityFacade;
    }

    public DepartmentFacade getDepartmentFacade() {
        return departmentFacade;
    }

    public void setDepartmentFacade(DepartmentFacade departmentFacade) {
        this.departmentFacade = departmentFacade;
    }

    /**
     *
     */
    @FacesConverter(forClass = TimedItem.class)
    public static class TimedItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TimedItemController controller = (TimedItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "timedItemController");
            return controller.getEjbFacade().find(getKey(value));
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
            if (object instanceof TimedItem) {
                TimedItem o = (TimedItem) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + TimedItemController.class.getName());
            }
        }
    }

    @FacesConverter("timedIt")
    public static class TimedItemConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TimedItemController controller = (TimedItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "timedItemController");
            return controller.getEjbFacade().find(getKey(value));
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
            if (object instanceof TimedItem) {
                TimedItem o = (TimedItem) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + TimedItemController.class.getName());
            }
        }
    }
}
