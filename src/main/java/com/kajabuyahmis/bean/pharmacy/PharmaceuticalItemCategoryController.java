/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.pharmacy;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalItemCategory;
import com.kajabuyahmis.facade.PharmaceuticalItemCategoryFacade;
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
public class PharmaceuticalItemCategoryController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private PharmaceuticalItemCategoryFacade ejbFacade;
    private PharmaceuticalItemCategory current;
    private List<PharmaceuticalItemCategory> items = null;
    List<PharmaceuticalItemCategory> pharmaceuticalItemCategoryList = null;

    public List<PharmaceuticalItemCategory> completeCategory(String qry) {

        Map m = new HashMap();
        m.put("n", "%" + qry + "%");
        String sql = "select c from PharmaceuticalItemCategory c where "
                + " c.retired=false and ((upper(c.name) like :n) or (upper(c.description) like :n)) order by c.name";

        pharmaceuticalItemCategoryList = getFacade().findBySQL(sql, m, 20);
        //////// // System.out.println("a size is " + a.size());

        if (pharmaceuticalItemCategoryList == null) {
            pharmaceuticalItemCategoryList = new ArrayList<>();
        }
        return pharmaceuticalItemCategoryList;
    }

    public void prepareAdd() {
        current = new PharmaceuticalItemCategory();
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        if (errorCheck()) {
            return;
        }

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Updated Successfully.");
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        recreateModel();
        getItems();
    }

    private boolean errorCheck() {
        if (getCurrent() != null) {
            if (getCurrent().getDescription() == null || getCurrent().getDescription().isEmpty()) {
                return false;
            } else {
                String sql;
                Map m = new HashMap();

                sql = " select c from PharmaceuticalItemCategory c where "
                        + " c.retired=false "
                        + " and c.description=:dis ";

                m.put("dis", getCurrent().getDescription());
                List<PharmaceuticalItemCategory> list = getFacade().findBySQL(sql, m);
                if (list.size() > 0) {
                    JsfUtil.addErrorMessage("Category Code " + getCurrent().getDescription() + " is alredy exsist.");
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public PharmaceuticalItemCategoryFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PharmaceuticalItemCategoryFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public PharmaceuticalItemCategoryController() {
    }

    public PharmaceuticalItemCategory getCurrent() {
        if (current == null) {
            current = new PharmaceuticalItemCategory();
        }
        return current;
    }

    public void setCurrent(PharmaceuticalItemCategory current) {
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
        getItems();
        current = null;
        getCurrent();
    }

    private PharmaceuticalItemCategoryFacade getFacade() {
        return ejbFacade;
    }

    public List<PharmaceuticalItemCategory> getItems() {
//        items = getFacade().findAll("name", true);
        String sql = " select c from PharmaceuticalItemCategory c where "
                + " c.retired=false "
                + " order by c.name ";

        items = getFacade().findBySQL(sql);
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = PharmaceuticalItemCategory.class)
    public static class PharmaceuticalItemCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PharmaceuticalItemCategoryController controller = (PharmaceuticalItemCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pharmaceuticalItemCategoryController");
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
            if (object instanceof PharmaceuticalItemCategory) {
                PharmaceuticalItemCategory o = (PharmaceuticalItemCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PharmaceuticalItemCategoryController.class.getName());
            }
        }
    }
}
