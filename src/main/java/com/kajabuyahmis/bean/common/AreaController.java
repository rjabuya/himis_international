/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.entity.Area;
import com.kajabuyahmis.facade.AreaFacade;
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

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class AreaController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private AreaFacade ejbFacade;
    private Area current;
    private List<Area> items = null;

    public List<Area> completeArea(String qry) {
        List<Area> list;
        String sql;
        HashMap hm = new HashMap();
        sql = "select c from Area c "
                + " where c.retired=false "
                + " and upper(c.name) like :q "
                + " order by c.name";
        hm.put("q", "%" + qry.toUpperCase() + "%");
        list = getFacade().findBySQL(sql, hm);

        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public void prepareAdd() {
        current = new Area();
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        if (getCurrent().getName().isEmpty() || getCurrent().getName() == null) {
            UtilityController.addErrorMessage("Please enter Value");
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

    public AreaFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(AreaFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public AreaController() {
    }

    public Area getCurrent() {
        if (current == null) {
            current = new Area();
        }
        return current;
    }

    public void setCurrent(Area current) {
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

    private AreaFacade getFacade() {
        return ejbFacade;
    }

    public List<Area> getItems() {
        if (items == null) {
            String j;
            j = "select a "
                    + " from Area a "
                    + " where a.retired=false "
                    + " order by a.name";
            items = getFacade().findBySQL(j);
        }
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = Area.class)
    public static class AreaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AreaController controller = (AreaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "areaController");
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
            if (object instanceof Area) {
                Area o = (Area) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + AreaController.class.getName());
            }
        }
    }

    @FacesConverter("areaCon")
    public static class AreaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AreaController controller = (AreaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "areaController");
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
            if (object instanceof Area) {
                Area o = (Area) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + AreaController.class.getName());
            }
        }
    }
}
