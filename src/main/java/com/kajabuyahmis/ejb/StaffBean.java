/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */

package com.kajabuyahmis.ejb;

import com.kajabuyahmis.entity.Staff;
import com.kajabuyahmis.facade.StaffFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pdhs
 */
@Stateless
public class StaffBean {

    @EJB
    StaffFacade facade;
    
    public void updateStaffCredit(Staff staff, double value) {
        if(staff==null || staff.getId()==null){
         //   ////// // System.out.println("Staff Null or Not previously persisted.");
            return;
        }
        staff.setAnnualWelfareUtilized(staff.getAnnualWelfareUtilized() + value);
        getFacade().edit(staff);
    }

    public StaffFacade getFacade() {
        return facade;
    }

    public void setFacade(StaffFacade facade) {
        this.facade = facade;
    }

    
    
    
    
}
