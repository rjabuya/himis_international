/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.facade;

import com.kajabuyahmis.entity.BatchBill;
import com.kajabuyahmis.facade.util.JsfUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author safrin
 */
@Stateless
public class BatchBillFacade extends AbstractFacade<BatchBill> {

    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        if (em == null) {
            JsfUtil.addErrorMessage("null em");
        }
        if (em == null) {
        }
        return em;
    }

    public BatchBillFacade() {
        super(BatchBill.class);
    }

}
