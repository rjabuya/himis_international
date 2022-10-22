package com.kajabuyahmis.entity.inward;

import com.kajabuyahmis.entity.PatientEncounter;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


/**
 *
 * Dr M H B Ariyaratne
 *
 * @author buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Admission extends PatientEncounter implements Serializable {

    private static final long serialVersionUID = 1L;
}
