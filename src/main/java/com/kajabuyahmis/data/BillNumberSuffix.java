/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data;

/**
 *
 * @author safrin
 */
public enum BillNumberSuffix {

    RF,//Refund
    CAN,//Cancel
    //Professional Payment
    PROPAY,//Professional Payment
    PROCAN,//Professional Cancel
    CHNPROPAY,//Channel Pro Pay
    //Pharmacy
    GRN,//Pharmacy GRN
    GRNCAN,//GRN Cancel
    GRNRET,//GRN Return
    PHDIRRET,//Pharmacy Return to dealer Without traising
    GRNRETCAN,//GRN Return Cancel
    PO,//Purchase Order
    POCAN,//PO Cancel
    POR,//Purchase Order Request
    PORCAN,//Purchase Order Request Cancell
    PHCAN,//Pharmacy Cancel    
    PHPUR,//Purchase  
    PURRET,//Purchase Return
    PURCAN,//Purchase Cancel
    PHTI,//Transfer Issue
    PHTICAN,//Trnsfer Issue Cancel
    PHTR,//Transfer Recieve
    PHTRCAN,//Transfer Receive Cancel
    PHTRQ,//Transfer Request
    //PHPRE,//Pre Bill
    PHSAL,//Before Error Sale
    SALE,//After Error
    SALCAN,//Pharmacy Sale Cancel
    PRECAN,//Pre Cancel
    @Deprecated
    BHTISSUE,//Bht Issue
    @Deprecated
    BHTISSUECAN,//Bht Issue Cancel
    PHISSUE,//Pharmacy Issue
    PHISSUEREQ,//Pharmacy Issue Request
    PHISSCAN,//Pharmacy Issue Cancel
    PHISSRET,//Pharmacy Issue Return
    STISSUE,//Store Issue
    STTISSUECAN,//Store Issue can
    PHRET,
    RETCAN,//Sale Return Cancel
    //PHPRERET,//Pre BIll Return
    // PHSAL,//Sale Bill
    //  PRERET,//Pre Return
    // SALRET,//Sal Return
    MJADJ,//Major Adjustment
    ADJ,//Adjustment
    //Inward
    INWPAY,//Payment Bill
    INWFINAL,//Inward Final
    INWINTRIM,//Inward Intrim    
    INWPRO,//Professional
    INWSER,//Service
    INWREF,//Refund
    INWREFCAN,//Refund Cancel
    INWCAN,//Cancell
    //Agent
    AGNPAY,//Payment 
    AGNCAN,//Payment Cancel
    AGNCN,//Credit Note 
    AGNCNCAN,//Credit Note Cancel
    AGNDN,//Debit Note 
    AGNDNCAN,//Debit Note Cancel
    //Collecting Centre
    CCPAY,//Payment 
    CCCAN,//Payment Cancel
    CCCN,//Credit Note 
    CCCNCAN,//Credit Note Cancel
    CCDN,//Debit Note 
    CCDNCAN,//Debit Note Cancel
    //Credit Company
    CRDPAY,//Payment
    CRDCAN,//Cancel
    //Petty Cash
    PTYPAY,//Payment
    PTYCAN,//Cancell
    NONE,//NO Prefix
    DRADJ,//Drawer Adjustment
    PACK,//Package
    SURG,//Surger Bill
    TIME,//Timed Service 
    CSIN,//Cashier Cash Out
    CSINCAN,//Cash In Cancel
    CSOUT,//Cashier CAsh IN
    CSOUTCAN,//Cash Out Cancel
    DI,//Department Issue
    DIC,//Department Issue Cancell
    ISS,//Department Issue
    ISSCAN,//Department Issue Cancel
    STTRQ,//transper request
    STTI,//transfer issue
    STTR,//transfer Recive
    I,//Channel Income
    E,//Channel Expenses
    ICAN,//Channel Income Cancel
    ECAN,//Channel Expenses Cancel
    ;

    public String getSuffix() {
        if (this == BillNumberSuffix.NONE) {
            return "";
        } else {
            return this.toString();
        }
    }
}
