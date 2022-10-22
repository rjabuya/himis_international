package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.bean.channel.SheduleController;
import com.kajabuyahmis.entity.SessionNumberGenerator;
import com.kajabuyahmis.entity.Speciality;
import com.kajabuyahmis.facade.SessionNumberGeneratorFacade;
import java.io.Serializable;
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
public class SessionNumberGenerateConrtroller implements Serializable {

    @Inject
    SessionController sessionController;
    @Inject
    SheduleController sheduleController;
    @EJB
    SessionNumberGeneratorFacade sessionNumberGeneratorFacade;

    List<SessionNumberGenerator> SessionNumberGeneratorlst;

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public SessionNumberGeneratorFacade getSessionNumberGeneratorFacade() {
        return sessionNumberGeneratorFacade;
    }

    public void setSessionNumberGeneratorFacade(SessionNumberGeneratorFacade sessionNumberGeneratorFacade) {
        this.sessionNumberGeneratorFacade = sessionNumberGeneratorFacade;
    }

    public List<SessionNumberGenerator> getSessionNumberGeneratorlst() {
        return SessionNumberGeneratorlst;
    }

    public void setSessionNumberGeneratorlst(List<SessionNumberGenerator> SessionNumberGeneratorlst) {
        this.SessionNumberGeneratorlst = SessionNumberGeneratorlst;
    }

    public List<SessionNumberGenerator> completeSessionNumberGenerator(String qry) {

        String sql;
        Map m=new HashMap();
        sql = " SELECT sg FROM SessionNumberGenerator sg WHERE sg.retired=false"
                + " and upper(sg.name) like '%" + qry.toUpperCase() + "%' "
                + " and sg.speciality=:sp"
                + " and sg.staff=:s "
                + " order by sg.name";
        
        m.put("sp", sheduleController.getSpeciality());
        m.put("s", sheduleController.getCurrentStaff());

        SessionNumberGeneratorlst = sessionNumberGeneratorFacade.findBySQL(sql,m);

        return SessionNumberGeneratorlst;
    }

    /**
     *
     */
    @FacesConverter(forClass = SessionNumberGenerator.class)
    public static class SessionNumberGenerateConrtrollerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SessionNumberGenerateConrtroller controller = (SessionNumberGenerateConrtroller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sessionNumberGenerateConrtroller");
            return controller.getSessionNumberGeneratorFacade().find(getKey(value));
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
            if (object instanceof Speciality) {
                Speciality o = (Speciality) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + SessionNumberGenerateConrtroller.class.getName());
            }
        }
    }

    @FacesConverter("genConvert")
    public static class SpecialityConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SessionNumberGenerateConrtroller controller = (SessionNumberGenerateConrtroller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sessionNumberGenerateConrtroller");
            return controller.getSessionNumberGeneratorFacade().find(getKey(value));
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
            if (object instanceof SessionNumberGenerator) {
                SessionNumberGenerator o = (SessionNumberGenerator) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + SessionNumberGenerateConrtroller.class.getName());
            }
        }
    }

}
