/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.CancelledBill;
import com.kajabuyahmis.entity.Institution;
import com.kajabuyahmis.entity.RefundBill;
import com.kajabuyahmis.facade.BillFacade;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.TemporalType;

/**
 *
 * @author Buddhika
 */
@Named(value = "billNumberController")
@ApplicationScoped
public class BillNumberController {

    @EJB
    BillFacade billFacade;
    Institution institution;
    Class billClass;

    /**
     * Creates a new instance of BillNumberController
     */
    public BillNumberController() {
    }

    Long channelBillCount = null;

    public void findInstitutionChannelBillCount(Institution ins, Bill bill) {
        BillType[] billTypes = {BillType.ChannelAgent, BillType.ChannelCash, BillType.ChannelOnCall, BillType.ChannelStaff};
        List<BillType> bts = Arrays.asList(billTypes);
        String sql = "SELECT count(b) FROM Bill b"
                + "  where type(b)=:type "
                + " and b.retired=false"
                + " AND b.institution=:ins "
                + " and b.billType in :bt "
                + " and b.createdAt is not null";
        HashMap hm = new HashMap();
        hm.put("ins", ins);
        hm.put("bt", bts);
        hm.put("type", bill.getClass());
        channelBillCount = getBillFacade().findAggregateLong(sql, hm, TemporalType.DATE);
    }

    public String institutionChannelBillNumberGenerator(Institution ins, Bill bill) {
        String result = "";
        Long i = channelBillCount;
        if (channelBillCount == null) {
            findInstitutionChannelBillCount(ins, bill);
        } else if (institution!=ins) {
            findInstitutionChannelBillCount(ins, bill);
            institution = ins;
        } else if (bill.getClass().equals(billClass)) {
            findInstitutionChannelBillCount(ins, bill);
            billClass = bill.getClass();
        } else {
            channelBillCount++;
        }
        String suffix = "";

        if (bill instanceof BilledBill) {
            suffix = "CHANN";
        }

        if (bill instanceof CancelledBill) {
            suffix = "CHANNCAN";
        }

        if (bill instanceof RefundBill) {
            suffix = "CHANNREF";
        }

        if (i != null) {
            result = ins.getInstitutionCode() + suffix + "/" + (i + 1);
        } else {
            result = ins.getInstitutionCode() + suffix + "/" + 1;
        }

        return result;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

}
