/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.lab;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

/**
 *
 * @author Buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class InvestigationItem extends ReportItem implements Serializable {
   
    private static final long serialVersionUID = 1L;
    
    
    @OneToMany(mappedBy = "investigationItem", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    List<InvestigationItemValue> investigationItemValues;

    public List<InvestigationItemValue> getInvestigationItemValues() {
        return investigationItemValues;
    }

    public void setInvestigationItemValues(List<InvestigationItemValue> investigationItemValues) {
        this.investigationItemValues = investigationItemValues;
    }
    
    
    
    
    
}
