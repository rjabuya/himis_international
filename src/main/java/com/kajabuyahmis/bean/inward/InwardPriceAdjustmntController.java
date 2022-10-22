/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.inward;

import com.kajabuyahmis.bean.common.CommonController;
import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.entity.Category;
import com.kajabuyahmis.entity.Department;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.PaymentScheme;
import com.kajabuyahmis.entity.PriceMatrix;
import com.kajabuyahmis.entity.ServiceCategory;
import com.kajabuyahmis.entity.ServiceSubCategory;
import com.kajabuyahmis.entity.inward.InwardPriceAdjustment;
import com.kajabuyahmis.entity.lab.InvestigationCategory;
import com.kajabuyahmis.entity.pharmacy.ConsumableCategory;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalItemCategory;
import com.kajabuyahmis.facade.PriceMatrixFacade;
import java.io.Serializable;
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class InwardPriceAdjustmntController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @Inject
    CommonController commonController;
    @EJB
    private PriceMatrixFacade ejbFacade;
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;
    private PriceMatrix current;
    private List<PriceMatrix> items = null;
    List<InwardPriceAdjustment> inwardPriceAdjustments;
    BillType billType;
    PaymentScheme paymentScheme;
    Category category;
    Institution institution;
    Department department;
    double fromPrice;
    double toPrice;
    double margin;
    private Category roomLocation;

    private void recreateModel() {
        fromPrice = toPrice + 1;
        toPrice = 0.0;
        margin = 0;
        items = null;
    }

    public List<InwardPriceAdjustment> getInwardPriceAdjustments() {
        return inwardPriceAdjustments;
    }

    public void setInwardPriceAdjustments(List<InwardPriceAdjustment> inwardPriceAdjustments) {
        this.inwardPriceAdjustments = inwardPriceAdjustments;
    }

//    public void copyPriceMetrixAsCredit(){
//        
//        String sql;
//        HashMap hm = new HashMap();
//        sql = " select pm from InwardPriceAdjustment pm "
//                + " where pm.retired = false"
//                + " and pm.paymentMethod =:pay";
//        hm.put("pay", PaymentMethod.Cash);
//        inwardPriceAdjustments = ejbFacade.findBySQL(sql, hm);
//        
//        for(InwardPriceAdjustment pm : inwardPriceAdjustments){
//            InwardPriceAdjustment prima = new InwardPriceAdjustment();
//            prima.setDepartment(pm.getDepartment());
//            prima.setDiscountPercent(pm.getDiscountPercent());
//            prima.setFromPrice(pm.getFromPrice());
//            prima.setMargin(pm.getMargin());
//            prima.setPaymentMethod(PaymentMethod.Credit);
//            prima.setToPrice(pm.getToPrice());
//            prima.setCategory(pm.getCategory());
//            prima.setInstitution(pm.getInstitution());
//            prima.setCreatedAt(pm.getCreatedAt());
//            prima.setCreater(pm.getCreater());
//            ejbFacade.create(prima);
//        }
//    }
    public void saveSelected() {

        if (fromPrice == toPrice) {
            UtilityController.addErrorMessage("Check prices");
            return;
        }
        if (toPrice == 0) {
            UtilityController.addErrorMessage("Check prices");
            return;
        }

        if (department == null) {
            UtilityController.addErrorMessage("Please select a department");
            return;
        }

        if (category == null) {
            UtilityController.addErrorMessage("Please select a category");
            return;
        }

        PriceMatrix a = new InwardPriceAdjustment();

        a.setCategory(category);
        a.setDepartment(department);
        a.setFromPrice(fromPrice);
        a.setToPrice(toPrice);
        a.setInstitution(department.getInstitution());
        a.setPaymentMethod(paymentMethod);
        a.setMargin(margin);
        a.setCreatedAt(new Date());
        a.setCreater(getSessionController().getLoggedUser());
        if (a.getId() == null) {
            getFacade().create(a);
        }
        UtilityController.addSuccessMessage("Saved Successfully");
        recreateModel();
//        createItems();
    }

    public PriceMatrixFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PriceMatrixFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public InwardPriceAdjustmntController() {
    }

    public PriceMatrix getCurrent() {
        return current;
    }

    public void setCurrent(PriceMatrix current) {

        this.current = current;
    }

    public BillType getBillType() {
        return billType;
    }

    public void setBillType(BillType billType) {
        this.billType = billType;
    }

    public PaymentScheme getPaymentScheme() {
        return paymentScheme;
    }

    public void setPaymentScheme(PaymentScheme paymentScheme) {
        this.paymentScheme = paymentScheme;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public double getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(double fromPrice) {
        this.fromPrice = fromPrice;
    }

    public double getToPrice() {
        return toPrice;
    }

    public void setToPrice(double toPrice) {
        this.toPrice = toPrice;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
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
        //    recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private PriceMatrixFacade getFacade() {
        return ejbFacade;
    }

    private List<PriceMatrix> filterItems;

    public List<PriceMatrix> getItems() {

        return items;
    }

    public void createItems() {
        String sql;
        sql = "select a from InwardPriceAdjustment a "
                + " where a.retired=false "
                + " order by a.department.name,a.category.name,a.fromPrice";
        items = getFacade().findBySQL(sql);
    }

    public void createCategroyService() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        filterItems = null;
        String sql;
        HashMap hm = new HashMap();
        sql = "select a from InwardPriceAdjustment a"
                + " where a.retired=false "
                + " and type(a.category)=:service "
                + " or type(a.category)=:sub "
                + " order by a.department.name,a.category.name,a.fromPrice";
        hm.put("service", ServiceCategory.class);
        hm.put("sub", ServiceSubCategory.class);
        items = getFacade().findBySQL(sql, hm);

        commonController.printReportDetails(fromDate, toDate, startTime, "Inward Administration/Price Metrix/Inward Price adjustment - service(/faces/inward/inward_price_adjustment_service.xhtml)");
    }

    public void createCategroyServicePharmacy() {
        filterItems = null;
        String sql;
        HashMap hm = new HashMap();
        sql = "select a from InwardPriceAdjustment a "
                + " where a.retired=false "
                + " and type(a.category)=:service "
                + " or type(a.category)=:sub"
                + " or type(a.category)=:cat "
                + " order by a.department.name,a.category.name,a.fromPrice";
        hm.put("service", ServiceCategory.class);
        hm.put("sub", ServiceSubCategory.class);
        hm.put("cat", PharmaceuticalItemCategory.class);
        items = getFacade().findBySQL(sql, hm);
    }

    public void createCategroyInvestiagtion() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        filterItems = null;
        String sql;
        HashMap hm = new HashMap();
        sql = "select a from InwardPriceAdjustment a "
                + " where a.retired=false "
                + " and type(a.category)=:cat  "
                + " order by a.department.name,a.category.name,a.fromPrice";
        hm.put("cat", InvestigationCategory.class);

        items = getFacade().findBySQL(sql, hm);

        commonController.printReportDetails(fromDate, toDate, startTime, "Inward Administration/Price Metrix/Inward Price adjustment - Investigation"
                + "(/faces/inward/inward_price_adjustment_investigation.xhtml)");
    }

    public void createCategroyPharmacy() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;
        
        filterItems = null;
        String sql;
        HashMap hm = new HashMap();
        sql = "select a from InwardPriceAdjustment a where "
                + " a.retired=false "
                + " and type(a.category)=:cat  "
                + " order by a.department.name,a.category.name,a.fromPrice";
        hm.put("cat", PharmaceuticalItemCategory.class);

        items = getFacade().findBySQL(sql, hm);
        
        commonController.printReportDetails(fromDate, toDate, startTime, "Inward Administration/Price Metrix/Inward Price adjustment - Pharmacy"
                + "(/faces/inward/inward_price_adjustment_investigation.xhtml)");
    }

    public void createCategroyStore() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;
        
        filterItems = null;
        String sql;
        HashMap hm = new HashMap();
        sql = "select a from InwardPriceAdjustment a "
                + " where a.retired=false "
                + " and type(a.category)=:cat  "
                + " order by a.department.name,a.category.name,a.fromPrice";
        hm.put("cat", ConsumableCategory.class);

        items = getFacade().findBySQL(sql, hm);
        
        commonController.printReportDetails(fromDate, toDate, startTime, "Inward Administration/Price Metrix/Inward Price adjustment - Store"
                + "(/faces/inward/inward_price_adjustment_investigation.xhtml)");
    }

    public void onEdit(PriceMatrix tmp) {
        //Cheking Minus Value && Null
        getFacade().edit(tmp);
        //  createItems();
    }

    public Category getRoomLocation() {

        return roomLocation;
    }

    public void setRoomLocation(Category roomLocation) {
        this.roomLocation = roomLocation;
    }

    public List<PriceMatrix> getFilterItems() {
        return filterItems;
    }

    public void setFilterItems(List<PriceMatrix> filterItems) {
        this.filterItems = filterItems;
    }

    /**
     *
     */
    @FacesConverter(forClass = PriceMatrix.class)
    public static class InwardPriceAdjustmentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InwardPriceAdjustmntController controller = (InwardPriceAdjustmntController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "inwardPriceAdjustmentController");
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
            if (object instanceof PriceMatrix) {
                PriceMatrix o = (PriceMatrix) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + InwardPriceAdjustmntController.class.getName());
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
