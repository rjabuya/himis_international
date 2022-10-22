/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.hr;

import com.kajabuyahmis.entity.Category;
import com.kajabuyahmis.entity.Staff;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class StaffCategory extends Category implements Serializable {
   
    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "staffCategory")
    private List<Staff> staffs;
    

  

    @XmlTransient
    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

  
    
}
