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
import com.kajabuyahmis.entity.pharmacy.DiscardCategory;
import com.kajabuyahmis.facade.DiscardCategoryFacade;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
public class DiscardCategoryController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private DiscardCategoryFacade ejbFacade;
    List<DiscardCategory> selectedItems;
    private DiscardCategory current;
    private List<DiscardCategory> items = null;
    String selectText = "";

    public void prepareAdd() {
        current = new DiscardCategory();
    }

    public void setSelectedItems(List<DiscardCategory> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {

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

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public DiscardCategoryFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(DiscardCategoryFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public DiscardCategoryController() {
    }

    public DiscardCategory getCurrent() {
        return current;
    }

    public void setCurrent(DiscardCategory current) {
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

    private DiscardCategoryFacade getFacade() {
        return ejbFacade;
    }

    public List<DiscardCategory> getItems() {
        if (items == null) {
            String j;
            j = "select c "
                    + " from DiscardCategory c "
                    + " where c.retired=false "
                    + " order by c.name";
            items = getFacade().findBySQL(j);
        }
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = DiscardCategory.class)
    public static class DiscardCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DiscardCategoryController controller = (DiscardCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "discardCategoryController");
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
            if (object instanceof DiscardCategory) {
                DiscardCategory o = (DiscardCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + DiscardCategoryController.class.getName());
            }
        }
    }
}
