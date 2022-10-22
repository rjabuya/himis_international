/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.lab;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.InvestigationItemType;
import com.kajabuyahmis.data.ReportItemType;
import com.kajabuyahmis.entity.Category;
import com.kajabuyahmis.entity.lab.CommonReportItem;
import com.kajabuyahmis.entity.lab.ReportFormat;
import com.kajabuyahmis.entity.lab.ReportItem;
import com.kajabuyahmis.facade.CategoryFacade;
import com.kajabuyahmis.facade.CommonReportItemFacade;
import com.kajabuyahmis.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
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
public class CommonReportItemController implements Serializable {

//  EJBs
    @EJB
    private CommonReportItemFacade ejbFacade;
    @EJB
    CategoryFacade categoryFacade;
//  Controllers    
    @Inject
    SessionController sessionController;
    @Inject
    private ReportFormatController reportFormatController;
//  Class Variables
    private static final long serialVersionUID = 1L;
    List<CommonReportItem> selectedItems;
    private CommonReportItem current;
    private List<CommonReportItem> items = null;
    String selectText = "";
    Category category;
    boolean showBorders;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        //////// // System.out.println("Common Report Format Category is " + category);
        items = null;
    }

    public void addNewLabel() {
        current = new CommonReportItem();
        current.setName("New Label");
        current.setCategory(category);

        CommonReportItem lastItem = getLastCommonReportItem();
        if (lastItem != null) {
            current.setCssFontFamily(lastItem.getCssFontFamily());
            current.setCssFontSize(lastItem.getCssFontSize());
            current.setCssFontStyle(lastItem.getCssFontStyle());
            current.setCssFontWeight(lastItem.getCssFontWeight());
        }

        getEjbFacade().create(current);
    }

    public CommonReportItem getLastCommonReportItem() {
        String j = "select i from CommonReportItem i order by i.id desc";
        return getEjbFacade().findFirstBySQL(j);
    }

    public void addNewCombo() {
        current = new CommonReportItem();
        current.setName("New Combo");
        current.setCategory(category);
        current.setIxItemType(InvestigationItemType.ItemsCatetgory);
        getEjbFacade().create(current);
    }

    public CommonReportItemController() {
    }

    public void removeItem() {
        current.setRetired(true);
        current.setRetirer(getSessionController().getLoggedUser());
        current.setRetiredAt(new Date());
        getEjbFacade().edit(getCurrent());
        getItems().remove(getCurrent());

    }

    public void duplicateItem() {
        CommonReportItem newItem = new CommonReportItem();

        ReportItem.copyReportItem(current, newItem);

        newItem.setName(current.getName() + " 1");
        newItem.setCreatedAt(new Date());
        newItem.setCreater(getSessionController().getLoggedUser());

        getEjbFacade().create(newItem);
        getItems().add(newItem);

        current = newItem;

    }

    public List<CommonReportItem> listCommonRportItems(Category commenReportFormat) {
//        System.err.println("commenReportFormat = " + commenReportFormat);
        String temSql;
        temSql = "SELECT i FROM CommonReportItem i where i.retired=false and i.category=:cat order by i.name";
        Map m = new HashMap();
        m.put("cat", commenReportFormat);
//        System.err.println("temSql = " + temSql);
//        System.err.println("m = " + m);
        items = getFacade().findBySQL(temSql, m);
        return items;
    }

    public void addNewValue() {
        current = new CommonReportItem();
        current.setName("New Value");
        current.setReportItemType(ReportItemType.PatientName);
        current.setCategory(category);
        CommonReportItem lastItem = getLastCommonReportItem();
        if (lastItem != null) {
            current.setCssFontFamily(lastItem.getCssFontFamily());
            current.setCssFontSize(lastItem.getCssFontSize());
            current.setCssFontStyle(lastItem.getCssFontStyle());
            current.setCssFontWeight(lastItem.getCssFontWeight());
        }
        getEjbFacade().create(current);
    }

    public void addNewCss() {
        current = new CommonReportItem();
        current.setName("New Css");
        current.setReportItemType(ReportItemType.PatientName);
        current.setCategory(category);
        CommonReportItem lastItem = getLastCommonReportItem();
        if (lastItem != null) {
            current.setCssFontFamily(lastItem.getCssFontFamily());
            current.setCssFontSize(lastItem.getCssFontSize());
            current.setCssFontStyle(lastItem.getCssFontStyle());
            current.setCssFontWeight(lastItem.getCssFontWeight());
        }
        getEjbFacade().create(current);
    }

    public void prepareAdd() {
        current = new CommonReportItem();
    }

    public void setSelectedItems(List<CommonReportItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
        current = null;
    }

    public void saveSelected() {

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(getCurrent());
            UtilityController.addSuccessMessage("Updated Successfully.");
        } else {
            getCurrent().setCreatedAt(new Date());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getFacade().create(getCurrent());
            UtilityController.addSuccessMessage("Saved Successfully");
        }
//        recreateModel();
//        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public CommonReportItemFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(CommonReportItemFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public CommonReportItem getCurrent() {
        if (current == null) {
            current = new CommonReportItem();
        }
        return current;
    }

    public void setCurrent(CommonReportItem current) {
        this.current = current;
    }

    private CommonReportItemFacade getFacade() {
        return ejbFacade;
    }

    public void duplicate() {
        if (category == null) {
            JsfUtil.addErrorMessage("Please selet a format");
            return;
        }
        List<CommonReportItem> ris = getItems();
        ReportFormat c = new ReportFormat();
        c.setName(category.getName() + "1");
        c.settName(category.gettName() + "1");
        c.setsName(category.getsName() + "1");
        c.setCreatedAt(new Date());
        c.setCreater(sessionController.getLoggedUser());
        getCategoryFacade().create(c);

        for (CommonReportItem ri : ris) {
            CommonReportItem tri = new CommonReportItem();
            ReportItem.copyReportItem(ri, tri);
            tri.setCategory(c);
            getFacade().create(tri);
        }
        
        reportFormatController.setItems(null);
    }

    public void addAllToCat() {
        List<CommonReportItem> is = getFacade().findAll();
        for (CommonReportItem ci : is) {
            ci.setCategory(category);
            getFacade().edit(ci);
        }
    }

    public List<CommonReportItem> getItems() {
        if (items != null) {
            return items;
        }
        String temSql;
        if (category != null) {
            temSql = "SELECT i FROM CommonReportItem i where i.retired=false and i.category=:cat order by i.name";
            Map m = new HashMap();
            m.put("cat", category);
            //////// // System.out.println("common report cat sql is " + temSql + " and " + m.toString());
            items = getFacade().findBySQL(temSql, m);
        } else {
            items = new ArrayList<>();
        }
        return items;
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }

    public List<CommonReportItem> getCategoryItems(Category cat) {
        List<CommonReportItem> cis;
        String temSql;
        if (cat != null) {
            temSql = "SELECT i FROM CommonReportItem i where i.retired=false and i.category=:cat order by i.name";
            Map m = new HashMap();
            m.put("cat", cat);
            //////// // System.out.println("common report cat sql is " + temSql + " and " + m.toString());
            cis = getFacade().findBySQL(temSql, m);
        } else {
            cis = new ArrayList<>();
        }
        return cis;
    }

    public ReportFormatController getReportFormatController() {
        return reportFormatController;
    }

    /**
     *
     */
    @FacesConverter(forClass = CommonReportItem.class)
    public static class CommonReportItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CommonReportItemController controller = (CommonReportItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "commonReportItemController");
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
            if (object instanceof CommonReportItem) {
                CommonReportItem o = (CommonReportItem) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + CommonReportItemController.class.getName());
            }
        }
    }

    public CategoryFacade getCategoryFacade() {
        return categoryFacade;
    }
    
    
    
    
}
