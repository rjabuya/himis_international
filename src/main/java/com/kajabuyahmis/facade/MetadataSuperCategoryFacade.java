/*
 * Author
 * Dr. M H B Ariyaratne, MO(Health Information), email : buddhika.ari@gmail.com
 */
package com.kajabuyahmis.facade;

import com.kajabuyahmis.entity.MetadataSuperCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dr. M H B Ariyaratne <buddhika.ari at gmail.com>
 */
@Stateless
public class MetadataSuperCategoryFacade extends AbstractFacade<MetadataSuperCategory> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        if(em == null){}return em;
    }

    public MetadataSuperCategoryFacade() {
        super(MetadataSuperCategory.class);
    }
    
}
