/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */

package com.kajabuyahmis.facade;

import com.kajabuyahmis.entity.pharmacy.StoreItemCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author safrin
 */
@Stateless
public class StoreItemCategoryFacade extends AbstractFacade<StoreItemCategory> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        if(em == null){}return em;
    }

    public StoreItemCategoryFacade() {
        super(StoreItemCategory.class);
    }
    
}
