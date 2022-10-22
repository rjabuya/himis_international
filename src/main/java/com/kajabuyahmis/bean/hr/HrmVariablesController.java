/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.hr;
import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.entity.hr.HrmVariables;
import com.kajabuyahmis.entity.hr.PayeeTaxRange;
import com.kajabuyahmis.facade.HrmVariablesFacade;
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
public class HrmVariablesController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private HrmVariablesFacade ejbFacade;
    List<HrmVariables> selectedItems;
    private HrmVariables current;
    private PayeeTaxRange currentPayeeTaxRange;

    ;

   
    public void prepareAdd() {
        current = new HrmVariables();
    }

    public void setSelectedItems(List<HrmVariables> selectedItems) {
        this.selectedItems = selectedItems;
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

    }

    public HrmVariablesFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(HrmVariablesFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public HrmVariablesController() {
    }

    public void fetchHrmVariable() {
        String sql = "select hv "
                + " from HrmVariables hv"
                + " where hv.retired=false ";

        current = ejbFacade.findFirstBySQL(sql);
    }

    public HrmVariables getCurrent() {
        fetchHrmVariable();

        if (current == null) {
            current = new HrmVariables();

            //   Date dt=new Date();
            current.setName("HrmVariable");
        }
        return current;
    }

    public void setCurrent(HrmVariables current) {
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
        
        current = null;
        getCurrent();
    }

    private HrmVariablesFacade getFacade() {
        return ejbFacade;
    }

    private boolean errorCheck() {
        if (getCurrentPayeeTaxRange().getFromSalary() == 0) {
            UtilityController.addErrorMessage("Set From Salary");
            return true;
        }

        if (getCurrentPayeeTaxRange().getToSalary() == 0) {
            UtilityController.addErrorMessage("Set To Salary");
            return true;
        }

        if (getCurrentPayeeTaxRange().getTaxRate() == 0) {
            UtilityController.addErrorMessage("Set Tax Rate");
            return true;
        }

        return false;
    }


    public void addTaxRange() {
        if (errorCheck()) {
            return;
        }
        getCurrent().getTaxRanges().add(getCurrentPayeeTaxRange());
        currentPayeeTaxRange = null;
    }

    public PayeeTaxRange getCurrentPayeeTaxRange() {
        if (currentPayeeTaxRange == null) {
            currentPayeeTaxRange = new PayeeTaxRange();
        }
        return currentPayeeTaxRange;
    }

    public void setCurrentPayeeTaxRange(PayeeTaxRange currentPayeeTaxRange) {
        this.currentPayeeTaxRange = currentPayeeTaxRange;
    }

    /**
     *
     */
    @FacesConverter(forClass = HrmVariables.class)
    public static class HrmVariablesConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HrmVariablesController controller = (HrmVariablesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "hrmVariablesController");
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
            if (object instanceof HrmVariables) {
                HrmVariables o = (HrmVariables) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + HrmVariablesController.class.getName());
            }
        }
    }

    @FacesConverter("hrmVariablesCon")
    public static class HrmVariablesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HrmVariablesController controller = (HrmVariablesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "hrmVariablesController");
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
            if (object instanceof HrmVariables) {
                HrmVariables o = (HrmVariables) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + HrmVariablesController.class.getName());
            }
        }
    }
}
