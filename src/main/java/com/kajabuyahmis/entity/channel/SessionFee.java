/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.channel;

import com.kajabuyahmis.entity.Fee;
import com.kajabuyahmis.entity.ServiceSession;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 *
 * @author buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SessionFee extends Fee implements Serializable {

    @ManyToOne
    ServiceSession session;

    public ServiceSession getSession() {
        return session;
    }

    public void setSession(ServiceSession session) {
        this.session = session;
    }
    
    
    
    private static final long serialVersionUID = 1L;
}
