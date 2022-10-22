/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.entity.Consultant;
import com.kajabuyahmis.entity.Person;
import com.kajabuyahmis.entity.Speciality;
import com.kajabuyahmis.facade.ConsultantFacade;
import com.kajabuyahmis.facade.PersonFacade;
import java.io.Serializable;
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
public class ConsultantController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private ConsultantFacade ejbFacade;
    @EJB
    private PersonFacade personFacade;
    List<Consultant> selectedItems;

    private Consultant current;
    private List<Consultant> items = null;
    String selectText = "";
    private Speciality speciality;

    public List<Consultant> getSelectedItems() {
        String sql;
        HashMap hm = new HashMap();
        if (selectText.trim().equals("")) {
            sql = "select c from Consultant c "
                    + " where c.retired=false ";

            if (speciality != null) {
                sql += " and c.speciality=:s ";
                hm.put("s", speciality);
            }
            sql += " order by c.codeInterger, c.person.name ";

        } else {
            sql = "select c from Consultant c "
                    + " where c.retired=false"
                    + " and upper(c.person.name) like :q ";

            sql += " and c.speciality=:s ";
            sql += " order by c.codeInterger , c.person.name ";
            hm.put("s", speciality);

            hm.put("q", "%" + getSelectText().toUpperCase() + "%");
        }
        selectedItems = getFacade().findBySQL(sql, hm);

        return selectedItems;
    }

    public void prepareAdd() {
        current = new Consultant();
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
        //  getItems();
        current = null;
        getCurrent();
    }

    public void createConsultantTable() {
        String sql;
        Map m = new HashMap();

        sql = "select c from Consultant c "
                + " where c.retired=false ";

        if (speciality != null) {
            sql += " and c.speciality=:s ";
            m.put("s", speciality);
        }

        sql += " order by c.codeInterger , c.person.name ";

        items = getFacade().findBySQL(sql, m);
        
    }

    public void setSelectedItems(List<Consultant> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        if (current == null) {
            UtilityController.addErrorMessage("Nothing to save");
            return;
        }
        if (current.getPerson() == null) {
            UtilityController.addErrorMessage("Nothing to save");
            return;
        }
        if (current.getPerson().getName().trim().equals("")) {
            UtilityController.addErrorMessage("Please Enter a Name");
            return;
        }
        if (current.getSpeciality() == null) {
            UtilityController.addErrorMessage("Please Select Speciality.");
            return;
        }
        if (current.getPerson().getId() == null || current.getPerson().getId() == 0) {
            getPersonFacade().create(current.getPerson());
        } else {
            getPersonFacade().edit(current.getPerson());
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
        // getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public ConsultantFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ConsultantFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public ConsultantController() {
    }

    public Consultant getCurrent() {
        if (current == null) {
            Person p = new Person();
            current = new Consultant();
            current.setPerson(p);
        }
        return current;
    }

    public void setCurrent(Consultant current) {
        this.current = current;
    }

    private ConsultantFacade getFacade() {
        return ejbFacade;
    }

    public List<Consultant> getItems() {
        if (items == null) {
            String temSql;
            temSql = "SELECT i FROM Consultant i where i.retired=false ";
            items = getFacade().findBySQL(temSql);
        }
        return items;
    }

    public PersonFacade getPersonFacade() {
        return personFacade;
    }

    public void setPersonFacade(PersonFacade personFacade) {
        this.personFacade = personFacade;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    /**
     *
     */
    @FacesConverter(forClass = Consultant.class)
    public static class ConsultantControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConsultantController controller = (ConsultantController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "consultantController");
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
            if (object instanceof Consultant) {
                Consultant o = (Consultant) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + ConsultantController.class.getName());
            }
        }
    }

}
