/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.clinical;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

/**
 *
 * @author Buddhika
 */
@Entity
@Inheritance
public class ClinicalFindingItem extends ClinicalEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    
}
