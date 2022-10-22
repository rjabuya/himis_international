/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.hr;

import com.kajabuyahmis.data.hr.PaysheetComponentType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class BasicSalary extends PaysheetComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public PaysheetComponentType getComponentType() {
        return PaysheetComponentType.BasicSalary;
    }

    @Override
    public void setComponentType(PaysheetComponentType componentType) {
        this.componentType = PaysheetComponentType.BasicSalary;
    }
}
