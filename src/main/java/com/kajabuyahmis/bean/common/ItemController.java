package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.DepartmentType;
import com.kajabuyahmis.data.FeeType;
import com.kajabuyahmis.data.ItemType;
import com.kajabuyahmis.data.hr.ReportKeyWord;
import com.kajabuyahmis.entity.BillExpense;
import com.kajabuyahmis.entity.Category;
import com.kajabuyahmis.entity.Department;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.ItemFee;
import com.kajabuyahmis.entity.Packege;
import com.kajabuyahmis.entity.Service;
import com.kajabuyahmis.entity.ServiceCategory;
import com.kajabuyahmis.entity.ServiceSubCategory;
import com.kajabuyahmis.entity.inward.InwardService;
import com.kajabuyahmis.entity.inward.TheatreService;
import com.kajabuyahmis.entity.lab.Investigation;
import com.kajabuyahmis.entity.lab.ItemForItem;
import com.kajabuyahmis.entity.lab.Machine;
import com.kajabuyahmis.entity.pharmacy.Amp;
import com.kajabuyahmis.entity.pharmacy.Ampp;
import com.kajabuyahmis.entity.pharmacy.Vmp;
import com.kajabuyahmis.entity.pharmacy.Vmpp;
import com.kajabuyahmis.facade.ItemFacade;
import com.kajabuyahmis.facade.ItemFeeFacade;
import com.kajabuyahmis.facade.util.JsfUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class ItemController implements Serializable {

    /**
     * EJBs
     */
    private static final long serialVersionUID = 1L;
    @EJB
    private ItemFacade ejbFacade;
    @EJB
    private ItemFeeFacade itemFeeFacade;
    /**
     * Managed Beans
     */
    @Inject
    SessionController sessionController;
    @Inject
    ItemFeeManager itemFeeManager;
    @Inject
    DepartmentController departmentController;
    @Inject
    ItemForItemController itemForItemController;
    @Inject
    ItemFeeController itemFeeController;
    @Inject
    ServiceController serviceController;

    /**
     * Properties
     */
    private Item current;
    private Item sampleComponent;
    private List<Item> items = null;
    private List<Item> investigationsAndServices = null;
    private List<Item> itemlist;
    List<Item> allItems;
    List<ItemFee> allItemFees;
    List<Item> selectedList;
    List<ItemFee> selectedItemFeeList;
    private Institution instituion;
    Department department;
    FeeType feeType;
    List<Department> departments;
    private Machine machine;
    private List<Item> machineTests;
    private List<Item> investigationSampleComponents;

    ReportKeyWord reportKeyWord;

    public void fillInvestigationSampleComponents() {
        if (current == null) {
            JsfUtil.addErrorMessage("Select an investigation");
            return;
        }
        if (current instanceof Investigation) {
            investigationSampleComponents = findInvestigationSampleComponents((Investigation) current);
            if (investigationSampleComponents != null && investigationSampleComponents.size() > 1) {
                current.setHasMoreThanOneComponant(true);
                getFacade().edit(current);
            }
        } else {
            investigationSampleComponents = null;
        }
    }

    public List<Item> getInvestigationSampleComponents(Item ix) {
        if (ix == null) {
            JsfUtil.addErrorMessage("Select an investigation");
            return null;
        }
        if (ix instanceof Investigation) {
            return findInvestigationSampleComponents((Investigation) ix);
        }
        return null;
    }

    public Item getFirstInvestigationSampleComponents(Item ix) {
        if (ix == null) {
            JsfUtil.addErrorMessage("Select an investigation");
            return null;
        }
        if (ix instanceof Investigation) {
            List<Item> is = findInvestigationSampleComponents((Investigation) ix);
            if (is != null && !is.isEmpty()) {
                return is.get(0);
            } else {
                Item sc = new Item();
                sc.setParentItem(ix);
                sc.setItemType(ItemType.SampleComponent);
                sc.setCreatedAt(new Date());
                sc.setCreater(sessionController.getLoggedUser());
                sc.setName(ix.getName());
                getFacade().create(sc);
                return sc;
            }
        }
        return null;
    }

    public List<Item> findInvestigationSampleComponents(Investigation ix) {
        if (ix == null) {
            JsfUtil.addErrorMessage("Select an investigation");
            return null;
        }
        String j = "select i from Item i where i.itemType=:t and i.parentItem=:m and i.retired=:r order by i.name";
        Map m = new HashMap();
        m.put("t", ItemType.SampleComponent);
        m.put("r", false);
        m.put("m", ix);
        return getFacade().findBySQL(j, m);
    }

    public void removeSampleComponent() {
        if (sampleComponent == null) {
            JsfUtil.addErrorMessage("Select one to delete");
            return;
        }
        sampleComponent.setRetired(true);
        sampleComponent.setRetirer(sessionController.getLoggedUser());
        sampleComponent.setRetiredAt(new Date());
        getFacade().edit(sampleComponent);
        fillInvestigationSampleComponents();
        JsfUtil.addSuccessMessage("Removed");
    }

    public void toCreateSampleComponent() {
        if (current == null) {
            JsfUtil.addErrorMessage("Select an investigation");
            return;
        }
        sampleComponent = new Item();
        sampleComponent.setParentItem(current);
        sampleComponent.setItemType(ItemType.SampleComponent);
        sampleComponent.setCreatedAt(new Date());
        sampleComponent.setCreater(sessionController.getLoggedUser());
    }

    public void createOrUpdateSampleComponent() {
        if (sampleComponent == null) {
            JsfUtil.addErrorMessage("Select one to delete");
            return;
        }
        sampleComponent.setParentItem(current);
        sampleComponent.setItemType(ItemType.SampleComponent);

        if (sampleComponent.getId() == null) {
            getFacade().create(sampleComponent);
            JsfUtil.addSuccessMessage("Added");
        } else {
            getFacade().edit(sampleComponent);
            JsfUtil.addSuccessMessage("Updated");
        }
        fillInvestigationSampleComponents();
    }

    public void addSampleComponentsForAllInvestigationsWithoutSampleComponents() {
        String j = "select ix from Investigation ix ";
        List<Item> ixs = getFacade().findBySQL(j);
        for (Item ix : ixs) {
            if (ix instanceof Investigation) {
                Investigation tix = (Investigation) ix;
                List<Item> scs = findInvestigationSampleComponents(tix);
                if (scs == null || scs.isEmpty()) {
                    sampleComponent = new Item();
                    sampleComponent.setName(tix.getName());
                    sampleComponent.setParentItem(tix);
                    sampleComponent.setItemType(ItemType.SampleComponent);
                    sampleComponent.setCreatedAt(new Date());
                    sampleComponent.setCreater(sessionController.getLoggedUser());
                    getFacade().create(sampleComponent);
                } else {
                    if (scs.size() > 1) {
                        tix.setHasMoreThanOneComponant(true);
                        getFacade().edit(tix);
                    } else {
                        tix.setHasMoreThanOneComponant(false);
                        getFacade().edit(tix);
                    }
                }
            }
        }
        JsfUtil.addSuccessMessage("Added");
    }

    public void fillMachineTests() {
        if (machine == null) {
            JsfUtil.addErrorMessage("Select a machine");
            return;
        }
        String j = "select i from Item i where i.itemType=:t and i.machine=:m and i.retired=:r order by i.code";
        Map m = new HashMap();
        m.put("t", ItemType.AnalyzerTest);
        m.put("m", machine);
        m.put("r", false);
        machineTests = getFacade().findBySQL(j, m);
    }

    public List<Item> completeMachineTests(String qry) {
        List<Item> ts;
        String j = "select i from Item i where i.itemType=:t and (lower(i.name) like :m or lower(i.name) like :m ) and i.retired=:r order by i.code";
        Map m = new HashMap();
        m.put("t", ItemType.AnalyzerTest);
        m.put("m", "%" + qry.toLowerCase() + "%");
        m.put("r", false);
        ts = getFacade().findBySQL(j, m);
        return ts;
    }

    public void removeTest() {
        if (current == null) {
            JsfUtil.addErrorMessage("Select one to delete");
            return;
        }
        current.setRetired(true);
        current.setRetirer(sessionController.getLoggedUser());
        current.setRetiredAt(new Date());
        getFacade().edit(current);
        fillMachineTests();
        JsfUtil.addSuccessMessage("Removed");
    }

    public void toCreateNewTest() {
        if (machine == null) {
            JsfUtil.addErrorMessage("Select a machine");
            return;
        }
        current = new Item();
        current.setItemType(ItemType.AnalyzerTest);
        current.setCreatedAt(new Date());
        current.setCreater(sessionController.getLoggedUser());
    }

    public void createOrUpdateTest() {
        if (current == null) {
            JsfUtil.addErrorMessage("Select one to delete");
            return;
        }
        current.setMachine(machine);
        current.setInstitution(machine.getInstitution());
        current.setItemType(ItemType.AnalyzerTest);

        if (current.getId() == null) {
            getFacade().create(current);
            JsfUtil.addSuccessMessage("Added");
        } else {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Updated");
        }
        fillMachineTests();
    }

    public void refreshInvestigationsAndServices() {
        investigationsAndServices = null;
        getInvestigationsAndServices();
        for (Item i : getInvestigationsAndServices()) {
            i.getItemFeesAuto();
        }
    }

    public void createItemFessForItemsWithoutFee() {
        if (selectedList == null || selectedList.isEmpty()) {
            JsfUtil.addErrorMessage("Nothing is selected");
            return;
        }
        for (Item i : selectedList) {
            if (i.getItemFeesAuto() == null || i.getItemFeesAuto().isEmpty()) {
                ItemFee itf = new ItemFee();
                itf.setName("Fee");
                itf.setItem(i);
                itf.setInstitution(i.getInstitution());
                itf.setDepartment(i.getDepartment());
                itf.setFeeType(FeeType.OwnInstitution);
                itf.setFee(0.0);
                itf.setFfee(0.0);
                itemFeeManager.setItemFee(itf);
                itemFeeManager.setItem(i);
                itemFeeManager.addNewFee();
            }
        }
    }

    public void updateSelectedItemFees() {
        if (selectedList == null || selectedList.isEmpty()) {
            JsfUtil.addErrorMessage("Nothing is selected");
            return;
        }
        for (Item i : selectedList) {
            if (!(i.getItemFeesAuto() == null) && !(i.getItemFeesAuto().isEmpty())) {
                double t = 0.0;
                double tf = 0.0;
                for (ItemFee itf : i.getItemFeesAuto()) {
                    getItemFeeFacade().edit(itf);
                    t += itf.getFee();
                    tf += itf.getFfee();
                }
                i.setTotal(t);
                i.setTotalForForeigner(tf);
                getFacade().edit(i);
            }
        }
        investigationsAndServices = null;
        getInvestigationsAndServices();
    }

    public void updateSelectedFeesForDiscountAllow() {
        if (selectedList == null || selectedList.isEmpty()) {
            JsfUtil.addErrorMessage("Nothing is selected");
            return;
        }
        for (Item i : selectedList) {
            if (!(i.getItemFeesAuto() == null) && !(i.getItemFeesAuto().isEmpty())) {
                for (ItemFee itf : i.getItemFeesAuto()) {
                    if (itf.getFeeType() == FeeType.OwnInstitution) {
                        itf.setDiscountAllowed(true);
                    } else {
                        itf.setDiscountAllowed(false);
                    }
                    getItemFeeFacade().edit(itf);
                }
                getFacade().edit(i);
            }
        }
        investigationsAndServices = null;
        getInvestigationsAndServices();
    }

    public List<Department> getDepartments() {
        departments = departmentController.getInstitutionDepatrments(instituion);
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void createNewItemsFromMasterItems() {
        ////// // System.out.println("createNewItemsFromMasterItems");
        if (instituion == null) {
            JsfUtil.addErrorMessage("Select institution");
            return;
        }
        if (department == null) {
            JsfUtil.addErrorMessage("Select department");
            return;
        }
        if (selectedList == null || selectedList.isEmpty()) {
            JsfUtil.addErrorMessage("Select Items");
            return;
        }
        for (Item i : selectedList) {
            ////// // System.out.println("i.getName() = " + i.getName());
            Item ni = null;
            if (i instanceof Investigation) {
                try {
                    ni = new Investigation();
                    BeanUtils.copyProperties(ni, i);

                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (i instanceof Service) {
                try {
                    ni = new Service();
                    BeanUtils.copyProperties(ni, i);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                continue;
            }
            if (ni == null) {
                continue;
            }
            ni.setId(null);
            ni.setInstitution(instituion);
            ni.setDepartment(department);
            ni.setItemFee(null);
            getFacade().create(ni);
            i.setItemFees(itemFeeManager.fillFees(i));
            ////// // System.out.println("ni = " + ni);
            ////// // System.out.println("i.getItemFees() = " + i.getItemFees());
            ////// // System.out.println("ni.getItemFees() = " + ni.getItemFees());

            for (ItemFee f : i.getItemFees()) {
                ItemFee nf = new ItemFee();
                ////// // System.out.println("f = " + f);
                try {
                    BeanUtils.copyProperties(nf, f);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (nf.getInstitution() != null) {
                    nf.setInstitution(instituion);
                }
                if (nf.getDepartment() != null) {
                    nf.setDepartment(department);
                }
                nf.setId(null);
                nf.setItem(ni);
                ni.getItemFees().add(nf);
                getItemFeeFacade().create(nf);
                ////// // System.out.println("nf = " + nf);
            }
            getFacade().edit(ni);
            List<Item> ifis = itemForItemController.getItemsForParentItem(i);
            if (ifis != null) {
                for (Item ifi : ifis) {
                    ItemForItem ifin = new ItemForItem();
                    ifin.setParentItem(ni);
                    ifin.setChildItem(ifi);
                    ifin.setCreatedAt(new Date());
                    ifin.setCreater(getSessionController().getLoggedUser());
                }
            }
            ////// // System.out.println("ni.getItemFees() = " + ni.getItemFees());
        }
    }

    public void updateItemsFromMasterItems() {
        if (instituion == null) {
            JsfUtil.addErrorMessage("Select institution");
            return;
        }
        if (department == null) {
            JsfUtil.addErrorMessage("Select department");
            return;
        }
        if (selectedList == null || selectedList.isEmpty()) {
            JsfUtil.addErrorMessage("Select Items");
            return;
        }

        for (Item i : selectedList) {
            if (i.getDepartment() != null) {
                i.setDepartment(department);
            }

            if (i.getInstitution() != null) {
                i.setInstitution(instituion);
            }
            getFacade().edit(i);
        }

        selectedList = null;

    }

    public void updateItemsAndFees() {
        if (instituion == null) {
            JsfUtil.addErrorMessage("Select institution");
            return;
        }
        if (department == null) {
            JsfUtil.addErrorMessage("Select department");
            return;
        }
        if (selectedItemFeeList == null || selectedItemFeeList.isEmpty()) {
            JsfUtil.addErrorMessage("Select Items");
            return;
        }

        for (ItemFee fee : selectedItemFeeList) {
            if (fee.getDepartment() != null) {
                fee.setDepartment(department);
            }

            if (fee.getInstitution() != null) {
                fee.setInstitution(instituion);
            }
            getItemFeeFacade().edit(fee);
        }

        selectedItemFeeList = null;
    }

    public List<Item> completeDealorItem(String query) {
        List<Item> suggestions;
        String sql;
        HashMap hm = new HashMap();
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select c.item from ItemsDistributors c"
                    + " where c.retired=false "
                    + " and c.item.retired=false "
                    + " and c.institution=:ins and (upper(c.item.name) like :q or "
                    + " upper(c.item.barcode) like :q or upper(c.item.code) like :q )order by c.item.name";
            hm.put("ins", getInstituion());
            hm.put("q", "%" + query + "%");
            //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql, hm, 20);
        }
        return suggestions;

    }

    public List<Item> getDealorItem() {
        List<Item> suggestions;
        String sql;
        HashMap hm = new HashMap();

        sql = "select c.item from ItemsDistributors c where c.retired=false "
                + " and c.institution=:ins "
                + " order by c.item.name";
        hm.put("ins", getInstituion());

        //////// // System.out.println(sql);
        suggestions = getFacade().findBySQL(sql, hm);

        return suggestions;

    }

    public List<Item> completeItem(String query, Class[] itemClasses, DepartmentType[] departmentTypes, int count) {
        String sql;
        List<Item> lst;
        HashMap tmpMap = new HashMap();
        if (query == null) {
            lst = new ArrayList<>();
        } else {
            sql = "select c "
                    + " from Item c "
                    + " where c.retired=false ";

            if (departmentTypes != null) {
                sql += " and c.departmentType in :deps ";
                tmpMap.put("deps", Arrays.asList(departmentTypes));
            }

            if (itemClasses != null) {
                sql += " and type(c) in :types ";
                tmpMap.put("types", Arrays.asList(itemClasses));
            }

            sql += " and (upper(c.name) like :q or upper(c.code) like :q or upper(c.barcode) like :q  ) ";
            tmpMap.put("q", "%" + query.toUpperCase() + "%");

            sql += " order by c.name";

            if (count != 0) {
                lst = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, count);
            } else {
                lst = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP);
            }
        }
        return lst;
    }

    public List<Item> completeItem(String query) {
        return completeItem(query, null, null, 20);
//        List<Item> suggestions;
//        String sql;
//        HashMap hm = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//            sql = "select c from Item c "
//                    + " where c.retired=false"
//                    + "  and (upper(c.name) like :q"
//                    + "  or upper(c.barcode) like :q"
//                    + "  or upper(c.code) like :q )"
//                    + " order by c.name";
//            hm.put("q", "%" + query.toUpperCase() + "%");
////////// // System.out.println(sql);
//            suggestions = getFacade().findBySQL(sql, hm, 20);
//        }
//        return suggestions;
//
    }

    List<Item> itemList;

    List<Item> suggestions;

    public List<Item> completeMedicine(String query) {
        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Pharmacy, null};
        Class[] classes = new Class[]{Vmp.class, Amp.class, Vmp.class, Amp.class, Vmpp.class, Ampp.class};
        return completeItem(query, classes, dts, 0);
    }

    public List<Item> completeItem(String query, Class[] itemClasses, DepartmentType[] departmentTypes) {
        return completeItem(query, itemClasses, departmentTypes, 0);
    }

    public List<Item> completeAmpItem(String query) {
//        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Pharmacy, null};
//        Class[] classes = new Class[]{Amp.class};
//        return completeItem(query, classes, dts, 30);
//        
        String sql;
        HashMap tmpMap = new HashMap();
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {

            sql = "select c from Item c where c.retired=false "
                    + " and (type(c)= :amp) and "
                    + " ( c.departmentType is null or c.departmentType!=:dep ) "
                    + " and (upper(c.name) like :str or upper(c.code) like :str or"
                    + " upper(c.barcode) like :str ) order by c.name";
            //////// // System.out.println(sql);
            tmpMap.put("dep", DepartmentType.Store);
            tmpMap.put("amp", Amp.class);
            tmpMap.put("str", "%" + query.toUpperCase() + "%");
            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
        }
        return suggestions;

    }

    public List<Item> completeAmpItemAll(String query) {
//        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Pharmacy, null};
//        Class[] classes = new Class[]{Amp.class};
//        return completeItem(query, classes, dts, 0);
        String sql;
        HashMap tmpMap = new HashMap();
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {

            sql = "select c from Item c where "
                    + " (type(c)= :amp) and "
                    + " ( c.departmentType is null or c.departmentType!=:dep ) "
                    + " and (upper(c.name) like :str or upper(c.code) like :str or"
                    + " upper(c.barcode) like :str ) order by c.name";
            //////// // System.out.println(sql);
            tmpMap.put("dep", DepartmentType.Store);
            tmpMap.put("amp", Amp.class);
            tmpMap.put("str", "%" + query.toUpperCase() + "%");
            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
        }
        return suggestions;

    }

    public List<Item> completeStoreItem(String query) {
        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Store, DepartmentType.Inventry};
        Class[] classes = new Class[]{Amp.class};
        return completeItem(query, classes, dts, 0);
//        String sql;
//        HashMap tmpMap = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//
//            sql = "select c from Item c "
//                    + "where c.retired=false and "
//                    + "(type(c)= :amp) "
//                    + "and (c.departmentType=:dep or c.departmentType=:inven )"
//                    + "and (upper(c.name) like :str or "
//                    + "upper(c.code) like :str or "
//                    + "upper(c.barcode) like :str) "
//                    + "order by c.name";
//            //////// // System.out.println(sql);
//            tmpMap.put("amp", Amp.class);
//            tmpMap.put("dep", DepartmentType.Store);
//            tmpMap.put("inven", DepartmentType.Inventry);
//            tmpMap.put("str", "%" + query.toUpperCase() + "%");
//            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
//        }
//        return suggestions;
//
    }

    public List<Item> completeStoreInventryItem(String query) {
        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Inventry};
        Class[] classes = new Class[]{Amp.class};
        return completeItem(query, classes, dts, 0);
//        String sql;
//        HashMap tmpMap = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//
//            sql = "select c from Item c "
//                    + "where c.retired=false and "
//                    + "(type(c)= :amp) "
//                    + "and c.departmentType=:dep "
//                    + "and (upper(c.name) like :str or "
//                    + "upper(c.code) like :str or "
//                    + "upper(c.barcode) like :str) "
//                    + "order by c.name";
//            //////// // System.out.println(sql);
//            tmpMap.put("amp", Amp.class);
//            tmpMap.put("dep", DepartmentType.Inventry);
//            tmpMap.put("str", "%" + query.toUpperCase() + "%");
//            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
//        }
//        return suggestions;

    }

    public List<Item> completeStoreItemOnly(String query) {
        DepartmentType[] dts = new DepartmentType[]{DepartmentType.Store};
        Class[] classes = new Class[]{Amp.class};
        return completeItem(query, classes, dts, 0);
//        String sql;
//        HashMap tmpMap = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//
//            sql = "select c from Item c "
//                    + "where c.retired=false and "
//                    + "(type(c)= :amp) "
//                    + "and c.departmentType=:dep "
//                    + "and (upper(c.name) like :str or "
//                    + "upper(c.code) like :str or "
//                    + "upper(c.barcode) like :str) "
//                    + "order by c.name";
//            //////// // System.out.println(sql);
//            tmpMap.put("amp", Amp.class);
//            tmpMap.put("dep", DepartmentType.Store);
//            tmpMap.put("str", "%" + query.toUpperCase() + "%");
//            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
//        }
//        return suggestions;
//
    }

    public List<Item> completeExpenseItem(String query) {
        Class[] classes = new Class[]{BillExpense.class};
        return completeItem(query, classes, null, 0);
//        String sql;
//        HashMap tmpMap = new HashMap();
//        if (query == null) {
//            suggestions = new ArrayList<>();
//        } else {
//            sql = "select c from Item c "
//                    + "where c.retired=false and "
//                    + "(type(c)= :amp) "
//                    + "and (upper(c.name) like :str or "
//                    + "upper(c.code) like :str or "
//                    + "upper(c.barcode) like :str) "
//                    + "order by c.name";
//            //////// // System.out.println(sql);
//            tmpMap.put("amp", BillExpense.class);
//            tmpMap.put("str", "%" + query.toUpperCase() + "%");
//            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
//        }
//        return suggestions;
    }

    public List<Item> fetchStoreItem() {
        List<Item> suggestions;
        String sql;
        HashMap tmpMap = new HashMap();

        sql = "select c from Item c"
                + "  where c.retired=false and "
                + " (type(c)= :amp) "
                + " and c.departmentType=:dep "
                + " order by c.name";
        //////// // System.out.println(sql);
        tmpMap.put("amp", Amp.class);
        tmpMap.put("dep", DepartmentType.Store);

        suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP);

        return suggestions;

    }

    public List<Item> completeAmpAndAmppItem(String query) {
        List<Item> suggestions;
        String sql;
        HashMap tmpMap = new HashMap();
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            if (query.length() > 4) {
                sql = "select c from Item c where c.retired=false and (type(c)= :amp or type(c)=:ampp ) and (upper(c.name) like '%" + query.toUpperCase() + "%' or upper(c.code) like '%" + query.toUpperCase() + "%' or upper(c.barcode) like '%" + query.toUpperCase() + "%') order by c.name";
            } else {
                sql = "select c from Item c where c.retired=false and (type(c)= :amp or type(c)=:ampp ) and (upper(c.name) like '%" + query.toUpperCase() + "%' or upper(c.code) like '%" + query.toUpperCase() + "%') order by c.name";
            }

//////// // System.out.println(sql);
            tmpMap.put("amp", Amp.class);
            tmpMap.put("ampp", Ampp.class);
            suggestions = getFacade().findBySQL(sql, tmpMap, TemporalType.TIMESTAMP, 30);
        }
        return suggestions;

    }

    public List<Item> completePackage(String query) {
        List<Item> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select c from Item c where c.retired=false"
                    + " and (c.inactive=false or c.inactive is null) "
                    + "and type(c)=Packege "
                    + "and upper(c.name) like '%" + query.toUpperCase() + "%' order by c.name";
            //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql);
        }
        return suggestions;

    }

    public List<Item> completeService(String query) {
        List<Item> suggestions;
        String sql;
        HashMap hm = new HashMap();

        sql = "select c from Item c where c.retired=false and type(c)=:cls"
                + " and upper(c.name) like :q order by c.name";

        hm.put("cls", Service.class);
        hm.put("q", "%" + query.toUpperCase() + "%");
        suggestions = getFacade().findBySQL(sql, hm, 20);

        return suggestions;

    }

    public List<Item> completeInvestigation(String query) {
        List<Item> suggestions;
        String sql;
        HashMap hm = new HashMap();

        sql = "select c from Item c where c.retired=false and type(c)=:cls"
                + " and upper(c.name) like :q order by c.name";

        hm.put("cls", Investigation.class);
        hm.put("q", "%" + query.toUpperCase() + "%");
        suggestions = getFacade().findBySQL(sql, hm, 20);

        return suggestions;

    }

    public List<Item> completeLoggedInstitutionInvestigation(String query) {
        List<Item> lst;
        String sql;
        HashMap hm = new HashMap();
        sql = "select c from Item c "
                + " where c.retired=false "
                + " and type(c)=:cls "
                + " and upper(c.name) like :q "
                + " and c.institution=:ins "
                + " order by c.name";
        hm.put("cls", Investigation.class);
        hm.put("q", "%" + query.toUpperCase() + "%");
        hm.put("ins", sessionController.getLoggedUser().getInstitution());
        lst = getFacade().findBySQL(sql, hm, 20);
        return lst;
    }

    public List<Item> completeServiceWithoutProfessional(String query) {
        List<Item> lst;
        String sql;
        HashMap hm = new HashMap();

        sql = "select c from Item c where c.retired=false and type(c)=:cls"
                + " and upper(c.name) like :q "
                + " c.id in (Select f.item.id From Itemfee f where f.retired=false "
                + " and f.feeType!=:ftp ) order by c.name ";

        hm.put("ftp", FeeType.Staff);
        hm.put("cls", Service.class);
        hm.put("q", "%" + query.toUpperCase() + "%");
        lst = getFacade().findBySQL(sql, hm, 20);
        return lst;
    }

    public List<Item> completeMedicalPackage(String query) {
        List<Item> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select c from Item c where c.retired=false "
                    + " and (c.inactive=false or c.inactive is null) "
                    + "and type(c)=MedicalPackage "
                    + "and upper(c.name) like '%" + query.toUpperCase() + "%' order by c.name";
            //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql);
        }
        return suggestions;

    }

    public List<Item> completeInwardItems(String query) {
        List<Item> suggestions;
        HashMap m = new HashMap();
        String sql;
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select c from Item c "
                    + " where c.retired=false "
                    + " and type(c)!=:pac "
                    + " and (type(c)=:ser "
                    + " or type(c)=:inv"
                    + " or type(c)=:ward "
                    + " or type(c)=:the)  "
                    + " and upper(c.name) like :q"
                    + " order by c.name";
            m.put("pac", Packege.class);
            m.put("ser", Service.class);
            m.put("inv", Investigation.class);
            m.put("ward", InwardService.class);
            m.put("the", TheatreService.class);
            m.put("q", "%" + query.toUpperCase() + "%");
            //    //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql, m, 20);
        }
        return suggestions;
    }

    public List<Item> completeTheatreItems(String query) {
        List<Item> suggestions;
        HashMap m = new HashMap();
        String sql;
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select c from Item c "
                    + " where c.retired=false "
                    + " and type(c)=:the "
                    //                    + " and type(c)!=:pac "
                    //                    + " and (type(c)=:ser "
                    //                    + " or type(c)=:inv "
                    //                    + " or type(c)=:the)  "
                    + " and upper(c.name) like :q"
                    + " order by c.name";
//            m.put("pac", Packege.class);
//            m.put("ser", Service.class);
//            m.put("inv", Investigation.class);
            m.put("the", TheatreService.class);
            m.put("q", "%" + query.toUpperCase() + "%");
            //    //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql, m, 20);
        }
        return suggestions;
    }

    Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private List<Item> fetchInwardItems(String query, Department department) {
        HashMap m = new HashMap();
        String sql;
        sql = "select c from Item c "
                + " where c.retired=false "
                + " and type(c)!=:pac "
                + " and (type(c)=:ser "
                + " or type(c)=:inward "
                + " or type(c)=:inv) "
                + " and (c.inactive=false or c.inactive is null) "
                + " and upper(c.name) like :q";
        if (department != null) {
            sql += " and c.department=:dep ";
            m.put("dep", getReportKeyWord().getDepartment());
        }
        sql += " order by c.name";
        m.put("pac", Packege.class);
        m.put("ser", Service.class);
        m.put("inward", InwardService.class);
        m.put("inv", Investigation.class);
        m.put("q", "%" + query.toUpperCase() + "%");

        return getFacade().findBySQL(sql, m, 20);

    }

    private List<Item> fetchInwardItems(String query, Category cat, Department department) {
        HashMap m = new HashMap();
        String sql;
        sql = "select c from Item c "
                + " where c.retired=false "
                + " and c.category=:ct"
                + " and type(c)!=:pac "
                + " and (type(c)=:ser "
                + " or type(c)=:inward "
                + " or type(c)=:inv) "
                + " and (c.inactive=false or c.inactive is null) "
                + " and upper(c.name) like :q";
        if (department != null) {
            sql += " and c.department=:dep ";
            m.put("dep", getReportKeyWord().getDepartment());
        }
        sql += " order by c.name";
        m.put("ct", cat);
        m.put("pac", Packege.class);
        m.put("ser", Service.class);
        m.put("inv", Investigation.class);
        m.put("inward", InwardService.class);
        m.put("q", "%" + query.toUpperCase() + "%");

        return getFacade().findBySQL(sql, m, 20);

    }

    @Inject
    ServiceSubCategoryController serviceSubCategoryController;

    public ServiceSubCategoryController getServiceSubCategoryController() {
        return serviceSubCategoryController;
    }

    public void setServiceSubCategoryController(ServiceSubCategoryController serviceSubCategoryController) {
        this.serviceSubCategoryController = serviceSubCategoryController;
    }

    public List<Item> completeInwardItemsCategory(String query) {
        List<Item> suggestions = new ArrayList<>();

        if (category == null) {
            suggestions = fetchInwardItems(query, null);
        } else if (category instanceof ServiceCategory) {
            suggestions = fetchInwardItems(query, category, null);
            getServiceSubCategoryController().setParentCategory(category);
            for (ServiceSubCategory ssc : getServiceSubCategoryController().getItems()) {
                suggestions.addAll(fetchInwardItems(query, ssc, null));
            }
        } else {
            suggestions = fetchInwardItems(query, category, null);
        }

        return suggestions;
    }

    public List<Item> completeInwardItemsCategoryNew(String query) {
        List<Item> suggestions = new ArrayList<>();

        if (category == null) {
            suggestions = fetchInwardItems(query, getReportKeyWord().getDepartment());
        } else if (category instanceof ServiceCategory) {
            suggestions = fetchInwardItems(query, category, getReportKeyWord().getDepartment());
            getServiceSubCategoryController().setParentCategory(category);
            for (ServiceSubCategory ssc : getServiceSubCategoryController().getItems()) {
                suggestions.addAll(fetchInwardItems(query, ssc, getReportKeyWord().getDepartment()));
            }
        } else {
            suggestions = fetchInwardItems(query, category, getReportKeyWord().getDepartment());
        }

        return suggestions;
    }

    public List<Item> completeOpdItemsByNamesAndCode(String query) {
        if (sessionController.getLoggedPreference().isInstitutionRestrictedBilling()) {
            return completeOpdItemsByNamesAndCodeInstitutionSpecificOrNotSpecific(query, true);
        } else {
            return completeOpdItemsByNamesAndCodeInstitutionSpecificOrNotSpecific(query, false);
        }
    }

    public void makeItemsAsActiveOrInactiveByRetiredStatus() {
        String j = "select i from Item i";
        List<Item> tis = getFacade().findBySQL(j);
        for (Item i : tis) {
            if (i.isRetired()) {
                i.setInactive(true);
            } else {
                i.setInactive(false);
            }
            getFacade().edit(i);
        }
    }
    
     public void toggleItemIctiveInactiveState() {
        String j = "select i from Item i";
        List<Item> tis = getFacade().findBySQL(j);
        for (Item i : tis) {
            if (i.isInactive()) {
                i.setInactive(false);
            } else {
                i.setInactive(true);
            }
            getFacade().edit(i);
        }
    }

    public List<Item> completeOpdItemsByNamesAndCodeInstitutionSpecificOrNotSpecific(String query, boolean spcific) {
        if (query == null || query.trim().equals("")) {
            return new ArrayList<>();
        }
        List<Item> mySuggestions;
        HashMap m = new HashMap();
        String sql;

        sql = "select c from Item c "
                + " where c.retired<>true "
                + " and (c.inactive<>true) "
                + " and type(c)!=:pac "
                + " and type(c)!=:inw "
                + " and (type(c)=:ser "
                + " or type(c)=:inv)  "
                + " and (upper(c.name) like :q or upper(c.fullName) like :q or "
                + " upper(c.code) like :q or upper(c.printName) like :q ) ";
        if (spcific) {
            sql += " and c.institution=:ins";
            m.put("ins", getSessionController().getInstitution());
        }
        if (getReportKeyWord().getDepartment() != null) {
            sql += " and c.department=:dep";
            m.put("dep", getReportKeyWord().getDepartment());
        }
        sql += " order by c.name";
        m.put("pac", Packege.class);
        m.put("inw", InwardService.class);
        m.put("ser", Service.class);
        m.put("inv", Investigation.class);
        m.put("q", "%" + query.toUpperCase() + "%");

//        //// // System.out.println(sql);
//        //// // System.out.println("m = " + m);
        mySuggestions = getFacade().findBySQL(sql, m, 20);
//        //// // System.out.println("mySuggestions = " + mySuggestions);
        return mySuggestions;
    }

    public List<Item> completeOpdItems(String query) {
        List<Item> mySuggestions;
        HashMap m = new HashMap();
        String sql;
        if (query == null) {
            mySuggestions = new ArrayList<>();
        } else {
            sql = "select c from Item c "
                    + " where c.retired=false "
                    + " and type(c)!=:pac "
                    + " and type(c)!=:inw "
                    + " and (type(c)=:ser "
                    + " or type(c)=:inv)  "
                    + " and upper(c.name) like :q"
                    + " order by c.name";
            m.put("pac", Packege.class);
            m.put("inw", InwardService.class);
            m.put("ser", Service.class);
            m.put("inv", Investigation.class);
            m.put("q", "%" + query.toUpperCase() + "%");
            //    //////// // System.out.println(sql);
            mySuggestions = getFacade().findBySQL(sql, m, 20);
        }
        return mySuggestions;
    }

    public List<Item> completeItemWithoutPackOwn(String query) {
        List<Item> suggestions;
        String sql;
        if (query == null) {
            suggestions = new ArrayList<Item>();
        } else {
            sql = "select c from Item c where c.institution.id = " + getSessionController().getInstitution().getId() + " and c.retired=false and type(c)!=Packege and type(c)!=TimedItem and upper(c.name) like '%" + query.toUpperCase() + "%' order by c.name";
            //////// // System.out.println(sql);
            suggestions = getFacade().findBySQL(sql);
        }
        return suggestions;
    }

    public void makeSelectedAsMasterItems() {
        for (Item i : selectedList) {
            ////// // System.out.println("i = " + i.getInstitution());
            if (i.getInstitution() != null) {
                ////// // System.out.println("i = " + i.getInstitution().getName());
                i.setInstitution(null);
                getFacade().edit(i);
            }
        }
    }

    public List<Item> fetchOPDItemList(boolean ins) {
        List<Item> items = new ArrayList<>();
        HashMap m = new HashMap();
        String sql;

        sql = "select c from Item c "
                + " where c.retired=false "
                + " and type(c)!=:pac "
                + " and type(c)!=:inw "
                + " and (type(c)=:ser "
                + " or type(c)=:inv)  ";

        if (ins) {
            sql += " and c.institution is null ";
        }

        sql += " order by c.name";

        m.put("pac", Packege.class);
        m.put("inw", InwardService.class);
        m.put("ser", Service.class);
        m.put("inv", Investigation.class);
        ////// // System.out.println(sql);
        items = getFacade().findBySQL(sql, m);
        return items;
    }

    public List<ItemFee> fetchOPDItemFeeList(boolean ins, FeeType ftype) {
        List<ItemFee> itemFees = new ArrayList<>();
        HashMap m = new HashMap();
        String sql;

        sql = "select c from ItemFee c "
                + " where c.retired=false "
                + " and type(c.item)!=:pac "
                + " and type(c.item)!=:inw "
                + " and (type(c.item)=:ser "
                + " or type(c.item)=:inv)  ";

        if (ftype != null) {
            sql += " and c.feeType=:fee ";
            m.put("fee", ftype);
        }

        if (ins) {
            sql += " and c.institution is null ";
        }

        sql += " order by c.name";

        m.put("pac", Packege.class);
        m.put("inw", InwardService.class);
        m.put("ser", Service.class);
        m.put("inv", Investigation.class);
        ////// // System.out.println(sql);
        itemFees = getItemFeeFacade().findBySQL(sql, m);
        return itemFees;
    }

    public void createMasterItemsList() {
        allItems = new ArrayList<>();
        allItems = fetchOPDItemList(false);
    }

    public void createAllItemsList() {
        allItems = new ArrayList<>();
        allItems = fetchOPDItemList(true);
    }

    public void createAllItemsFeeList() {
        allItemFees = new ArrayList<>();
        allItemFees = fetchOPDItemFeeList(false, feeType);
    }

    public void updateSelectedOPDItemList() {

    }

    public void createOpdSeviceInvestgationList() {
        itemlist = getItems();
        for (Item i : itemlist) {
            List<ItemFee> tmp = serviceController.getFees(i);
            for (ItemFee itf : tmp) {
                i.setItemFee(itf);
                if (itf.getFeeType() == FeeType.OwnInstitution) {
                    i.setHospitalFee(i.getHospitalFee() + itf.getFee());
                    i.setHospitalFfee(i.getHospitalFfee() + itf.getFfee());
                } else if (itf.getFeeType() == FeeType.Staff) {
                    i.setProfessionalFee(i.getProfessionalFee() + itf.getFee());
                    i.setProfessionalFfee(i.getProfessionalFfee() + itf.getFfee());
                }
            }
        }
    }

    public void createInwardList() {
        itemlist = getInwardItems();
        for (Item i : itemlist) {
            List<ItemFee> tmp = serviceController.getFees(i);
            for (ItemFee itf : tmp) {
                i.setItemFee(itf);
                if (itf.getFeeType() == FeeType.OwnInstitution) {
                    i.setHospitalFee(i.getHospitalFee() + itf.getFee());
                    i.setHospitalFfee(i.getHospitalFfee() + itf.getFfee());
                } else if (itf.getFeeType() == FeeType.Staff) {
                    i.setProfessionalFee(i.getProfessionalFee() + itf.getFee());
                    i.setProfessionalFfee(i.getProfessionalFfee() + itf.getFfee());
                }
            }
        }
    }

    /**
     *
     */
    public ItemController() {
    }

    /**
     * Prepare to add new Collecting Centre
     *
     * @return
     */
    public void prepareAdd() {
        current = new Item();
    }

    /**
     *
     * @return
     */
    public ItemFacade getEjbFacade() {
        return ejbFacade;
    }

    /**
     *
     * @param ejbFacade
     */
    public void setEjbFacade(ItemFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    /**
     *
     * @return
     */
    public SessionController getSessionController() {
        return sessionController;
    }

    /**
     *
     * @param sessionController
     */
    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    /**
     * Return the current item
     *
     * @return
     */
    public Item getCurrent() {
        if (current == null) {
            current = new Item();
        }
        return current;
    }

    /**
     * Set the current item
     *
     * @param current
     */
    public void setCurrent(Item current) {
        this.current = current;
    }

    private ItemFacade getFacade() {
        return ejbFacade;
    }

    /**
     *
     * @return
     */
    public List<Item> getItems() {
        if(items==null){
            fillItemsWithInvestigationsAndServices();
        }
        return items;
    }

    
    public void fillItemsWithInvestigationsAndServices(){
        String temSql;
        HashMap h = new HashMap();
        temSql = "SELECT i FROM Item i where (type(i)=:t1 or type(i)=:t2 ) and i.retired=false order by i.department.name";
        h.put("t1", Investigation.class);
        h.put("t2", Service.class);
        items = getFacade().findBySQL(temSql, h, TemporalType.TIME);
    }
    
    public List<Item> getInwardItems() {
        String temSql;
        HashMap h = new HashMap();
        temSql = "SELECT i FROM Item i where type(i)=:t1 and i.retired=false order by i.department.name";
        h.put("t1", InwardService.class);
        items = getFacade().findBySQL(temSql, h, TemporalType.TIME);
        return items;
    }

    public List<Item> getItems(Category category) {
        String temSql;
        HashMap h = new HashMap();
        temSql = "SELECT i FROM Item i where i.category=:cat and i.retired=false order by i.name";
        h.put("cat", category);
        return getFacade().findBySQL(temSql, h);
    }

    /**
     *
     * Set all Items to null
     *
     */
    private void recreateModel() {
        items = null;
    }

    /**
     *
     */
    public void saveSelected() {
        saveSelected(getCurrent());
        JsfUtil.addSuccessMessage("Saved");
        recreateModel();
        getItems();
    }

    public void saveSelected(Item item) {
        if (item.getId() != null && item.getId() > 0) {
            getFacade().edit(item);
        } else {
            item.setCreatedAt(new Date());
            item.setCreater(getSessionController().getLoggedUser());
            getFacade().create(item);
        }
    }

    /**
     *
     * Delete the current Item
     *
     */
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

    public Institution getInstituion() {
        if (instituion == null) {
            instituion = getSessionController().getInstitution();
        }
        return instituion;
    }

    public void setInstituion(Institution instituion) {
        this.instituion = instituion;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public List<Item> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<Item> selectedList) {
        this.selectedList = selectedList;
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public void setAllItems(List<Item> allItems) {
        this.allItems = allItems;
    }

    public List<ItemFee> getAllItemFees() {
        return allItemFees;
    }

    public void setAllItemFees(List<ItemFee> allItemFees) {
        this.allItemFees = allItemFees;
    }

    public List<ItemFee> getSelectedItemFeeList() {
        return selectedItemFeeList;
    }

    public void setSelectedItemFeeList(List<ItemFee> selectedItemFeeList) {
        this.selectedItemFeeList = selectedItemFeeList;
    }

    public Department getDepartment() {
        if (department == null) {
            department = getSessionController().getDepartment();
        }
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Item> suggestions) {
        this.suggestions = suggestions;
    }

    public ItemFeeFacade getItemFeeFacade() {
        return itemFeeFacade;
    }

    public void setItemFeeFacade(ItemFeeFacade itemFeeFacade) {
        this.itemFeeFacade = itemFeeFacade;
    }

    public ItemFeeManager getItemFeeManager() {
        return itemFeeManager;
    }

    public void setItemFeeManager(ItemFeeManager itemFeeManager) {
        this.itemFeeManager = itemFeeManager;
    }

    public List<Item> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<Item> itemlist) {
        this.itemlist = itemlist;
    }

    public ReportKeyWord getReportKeyWord() {
        if (reportKeyWord == null) {
            reportKeyWord = new ReportKeyWord();
        }
        return reportKeyWord;
    }

    public void setReportKeyWord(ReportKeyWord reportKeyWord) {
        this.reportKeyWord = reportKeyWord;
    }

    public List<Item> getInvestigationsAndServices() {
        if (investigationsAndServices == null) {
            String temSql;
            HashMap h = new HashMap();
            temSql = "SELECT i FROM Item i where (type(i)=:t1 or type(i)=:t2 ) and i.retired=false order by i.department.name";
            h.put("t1", Investigation.class);
            h.put("t2", Service.class);
            investigationsAndServices = getFacade().findBySQL(temSql, h, TemporalType.TIME);
        }
        return investigationsAndServices;
    }

    public void setInvestigationsAndServices(List<Item> investigationsAndServices) {
        this.investigationsAndServices = investigationsAndServices;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public List<Item> getMachineTests() {
        return machineTests;
    }

    public void setMachineTests(List<Item> machineTests) {
        this.machineTests = machineTests;
    }

    public List<Item> getInvestigationSampleComponents() {
        return investigationSampleComponents;
    }

    public void setInvestigationSampleComponents(List<Item> investigationSampleComponents) {
        this.investigationSampleComponents = investigationSampleComponents;
    }

    public Item getSampleComponent() {
        return sampleComponent;
    }

    public void setSampleComponent(Item sampleComponent) {
        this.sampleComponent = sampleComponent;
    }

    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key = 0l;
            try {
                key = Long.valueOf(value);
            } catch (Exception e) {

            }

            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + ItemController.class.getName());
            }
        }
    }

    /**
     *
     */
    @FacesConverter("itemcon")
    public static class ItemConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key = 0l;
            try {
                key = Long.valueOf(value);
            } catch (Exception e) {

            }

            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + ItemController.class.getName());
            }
        }
    }
}
