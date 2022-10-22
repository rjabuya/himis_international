/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.FeeType;
import com.kajabuyahmis.entity.Department;
import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.ItemFee;
import com.kajabuyahmis.entity.Staff;
import com.kajabuyahmis.facade.DepartmentFacade;
import com.kajabuyahmis.facade.ItemFacade;
import com.kajabuyahmis.facade.ItemFeeFacade;
import com.kajabuyahmis.facade.StaffFacade;
import com.kajabuyahmis.facade.util.JsfUtil;
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
 * @author pasan
 */
@Named
@SessionScoped
public class ItemFeeManager implements Serializable {

    /**
     * Creates a new instance of ItemFeeManager
     */
    public ItemFeeManager() {
    }

    Item item;
    ItemFee itemFee;
    ItemFee removingFee;

    List<ItemFee> itemFees;

    @EJB
    ItemFeeFacade itemFeeFacade;
    @EJB
    ItemFacade itemFacade;
    @EJB
    DepartmentFacade departmentFacade;
    @EJB
    StaffFacade staffFacade;

    @Inject
    SessionController sessionController;

    List<Department> departments;
    List<Staff> staffs;

    
    public void fixIssueToReferralFees(){
        List<ItemFee> ifs = itemFeeFacade.findAll();
        for(ItemFee f:ifs){
            if(f.getFeeType()==FeeType.Issue){
                f.setFeeType(FeeType.Referral);
                f.setInstitution(f.getItem().getInstitution());
                f.setDepartment(f.getItem().getDepartment());
                itemFeeFacade.edit(f);
            }
        }
    }
    
    public ItemFee getRemovingFee() {
        return removingFee;
    }

    public void setRemovingFee(ItemFee removingFee) {
        this.removingFee = removingFee;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public void removeFee() {
        if (removingFee == null) {
            JsfUtil.addErrorMessage("Select a fee");
            return;
        }
        removingFee.setRetired(true);
        removingFee.setRetiredAt(new Date());
        removingFee.setRetirer(sessionController.getLoggedUser());
        itemFeeFacade.edit(removingFee);
        itemFees = null;
        fillFees();
        updateTotal();
        JsfUtil.addSuccessMessage("Removed");
    }

    public void fillDepartments() {
        ////// // System.out.println("fill dept");
        String jpql;
        Map m = new HashMap();
        m.put("ins", getItemFee().getInstitution());
        jpql = "select d from Department d where d.retired=false and d.institution=:ins order by d.name";
        ////// // System.out.println("m = " + m);
        ////// // System.out.println("jpql = " + jpql);
        departments = departmentFacade.findBySQL(jpql, m);
    }

    public void fillStaff() {
        ////// // System.out.println("fill staff");
        String jpql;
        Map m = new HashMap();
        m.put("ins", getItemFee().getSpeciality());
        jpql = "select d from Staff d where d.retired=false and d.speciality=:ins order by d.person.name";
        ////// // System.out.println("m = " + m);
        ////// // System.out.println("jpql = " + jpql);
        staffs = staffFacade.findBySQL(jpql, m);
    }

    public List<Department> compelteDepartments(String qry) {
        String jpql;
        if (qry == null) {
            return new ArrayList<>();
        }
        Map m = new HashMap();
        m.put("ins", getItemFee().getInstitution());
        m.put("name", "%" + qry.toUpperCase() + "%");
        jpql = "select d from Department d where d.retired=false and d.institution=:ins and d.name like :name order by d.name";
        return departmentFacade.findBySQL(jpql, m);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemFee getItemFee() {
        if (itemFee == null) {
            itemFee = new ItemFee();
        }
        return itemFee;
    }

    public void setItemFee(ItemFee itemFee) {
        this.itemFee = itemFee;
    }

    public List<ItemFee> getItemFees() {
        return itemFees;
    }

    public void setItemFees(List<ItemFee> itemFees) {
        this.itemFees = itemFees;
    }

    public void fillFees() {
        itemFees = fillFees(item);
    }

    public String toManageItemFees(){
        if(item==null){
            JsfUtil.addErrorMessage("Nothing Selected to Edit");
            return "";
        }
        fillFees();
        return "/common/manage_item_fees";
    }
    
    public List<ItemFee> fillFees(Item i) {
        String jpql;
        Map m = new HashMap();
        jpql = "select f from ItemFee f where f.retired=false and f.item=:i";
        m.put("i", i);
        return itemFeeFacade.findBySQL(jpql, m);
    }

    public void addNewFee() {
        if (item == null) {
            JsfUtil.addErrorMessage("Select Item ?");
            return;
        }
        getItemFee().setCreatedAt(new Date());
        getItemFee().setCreater(sessionController.getLoggedUser());
        itemFeeFacade.create(itemFee);

        getItemFee().setItem(item);
        itemFeeFacade.edit(itemFee);

        itemFee = new ItemFee();
        itemFees = null;
        fillFees();
        updateTotal();
        JsfUtil.addSuccessMessage("New Fee Added");
    }

    public void updateFee(ItemFee f) {
        itemFeeFacade.edit(f);
        updateTotal();
    }

    public void updateFee() {
        if (item == null) {
            return;
        }
        double t = 0.0;
        double tf =0.0;
        for (ItemFee f : itemFees) {
            t += f.getFee();
            tf+=f.getFfee();
            itemFeeFacade.edit(f);
        }
        getItem().setTotal(t);
        getItem().setTotalForForeigner(tf);
        itemFacade.edit(getItem());
    }
    
    
    public void updateTotal() {
        if (item == null) {
            return;
        }
        double t = 0.0;
        double tf =0.0;
        for (ItemFee f : itemFees) {
            t += f.getFee();
            tf+=f.getFfee();
        }
        getItem().setTotal(t);
        getItem().setTotalForForeigner(tf);
        itemFacade.edit(item);
    }
}
