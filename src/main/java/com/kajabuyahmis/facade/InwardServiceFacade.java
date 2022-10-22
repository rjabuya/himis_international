/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */

package com.kajabuyahmis.facade;

import com.kajabuyahmis.entity.inward.InwardService;
import com.kajabuyahmis.facade.util.JsfUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author safrin
 */
@Stateless
public class InwardServiceFacade extends AbstractFacade<InwardService> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        if(em == null){
            JsfUtil.addErrorMessage("Null em");
        }
        if(em == null){}return em;
    }

    public InwardServiceFacade() {
        super(InwardService.class);
    }
    
}
