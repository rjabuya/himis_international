/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.clinical;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.SymanticType;
import com.kajabuyahmis.entity.clinical.ClinicalFindingItem;
import com.kajabuyahmis.facade.ClinicalFindingItemFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class TreatementController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private ClinicalFindingItemFacade ejbFacade;
    List<ClinicalFindingItem > selectedItems;
    private ClinicalFindingItem current;
    private List<ClinicalFindingItem> items = null;
    List<ClinicalFindingItem> insItems =null;
    String selectText = "";

    public List<ClinicalFindingItem> completeTreatments(String qry) {
        List<ClinicalFindingItem> c;
        Map m = new HashMap();
        m.put("t", SymanticType.Pharmacologic_Substance);
        m.put("n", "%" + qry.toUpperCase() + "%");
        String sql;
        sql="select c from ClinicalFindingItem c where c.retired=false and upper(c.name) like :n and c.symanticType=:t order by c.name";
        c = getFacade().findBySQL(sql,m,10);
        if (c == null) {
            c = new ArrayList<>();
        }
        return c;
    }

    public List<ClinicalFindingItem> getSelectedItems() {
        Map m = new HashMap();
        m.put("t", SymanticType.Pharmacologic_Substance);
        m.put("n", "%" + getSelectText().toUpperCase() + "%");
        String sql;
        sql="select c from ClinicalFindingItem c where c.retired=false and upper(c.name) like :n and c.symanticType=:t order by c.name";
        selectedItems = getFacade().findBySQL(sql,m);
        return selectedItems;
    }

    public void prepareAdd() {
        current = new ClinicalFindingItem();
        current.setInstitution(sessionController.getInstitution());
        current.setSymanticType(SymanticType.Pharmacologic_Substance);
        //TODO:
    }

    public void setSelectedItems(List<ClinicalFindingItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        current.setSymanticType(SymanticType.Pharmacologic_Substance);
        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Saved");
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Updates");
        }
        recreateModel();
        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public ClinicalFindingItemFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ClinicalFindingItemFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public TreatementController() {
    }

    public ClinicalFindingItem getCurrent() {
        if (current == null) {
            current = new ClinicalFindingItem();
        }
        return current;
    }

    public void setCurrent(ClinicalFindingItem current) {
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

    private ClinicalFindingItemFacade getFacade() {
        return ejbFacade;
    }

    public List<ClinicalFindingItem> getItems() {
        if (items == null) {
            Map m = new HashMap();
            m.put("t", SymanticType.Pharmacologic_Substance);
            String sql;
            sql = "select c from ClinicalFindingItem c where c.retired=false and c.symanticType=:t order by c.name";
            items = getFacade().findBySQL(sql, m);
        }
        return items;
    }

    public List<ClinicalFindingItem> getInsItems() {
        if (insItems == null) {
            Map m = new HashMap();
            m.put("t", SymanticType.Pharmacologic_Substance);
            m.put("ins", sessionController.getInstitution());
            String sql;
            sql = "select c "
                    + " from ClinicalFindingItem c "
                    + " where c.retired=false "
                    + " and c.symanticType=:t "
                    + " and c.institution=:ins "
                    + " order by c.name";
            insItems = getFacade().findBySQL(sql, m);
        }
        return insItems;
    }

    public void setInsItems(List<ClinicalFindingItem> insItems) {
        this.insItems = insItems;
    }
    
    
}
