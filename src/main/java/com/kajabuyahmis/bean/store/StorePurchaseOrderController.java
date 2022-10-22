/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.store;

import com.kajabuyahmis.bean.common.SessionController;
import com.kajabuyahmis.bean.common.UtilityController;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillNumberSuffix;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.dataStructure.SearchKeyword;
import com.kajabuyahmis.ejb.BillNumberGenerator;
import com.kajabuyahmis.ejb.CommonFunctions;
import com.kajabuyahmis.entity.Bill;
import com.kajabuyahmis.entity.BillItem;
import com.kajabuyahmis.entity.BilledBill;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalBillItem;
import com.kajabuyahmis.facade.BillFacade;
import com.kajabuyahmis.facade.BillItemFacade;
import com.kajabuyahmis.facade.PharmaceuticalBillItemFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class StorePurchaseOrderController implements Serializable {

    @Inject
    private SessionController sessionController;
    @EJB
    private BillFacade billFacade;
    @EJB
    private PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade;
    @EJB
    private BillNumberGenerator billNumberBean;
    @EJB
    StoreBean storeBean;
    @EJB
    private BillItemFacade billItemFacade;
    ///////////////
    private Bill requestedBill;
    private Bill aprovedBill;
    private Date fromDate;
    Date toDate;
    private boolean printPreview;
    private String txtSearch;
    /////////////
//    private List<PharmaceuticalBillItem> pharmaceuticalBillItems;
    private List<PharmaceuticalBillItem> filteredValue;
    private List<BillItem> billItems;
    private List<BillItem> selectedItems;
    private List<Bill> billsToApprove;
    private List<Bill> bills;
    private SearchKeyword searchKeyword;
    // private List<BillItem> billItems;
    // List<PharmaceuticalBillItem> pharmaceuticalBillItems;
    //////////
    @EJB
    private CommonFunctions commonFunctions;
    private LazyDataModel<Bill> searchBills;

    public void removeSelected() {
        //  //System.err.println("1");
        if (selectedItems == null) {
            //   //System.err.println("2");
            return;
        }

        //System.err.println("3");
        for (BillItem b : selectedItems) {
            //  //System.err.println("4");
            getBillItems().remove(b.getSearialNo());
            calTotal();
        }

        selectedItems = null;
    }

    public void removeItem(BillItem billItem) {
        getBillItems().remove(billItem.getSearialNo());
        calTotal();
    }

    private int maxResult = 50;

   

    public void clearList() {
        filteredValue = null;
        billsToApprove = null;
        printPreview = false;
        billItems = null;
        aprovedBill = null;
        requestedBill = null;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = getCommonFunctions().getEndOfDay(new Date());
        }
        return toDate;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = getCommonFunctions().getStartOfDay(new Date());
        }
        return fromDate;
    }

    public void approve() {
        if (getAprovedBill().getPaymentMethod() == null) {
            UtilityController.addErrorMessage("Select Paymentmethod");
            return ;
        }

        calTotal();

        saveBill();
        saveBillComponent();

        //Update Requested Bill Reference
        getRequestedBill().setReferenceBill(getAprovedBill());
        getBillFacade().edit(getRequestedBill());

        clearList();

       // return viewRequestedList();
        //   printPreview = true;

    }

    public String viewRequestedList() {
        clearList();
        return "store_purhcase_order_list_to_approve";
    }

    @Inject
    private StoreController1 storeController1;

    public void onEdit(BillItem tmp) {
        tmp.setNetValue(tmp.getPharmaceuticalBillItem().getQty() * tmp.getPharmaceuticalBillItem().getPurchaseRate());
        calTotal();
    }

    public void onFocus(BillItem ph) {
        storeController1.setPharmacyItem(ph.getItem());
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public Bill getRequestedBill() {
        if (requestedBill == null) {
            requestedBill = new BilledBill();
        }
        return requestedBill;
    }

    public void saveBill() {

        getAprovedBill().setPaymentMethod(getRequestedBill().getPaymentMethod());
        getAprovedBill().setFromDepartment(getRequestedBill().getDepartment());
        getAprovedBill().setFromInstitution(getRequestedBill().getInstitution());
        getAprovedBill().setReferenceBill(getRequestedBill());
        getAprovedBill().setBackwardReferenceBill(getRequestedBill());

//        getAprovedBill().setDeptId(getBillNumberBean().institutionBillNumberGeneratorWithReference(getRequestedBill().getDepartment(), getAprovedBill(), BillType.StoreOrder, BillNumberSuffix.PO));
//        getAprovedBill().setInsId(getBillNumberBean().institutionBillNumberGeneratorWithReference(getRequestedBill().getInstitution(), getAprovedBill(), BillType.StoreOrder, BillNumberSuffix.PO));
        
        getAprovedBill().setDeptId(getBillNumberBean().institutionBillNumberGenerator(getRequestedBill().getDepartment(), BillType.StoreOrderApprove, BillClassType.BilledBill, BillNumberSuffix.PO));
        getAprovedBill().setInsId(getBillNumberBean().institutionBillNumberGenerator(getRequestedBill().getInstitution(), BillType.StoreOrderApprove, BillClassType.BilledBill, BillNumberSuffix.PO));

        getAprovedBill().setDepartment(getSessionController().getLoggedUser().getDepartment());
        getAprovedBill().setInstitution(getSessionController().getLoggedUser().getDepartment().getInstitution());

        getAprovedBill().setCreater(getSessionController().getLoggedUser());
        getAprovedBill().setCreatedAt(Calendar.getInstance().getTime());

        getBillFacade().create(getAprovedBill());

    }

    public void saveBillComponent() {
        for (BillItem i : getBillItems()) {
            i.setBill(getAprovedBill());
            i.setCreatedAt(Calendar.getInstance().getTime());
            i.setCreater(getSessionController().getLoggedUser());
            i.setNetValue(i.getPharmaceuticalBillItem().getQty() * i.getPharmaceuticalBillItem().getPurchaseRate());

            PharmaceuticalBillItem phItem = i.getPharmaceuticalBillItem();
            i.setPharmaceuticalBillItem(null);
            getBillItemFacade().create(i);

            phItem.setBillItem(i);
            getPharmaceuticalBillItemFacade().create(phItem);

            i.setPharmaceuticalBillItem(phItem);
            getBillItemFacade().edit(i);

            getAprovedBill().getBillItems().add(i);
        }

        getBillFacade().edit(getAprovedBill());
    }

    public void generateBillComponent() {

        for (PharmaceuticalBillItem i : getPharmaceuticalBillItemFacade().getPharmaceuticalBillItems(getRequestedBill())) {
            BillItem bi = new BillItem();
            bi.copy(i.getBillItem());

            PharmaceuticalBillItem ph = new PharmaceuticalBillItem();
            ph.setBillItem(bi);
            ph.setQtyInUnit(i.getQtyInUnit());
            ph.setPurchaseRateInUnit(i.getPurchaseRateInUnit());
            ph.setRetailRateInUnit(i.getRetailRateInUnit());
            bi.setPharmaceuticalBillItem(ph);

            bi.setTmpQty(ph.getQtyInUnit());

            getBillItems().add(bi);
        }

        calTotal();

    }

    public void setRequestedBill(Bill requestedBill) {
        clearList();
        this.requestedBill = requestedBill;
        getAprovedBill().setPaymentMethod(getRequestedBill().getPaymentMethod());
        getAprovedBill().setToInstitution(getRequestedBill().getToInstitution());
        generateBillComponent();
    }

    public Bill getAprovedBill() {
        if (aprovedBill == null) {
            aprovedBill = new BilledBill();
            aprovedBill.setBillType(BillType.StoreOrderApprove);
        }
        return aprovedBill;
    }

    public void setAprovedBill(Bill aprovedBill) {
        this.aprovedBill = aprovedBill;
    }

    public PharmaceuticalBillItemFacade getPharmaceuticalBillItemFacade() {
        return pharmaceuticalBillItemFacade;
    }

    public void setPharmaceuticalBillItemFacade(PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade) {
        this.pharmaceuticalBillItemFacade = pharmaceuticalBillItemFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public StoreBean getStoreBean() {
        return storeBean;
    }

    public void setStoreBean(StoreBean storeBean) {
        this.storeBean = storeBean;
    }
    
    public void calTotal() {
        double tmp = 0;
        int serialNo = 0;
        for (BillItem bi : getBillItems()) {
            tmp += bi.getPharmaceuticalBillItem().getQty() * bi.getPharmaceuticalBillItem().getPurchaseRate();
            bi.setSearialNo(serialNo++);
        }
        getAprovedBill().setTotal(tmp);
        getAprovedBill().setNetTotal(tmp);
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public List<PharmaceuticalBillItem> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<PharmaceuticalBillItem> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public List<Bill> getBillsToApprove() {
        return billsToApprove;
    }

    public void setBillsToApprove(List<Bill> billsToApprove) {
        this.billsToApprove = billsToApprove;
    }

  
    public boolean getPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public void makeListNull() {
//        pharmaceuticalBillItems = null;
        filteredValue = null;
        billsToApprove = null;
        searchBills = null;
        billItems = null;
        bills = null;
        maxResult = 50;

    }

    public LazyDataModel<Bill> getSearchBills() {
        return searchBills;
    }

    public void setSearchBills(LazyDataModel<Bill> searchBills) {
        this.searchBills = searchBills;
    }

    public String getTxtSearch() {
        return txtSearch;
    }

    public void setTxtSearch(String txtSearch) {
        this.txtSearch = txtSearch;
    }

    public List<BillItem> getBillItems() {
        if (billItems == null) {
            billItems = new ArrayList<>();
        }
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    public List<BillItem> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<BillItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public SearchKeyword getSearchKeyword() {
        if (searchKeyword == null) {
            searchKeyword = new SearchKeyword();
        }
        return searchKeyword;
    }

    public void setSearchKeyword(SearchKeyword searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }
}
