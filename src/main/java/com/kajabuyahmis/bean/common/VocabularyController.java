/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.entity.Vocabulary;
import com.kajabuyahmis.facade.VocabularyFacade;
import java.io.Serializable;
import java.util.ArrayList;
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
public class VocabularyController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private VocabularyFacade ejbFacade;
    List<Vocabulary> selectedItems;
    private Vocabulary current;
    private List<Vocabulary> items = null;
    String selectText = "";

    public List<Vocabulary> completeVocabulary(String qry) {
        List<Vocabulary> c;
        c = getFacade().findBySQL("select c from Vocabulary c where c.retired=false and upper(c.name) like '%" + qry.toUpperCase() + "%' order by c.name");
        if (c == null) {
            c = new ArrayList<>();
        }
        return c;
    }

    public List<Vocabulary> getSelectedItems() {
        selectedItems = getFacade().findBySQL("select c from Vocabulary c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        return selectedItems;
    }

    public void prepareAdd() {
        current = new Vocabulary();
    }

    public void setSelectedItems(List<Vocabulary> selectedItems) {
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

    public VocabularyFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(VocabularyFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public VocabularyController() {
    }

    public Vocabulary getCurrent() {
        if (current == null) {
            current = new Vocabulary();
        }
        return current;
    }

    public void setCurrent(Vocabulary current) {
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

    private VocabularyFacade getFacade() {
        return ejbFacade;
    }

    public List<Vocabulary> getItems() {
        if (items == null) {
            String j ;
            j="select v from Vocabulary v where v.retired=false order by v.name";
            items = getFacade().findBySQL(j);
        }
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = Vocabulary.class)
    public static class VocabularyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VocabularyController controller = (VocabularyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vocabularyController");
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
            if (object instanceof Vocabulary) {
                Vocabulary o = (Vocabulary) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + VocabularyController.class.getName());
            }
        }
    }

    /**
     *
     */
    @FacesConverter("vocabularyConverter")
    public static class VocabularyConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VocabularyController controller = (VocabularyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vocabularyController");
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
            if (object instanceof Vocabulary) {
                Vocabulary o = (Vocabulary) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + VocabularyController.class.getName());
            }
        }
    }
}
