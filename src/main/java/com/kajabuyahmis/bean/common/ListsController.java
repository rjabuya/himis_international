/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

//import ch.lambdaj.Lambda;
import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.pharmacy.Amp;
import com.kajabuyahmis.entity.pharmacy.Ampp;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalItem;
import com.kajabuyahmis.entity.pharmacy.Vmp;
import com.kajabuyahmis.entity.pharmacy.Vmpp;
import com.kajabuyahmis.facade.ItemFacade;
import com.kajabuyahmis.facade.PharmaceuticalItemFacade;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Buddhika
 */
@Named(value = "listsController")
@ApplicationScoped
public class ListsController {

    /**
     * Creates a new instance of ListsController
     */
    public ListsController() {
    }

    List<Item> items;
    List<PharmaceuticalItem> pharmaceuticalItems;
    @EJB
    ItemFacade itemFacade;
    @EJB
    PharmaceuticalItemFacade pharmaceuticalItemFacade;
    

    List<PharmaceuticalItem> ampsAndAmpp;

    public List<PharmaceuticalItem> getAmpsAndAmpp() {
        if (ampsAndAmpp == null) {
            String sql;
            sql = "select i from Item i where i.retired=false and (type(i)=:it1 or type(i)=:it2) order by i.name";
            Map m = new HashMap();
            m.put("it1", Amp.class);
            m.put("it2", Ampp.class);
            ampsAndAmpp = getPharmaceuticalItemFacade().findBySQL(sql, m);
        }
        return ampsAndAmpp;
    }

    public void setAmpsAndAmpp(List<PharmaceuticalItem> ampsAndAmpp) {
        this.ampsAndAmpp = ampsAndAmpp;
    }

    public List<Item> getItems() {
        if (items == null) {
            String sql;
            sql = "select i from Item i where i.retired=false order by i.name";
            items = getItemFacade().findBySQL(null);
        }
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    

    List<Item> pharmacyItems;
    
    public List<Item> getPharmacyItem() {
        if (pharmacyItems == null) {
            String sql;
            HashMap tmpMap = new HashMap();
            sql = "select c from Item c where c.retired=false and (type(c)= :amp or type(c)= :ampp or type(c)= :vmp or type(c)= :vmpp) order by c.name";
            //////// // System.out.println(sql);
            tmpMap.put("amp", Amp.class);
            tmpMap.put("ampp", Ampp.class);
            tmpMap.put("vmp", Vmp.class);
            tmpMap.put("vmpp", Vmpp.class);
            pharmacyItems = getItemFacade().findBySQL(sql, tmpMap);
        }
        return pharmacyItems;
    }

    
    public List<PharmaceuticalItem> getPharmaceuticalItems() {
        return pharmaceuticalItems;
    }

    public void setPharmaceuticalItems(List<PharmaceuticalItem> pharmaceuticalItems) {
        this.pharmaceuticalItems = pharmaceuticalItems;
    }

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public void setItemFacade(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    public PharmaceuticalItemFacade getPharmaceuticalItemFacade() {
        return pharmaceuticalItemFacade;
    }

    public void setPharmaceuticalItemFacade(PharmaceuticalItemFacade pharmaceuticalItemFacade) {
        this.pharmaceuticalItemFacade = pharmaceuticalItemFacade;
    }

}
