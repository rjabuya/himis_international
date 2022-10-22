/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.pharmacy.ItemsDistributors;
import com.kajabuyahmis.facade.InstitutionFacade;
import com.kajabuyahmis.facade.ItemFacade;
import com.kajabuyahmis.facade.ItemsDistributorsFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 Informatics)
 */
@Named
@SessionScoped
public class XItemsDistributorsController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private ItemsDistributorsFacade ejbFacade;
    List<ItemsDistributors> selectedItems;
    @EJB
    private InstitutionFacade institutionFacade;
    @EJB
    ItemFacade itemFacade;
    private ItemsDistributors current;
    private Institution institution;
    Item item;

    public Item getItem() {
        if (item == null) {
            item = new Item();
        }
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        current = new ItemsDistributors();
    }
    String selectText = "";

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public void setItemFacade(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    public void prepareAdd() {
        current = new ItemsDistributors();
    }

    public void setSelectedItems(List<ItemsDistributors> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        institution = null;
        item = null;
    }

    public void saveSelected() {
        current.setInstitution(getInstitution());
        current.setItem(getItem());
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

        current = new ItemsDistributors();
        current.setInstitution(getInstitution());
        current.setItem(getItem());



    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public ItemsDistributorsFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsDistributorsFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public XItemsDistributorsController() {
    }

    public ItemsDistributors getCurrent() {
        if (current == null) {
            current = new ItemsDistributors();
        }
        return current;
    }

    public void setCurrent(ItemsDistributors current) {
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
        current = null;
        getCurrent();
    }

    private ItemsDistributorsFacade getFacade() {
        return ejbFacade;
    }

//    public List<Institution> getItems() {
//        List<Institution> tmp;
//        String sql = "SELECT i.institution FROM ItemsDistributors i where i.retired=false  order by i.institution.name";
//        tmp = getInstitutionFacade().findBySQL(sql);
//
//        if (tmp == null) {
//            tmp = new ArrayList<Institution>();
//        }
//
//        return tmp;
//    }
    public List<Item> getItemForInstitution() {
        if (getInstitution().getId() == null) {
            return new ArrayList<Item>();
        }


        String sql = "SELECT i.item FROM ItemsDistributors i where i.retired=false and i.institution.id=" + getInstitution().getId() + "  order by i.item.name";
        List<Item> tmp = getItemFacade().findBySQL(sql);
        if (tmp == null) {
            tmp = new ArrayList<Item>();
        }
        return tmp;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public Institution getInstitution() {
        if (institution == null) {
            institution = new Institution();
        }
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        item = new Item();
        current = new ItemsDistributors();

    }
    /**
     *
     */
}
