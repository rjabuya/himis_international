/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.facade;

import com.kajabuyahmis.data.inward.RoomFacility;
import com.kajabuyahmis.entity.inward.RoomFacilityCharge;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Buddhika
 */
@Stateless
public class RoomFacilityChargeFacade extends AbstractFacade<RoomFacilityCharge> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        if(em == null){}return em;
    }

    public RoomFacilityChargeFacade() {
        super(RoomFacilityCharge.class);
    }
    
     public RoomFacility[] roomFacilityBySql(String sql) {
        Query q = getEntityManager().createQuery(sql);
        return (RoomFacility[]) q.getSingleResult();
    }
    
}
