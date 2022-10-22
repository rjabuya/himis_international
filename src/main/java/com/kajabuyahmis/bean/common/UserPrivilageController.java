/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.Privileges;
import com.kajabuyahmis.data.dataStructure.PrivilageNode;
import com.kajabuyahmis.entity.WebUser;
import com.kajabuyahmis.entity.WebUserPrivilege;
import com.kajabuyahmis.facade.WebUserPrivilegeFacade;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.TreeNode;

//import org.primefaces.examples.domain.Document;  
//import org.primefaces.model;
/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics) Acting
 * Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class UserPrivilageController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private WebUserPrivilegeFacade ejbFacade;
    private List<WebUserPrivilege> selectedItems;
    private WebUserPrivilege current;
    private WebUser currentWebUser;
    private List<WebUserPrivilege> items = null;
    //private Privileges currentPrivileges;
    private TreeNode root;
    private TreeNode[] selectedNodes;
    private List<Privileges> privilegeList;

    private TreeNode createTreeNode() {
        TreeNode tmproot = new PrivilageNode("Root", null);

        TreeNode node0 = new PrivilageNode("OPD", tmproot);
        TreeNode node00 = new PrivilageNode("Billing Menu", node0, Privileges.Opd);
        TreeNode node01 = new PrivilageNode("Bill", node0, Privileges.OpdBilling);
        TreeNode node01a = new PrivilageNode("Pre Billing", node0, Privileges.OpdPreBilling);
        TreeNode node01aa = new PrivilageNode("Collecting Centre Billing", node0, Privileges.OpdCollectingCentreBilling);
        TreeNode node02 = new PrivilageNode("Bill Search", node0, Privileges.OpdBillSearch);
        TreeNode node03 = new PrivilageNode("Bill Item Search", node0, Privileges.OpdBillItemSearch);
        TreeNode node04 = new PrivilageNode("Reprint", node0, Privileges.OpdReprint);
        TreeNode node05 = new PrivilageNode("Cancel", node0, Privileges.OpdCancel);
        TreeNode node06 = new PrivilageNode("Return", node0, Privileges.OpdReturn);
        TreeNode node07 = new PrivilageNode("Reactivate", node0, Privileges.OpdReactivate);
        TreeNode node08 = new PrivilageNode("OPD Bill Search Edit(Patient Details)", node0, Privileges.OpdBillSearchEdit);

        TreeNode node1 = new PrivilageNode("Inward", tmproot);
        TreeNode node10 = new PrivilageNode("Inward Menu", node1, Privileges.Inward);
        TreeNode node11 = new PrivilageNode("Addmissions", node1);
        TreeNode node110 = new PrivilageNode("Admission Menu", node11, Privileges.InwardAdmissions);
        TreeNode node111 = new PrivilageNode("Admission", node11, Privileges.InwardAdmissionsAdmission);
        TreeNode node112 = new PrivilageNode("Edit Admission Details", node11, Privileges.InwardAdmissionsEditAdmission);
        TreeNode node113 = new PrivilageNode("Inward Appoinment", node11, Privileges.InwardAdmissionsInwardAppoinment);
        TreeNode node12 = new PrivilageNode("Room", node1);
        TreeNode node120 = new PrivilageNode("Room Menu", node12, Privileges.InwardRoom);
        TreeNode node121 = new PrivilageNode("Room Occupency", node12, Privileges.InwardRoomRoomOccupency);
        TreeNode node122 = new PrivilageNode("Room Change", node12, Privileges.InwardRoomRoomChange);
        TreeNode node123 = new PrivilageNode("Gurdian Room Change", node12, Privileges.InwardRoomGurdianRoomChange);
        TreeNode node124 = new PrivilageNode("Dischage Room in Room Ocupency", node12, Privileges.InwardRoomDischarge);
        TreeNode node13 = new PrivilageNode("Services & Items", node1);
        TreeNode node130 = new PrivilageNode("Services & Items", node13, Privileges.InwardServicesAndItems);
        TreeNode node131 = new PrivilageNode("Add Services", node13, Privileges.InwardServicesAndItemsAddServices);
        TreeNode node132 = new PrivilageNode("Add Out Side Charges", node13, Privileges.InwardServicesAndItemsAddOutSideCharges);
        TreeNode node133 = new PrivilageNode("Add Professional Fee", node13, Privileges.InwardServicesAndItemsAddProfessionalFee);
        TreeNode node134 = new PrivilageNode("Add Timed Services", node13, Privileges.InwardServicesAndItemsAddTimedServices);
        TreeNode node14 = new PrivilageNode("Billing", node1);
        TreeNode node140 = new PrivilageNode("Billing Menu", node14, Privileges.InwardBilling);
        TreeNode node141 = new PrivilageNode("Interim Bill", node14, Privileges.InwardBillingInterimBill);
        TreeNode node142 = new PrivilageNode("Interim Bill Search", node14, Privileges.InwardBillingInterimBillSearch);
        TreeNode node143 = new PrivilageNode("Edit Patient Name After Payment Finalized", node14, Privileges.InwardFinalBillReportEdit);
        TreeNode node19 = new PrivilageNode("Pharmacy", node1);
        TreeNode node190 = new PrivilageNode("Pharmacy Menu", node19, Privileges.InwardPharmacyMenu);
        TreeNode node191 = new PrivilageNode("Pharmacy Issue Request", node19, Privileges.InwardPharmacyIssueRequest);
        TreeNode node192 = new PrivilageNode("Pharmacy Issue Request Search", node19, Privileges.InwardPharmacyIssueRequestSearch);
        TreeNode node15 = new PrivilageNode("Search", node1);
        TreeNode node150 = new PrivilageNode("Serch Menu", node15, Privileges.InwardSearch);
        TreeNode node151 = new PrivilageNode("Serch Service Bill", node15, Privileges.InwardSearchServiceBill);
        TreeNode node152 = new PrivilageNode("Serch Professional Bill", node15, Privileges.InwardSearchProfessionalBill);
        TreeNode node153 = new PrivilageNode("Serch Final Bill", node15, Privileges.InwardSearchFinalBill);
        TreeNode node16 = new PrivilageNode("Reports", node1, Privileges.InwardReport);
        TreeNode node17 = new PrivilageNode("Administration", node1, Privileges.InwardAdministration);
        TreeNode node18 = new PrivilageNode("Aditional Privilages", node1);
        TreeNode node180 = new PrivilageNode("Additional Privilage Menu", node18, Privileges.InwardAdditionalPrivilages);
        TreeNode node181 = new PrivilageNode("Search Bills", node18, Privileges.InwardBillSearch);
        TreeNode node182 = new PrivilageNode("Search Bill Items", node18, Privileges.InwardBillItemSearch);
        TreeNode node183 = new PrivilageNode("Reprint", node18, Privileges.InwardBillReprint);
        TreeNode node184 = new PrivilageNode("Cancel", node18, Privileges.InwardCancel);
        TreeNode node185 = new PrivilageNode("Return", node18, Privileges.InwardReturn);
        TreeNode node186 = new PrivilageNode("Reactivate", node18, Privileges.InwardReactivate);
        TreeNode node187 = new PrivilageNode("Show Inward Fee", node18, Privileges.ShowInwardFee);
        TreeNode node188 = new PrivilageNode("Inward Check", node18, Privileges.InwardCheck);
        TreeNode node189 = new PrivilageNode("Inward Uncheck", node18, Privileges.InwardUnCheck);
        TreeNode node1810 = new PrivilageNode("Inward Final Bill Cancel With Out Check Date Range", node18, Privileges.InwardFinalBillCancel);
        TreeNode node1811 = new PrivilageNode("Inward Out Side Bill Mark as Un-Paid", node18, Privileges.InwardOutSideMarkAsUnPaid);
        TreeNode node1812 = new PrivilageNode("Inward Bill Settle With Out Check", node18, Privileges.InwardBillSettleWithoutCheck);

        TreeNode node1a = new PrivilageNode("Theatre", tmproot);
        TreeNode node1a0 = new PrivilageNode("Theatre Menu", node1a, Privileges.Theatre);
        TreeNode node1a2 = new PrivilageNode("Add Surgery", node1a, Privileges.TheatreAddSurgery);
        TreeNode node1a1 = new PrivilageNode("Theatre Billing", node1a, Privileges.TheatreBilling);
        TreeNode node1a3 = new PrivilageNode("Theatre Transfer Menu Item", node1a, Privileges.TheaterTransfer);
        TreeNode node1a4 = new PrivilageNode("Theatre Transfer Request", node1a, Privileges.TheaterTransferRequest);
        TreeNode node1a5 = new PrivilageNode("Theatre Transfer Issue", node1a, Privileges.TheaterTransferIssue);
        TreeNode node1a6 = new PrivilageNode("Theatre Transfer Recieve", node1a, Privileges.TheaterTransferRecieve);
        TreeNode node1a7 = new PrivilageNode("Theatre Transfer Report", node1a, Privileges.TheaterTransferReport);
        TreeNode node1a8 = new PrivilageNode("Theatre Show Reports Menu Item", node1a, Privileges.TheaterReports);
        TreeNode node1a9 = new PrivilageNode("Theatre Show Summery Menu Item", node1a, Privileges.TheaterSummeries);
        TreeNode node1a10 = new PrivilageNode("Theatre BHT Issue", node1a);
        TreeNode node1a100 = new PrivilageNode("Theatre BHT Issue", node1a10, Privileges.TheaterIssue);
        TreeNode node1a101 = new PrivilageNode("Pharmacy BHT Issue", node1a10, Privileges.TheaterIssuePharmacy);
        TreeNode node1a102 = new PrivilageNode("General BHT Issue", node1a10, Privileges.TheaterIssueStore);
        TreeNode node1a1021 = new PrivilageNode("Inward BHT Billing", node1a102, Privileges.TheaterIssueStoreBhtBilling);
        TreeNode node1a1022 = new PrivilageNode("Search BHT Issue Bill", node1a102, Privileges.TheaterIssueStoreBhtSearchBill);
        TreeNode node1a1023 = new PrivilageNode("Search BHT Issue Bill Items ", node1a102, Privileges.TheaterIssueStoreBhtSearchBillItem);
        TreeNode node1a11 = new PrivilageNode("Opd Issue", node1a, Privileges.TheaterIssueOpd);
        TreeNode node1a12 = new PrivilageNode("Opd Issue For Cashier", node1a11, Privileges.TheaterIssueOpdForCasheir);
        TreeNode node1a13 = new PrivilageNode("Opd Issue Search Pre Bill", node1a11, Privileges.TheaterIssueOpdSearchPreBill);
        TreeNode node1a14 = new PrivilageNode("Opd Issue Return Item Only", node1a11, Privileges.TheaterIssueOpdSearchPreBillForReturnItemOnly);
        TreeNode node1a15 = new PrivilageNode("Opd Issue Search Pre Bill Return", node1a11, Privileges.TheaterIssueOpdSearchPreBillReturn);
        TreeNode node1a16 = new PrivilageNode("Opd Issue Pre Bill Add To Stock", node1a11, Privileges.TheaterIssueOpdSearchPreBillAddToStock);

        TreeNode node2 = new PrivilageNode("Lab", tmproot);
        TreeNode node200 = new PrivilageNode("Lab Menu", node2, Privileges.Lab);
        TreeNode node201 = new PrivilageNode("Billing Menu", node2);
        TreeNode node217 = new PrivilageNode("Lab Bill", node201, Privileges.LabBilling);
        TreeNode node201a = new PrivilageNode("Lab Bill Search", node201, Privileges.LabBillSearch);
        TreeNode node201b = new PrivilageNode("Lab Bill Item Search", node201, Privileges.LabBillItemSearch);
        TreeNode node218 = new PrivilageNode("Lab Bill Search", node2, Privileges.LabBillSearchCashier);
        TreeNode node202 = new PrivilageNode("Search Bills", node2, Privileges.LabBillSearch);
        TreeNode node203 = new PrivilageNode("Lab Report Search", node2, Privileges.LabReportSearch);
        TreeNode node204 = new PrivilageNode("Patient Edit", node2, Privileges.LabEditPatient);
        TreeNode node205 = new PrivilageNode("Lab Bill Reprint", node2, Privileges.LabBillReprint);
        TreeNode node206 = new PrivilageNode("Lab Bill Return", node2, Privileges.LabBillReturning);
        TreeNode node207 = new PrivilageNode("Lab Bill Cancel", node2, Privileges.LabBillCancelling);
        TreeNode node226 = new PrivilageNode("CC Bill Cancel", node2, Privileges.CollectingCentreCancelling);
        TreeNode node208 = new PrivilageNode("Reactivate", node2, Privileges.LabBillReactivating);
        TreeNode node209 = new PrivilageNode("Sample Collection", node2, Privileges.LabSampleCollecting);
        TreeNode node210 = new PrivilageNode("Sample Receive", node2, Privileges.LabSampleReceiving);
        TreeNode node211 = new PrivilageNode("DataEntry", node2, Privileges.LabDataentry);
        TreeNode node212 = new PrivilageNode("Autherize", node2, Privileges.LabAutherizing);
        TreeNode node213 = new PrivilageNode("De-Autherize", node2, Privileges.LabDeAutherizing);
        TreeNode node214 = new PrivilageNode("Report Print", node2, Privileges.LabPrinting);
        TreeNode node214a = new PrivilageNode("Report Reprint", node214, Privileges.LabReprinting);
        TreeNode node215 = new PrivilageNode("Lab Report Formats Editing", node2, Privileges.LabReportFormatEditing);
        TreeNode node215a = new PrivilageNode("Report Edit After Authorized", node2, Privileges.LabReportEdit);
        TreeNode node216 = new PrivilageNode("Lab Summeries", node2);
        TreeNode node216a = new PrivilageNode("Lab Summeries Menu", node216, Privileges.LabSummeries);
        TreeNode node216b = new PrivilageNode("Lab Summeries Level1", node216, Privileges.LabSummeriesLevel1);
        TreeNode node216c = new PrivilageNode("Lab Summeries Level2", node216, Privileges.LabSummeriesLevel2);
        TreeNode node216d = new PrivilageNode("Lab Summeries Level3", node216, Privileges.LabSummeriesLevel3);
        TreeNode node221 = new PrivilageNode("Lab Investigation Fees", node2, Privileges.LabInvestigationFee);
        TreeNode node222 = new PrivilageNode("Lab Bill Cancell Special(after collecting sample can cancell)", node2, Privileges.LabBillCancelSpecial);
        TreeNode node223 = new PrivilageNode("Lab Bill Refund Special(after collecting sample can Refund)", node2, Privileges.LabBillRefundSpecial);
        TreeNode node224 = new PrivilageNode("Add Inward Services", node2, Privileges.LabAddInwardServices);
        TreeNode node225 = new PrivilageNode("Search By Logged Institution", node2, Privileges.LabReportSearchByLoggedInstitution);
        TreeNode node227 = new PrivilageNode("Lab Administration", node2);
        TreeNode node227a = new PrivilageNode("Lab Administration Menu", node227, Privileges.LabAdiministrator);
        TreeNode node227b = new PrivilageNode("Mannage Items Menu", node227, Privileges.LabItems);
        TreeNode node227ba = new PrivilageNode("Mannage Item Fee Update", node227, Privileges.LabItemFeeUpadate);
        TreeNode node227bb = new PrivilageNode("Mannage Item Fee Delete", node227, Privileges.LabItemFeeDelete);
        TreeNode node227c = new PrivilageNode("Mannage Reports Menu", node227, Privileges.LabReports);
        TreeNode node227d = new PrivilageNode("Lists Menu", node227, Privileges.LabLists);
        TreeNode node227e = new PrivilageNode("Setup Menu", node227, Privileges.LabSetUp);
        TreeNode node228 = new PrivilageNode("Lab Inward Billing Menu", node2);
        TreeNode node228a = new PrivilageNode("Lab Inward Bill", node228, Privileges.LabInwardBilling);
        TreeNode node228b = new PrivilageNode("Lab Inward Bill Search", node228, Privileges.LabInwardSearchServiceBill);
        TreeNode node229 = new PrivilageNode("Lab Collecting Center Billing", node2);
        TreeNode node229a = new PrivilageNode("Lab Collecting Center Menu", node229, Privileges.LabCollectingCentreBilling);
        TreeNode node229b = new PrivilageNode("Lab Collecting center Billing", node229, Privileges.LabCCBilling);
        TreeNode node229c = new PrivilageNode("Lab Collecting Center Bill search", node229, Privileges.LabCCBillingSearch);
        TreeNode node230 = new PrivilageNode("Lab Reporting", node2, Privileges.LabReporting);

        TreeNode node3 = new PrivilageNode("Pharmacy", tmproot);
        TreeNode node300 = new PrivilageNode("Pharmacy Menu", node3, Privileges.Pharmacy);
        TreeNode node301 = new PrivilageNode("Pharmacy Administration", node3, Privileges.PharmacyAdministration);
        TreeNode node306 = new PrivilageNode("Pharmacy Stock Adjustment", node3, Privileges.PharmacyStockAdjustment);
        TreeNode node306a = new PrivilageNode("Pharmacy Stock Adjustment By Single Item", node3, Privileges.PharmacyStockAdjustmentSingleItem);
        TreeNode node307 = new PrivilageNode("Pharmacy Re Add To Stock", node3, Privileges.PharmacyReAddToStock);
        TreeNode node314 = new PrivilageNode("Pharmacy Stock Issue", node3, Privileges.PharmacyStockIssue);

        ///////////////////////
        TreeNode node302 = new PrivilageNode("GRN", node3);
        TreeNode node3021 = new PrivilageNode("GRN", node302, Privileges.PharmacyGoodReceive);
        TreeNode node3021a = new PrivilageNode("GRN For Wholesale", node302, Privileges.PharmacyGoodReceiveWh);
        TreeNode node3022 = new PrivilageNode("GRN Cancelling", node302, Privileges.PharmacyGoodReceiveCancel);
        TreeNode node3023 = new PrivilageNode("GRN Return", node302, Privileges.PharmacyGoodReceiveReturn);
        TreeNode node3024 = new PrivilageNode("GRN Edit", node302, Privileges.PharmacyGoodReceiveEdit);
        ///////////////////////
        TreeNode node303 = new PrivilageNode("Order", node3);
        TreeNode node3031 = new PrivilageNode("Order Creation", node303, Privileges.PharmacyOrderCreation);
        TreeNode node3032 = new PrivilageNode("Order Aproval", node303, Privileges.PharmacyOrderApproval);
        TreeNode node3033 = new PrivilageNode("Order Cancellation", node303, Privileges.PharmacyOrderCancellation);
        //////////////////
        TreeNode node304 = new PrivilageNode("Sale", node3);
        TreeNode node3041 = new PrivilageNode("Pharmacy Sale", node304, Privileges.PharmacySale);
        TreeNode node3041a = new PrivilageNode("Pharmacy Wholesale", node304, Privileges.PharmacySaleWh);
        TreeNode node3042 = new PrivilageNode("Pharmacy Sale Cancel", node304, Privileges.PharmacySaleCancel);
        TreeNode node3042a = new PrivilageNode("Pharmacy Wholesale Cancel", node304, Privileges.PharmacySaleCancelWh);
        TreeNode node3043 = new PrivilageNode("Pharmacy Sale Return", node304, Privileges.PharmacySaleReturn);
        TreeNode node3043a = new PrivilageNode("Pharmacy Wholesale Return", node304, Privileges.PharmacySaleReturnWh);
//////////////////
        TreeNode node305 = new PrivilageNode("Purchase", node3);
        TreeNode node3051 = new PrivilageNode("Purchase", node305, Privileges.PharmacyPurchase);
        TreeNode node3051a = new PrivilageNode("Purchase Wholesale", node305, Privileges.PharmacyPurchaseWh);
        TreeNode node3052 = new PrivilageNode("Purchase Cancel", node305, Privileges.PharmacyPurchaseCancellation);
        TreeNode node3053 = new PrivilageNode("Purchase Return", node305, Privileges.PharmacyPurchaseReturn);
        TreeNode node3054 = new PrivilageNode("Pharmacy Return Without Traising", node305, Privileges.PharmacyReturnWithoutTraising);
        ///////////////////
        TreeNode node308 = new PrivilageNode("Pharmacy Dealor Payment", node3, Privileges.PharmacyDealorPayment);
        TreeNode node309 = new PrivilageNode("Pharmacy Search", node3, Privileges.PharmacySearch);
        TreeNode node310 = new PrivilageNode("Pharmacy Reports", node3, Privileges.PharmacyReports);
        TreeNode node311 = new PrivilageNode("Pharmacy Summery", node3, Privileges.PharmacySummery);
        TreeNode node312 = new PrivilageNode("Pharmacy Transfer", node3, Privileges.PharmacyTransfer);
        TreeNode node313 = new PrivilageNode("Pharmacy Set Reorder Level", node3, Privileges.PharmacySetReorderLevel);
        TreeNode node315 = new PrivilageNode("Pharmacy Accept BHT Issue", node3, Privileges.PharmacyBHTIssueAccept);

        ///////////////////
        TreeNode node4 = new PrivilageNode("Payment", tmproot);
        TreeNode node400 = new PrivilageNode("Payment Menu", node4, Privileges.Payment);
        TreeNode node401 = new PrivilageNode("Staff Payment", node4, Privileges.PaymentBilling);
        TreeNode node402 = new PrivilageNode("Payment Search", node4, Privileges.PaymentBillSearch);
        TreeNode node403 = new PrivilageNode("Payment Reprints", node4, Privileges.PaymentBillReprint);
        TreeNode node404 = new PrivilageNode("Payment Cancel", node4, Privileges.PaymentBillCancel);
        TreeNode node405 = new PrivilageNode("Payment Refund", node4, Privileges.PaymentBillRefund);
        TreeNode node406 = new PrivilageNode("Payment Reactivation", node4, Privileges.PaymentBillReactivation);

        TreeNode node5 = new PrivilageNode("Reports", tmproot);
        TreeNode node53 = new PrivilageNode("Reports Menu", node5, Privileges.Reports);
        TreeNode node51 = new PrivilageNode("For Own Institution", node5);
        TreeNode node52 = new PrivilageNode("For All Institution", node5);

        TreeNode node510 = new PrivilageNode("Cash/Card Bill Reports", node51, Privileges.ReportsSearchCashCardOwn);
        TreeNode node511 = new PrivilageNode("Credit Bill Reports", node51, Privileges.ReportsSearchCreditOwn);
        TreeNode node512 = new PrivilageNode("Item Reports", node51, Privileges.ReportsItemOwn);

        TreeNode node520 = new PrivilageNode("Cash/Card Bill Reports", node52, Privileges.ReportsSearchCashCardOther);
        TreeNode node521 = new PrivilageNode("Credit Bill Reports", node52, Privileges.ReportSearchCreditOther);
        TreeNode node522 = new PrivilageNode("Item Reports", node52, Privileges.ReportsItemOwn);

        TreeNode node7 = new PrivilageNode("Clinicals", tmproot);
        TreeNode node700 = new PrivilageNode("Clinical Data", node7, Privileges.Clinical);
        TreeNode node701 = new PrivilageNode("Patient Summery", node7, Privileges.ClinicalPatientSummery);
        TreeNode node702 = new PrivilageNode("Patient Details", node7, Privileges.ClinicalPatientDetails);
        TreeNode node703 = new PrivilageNode("Patient Photo", node7, Privileges.ClinicalPatientPhoto);
        TreeNode node704 = new PrivilageNode("Visit Details", node7, Privileges.ClinicalVisitDetail);
        TreeNode node705 = new PrivilageNode("Visit Summery", node7, Privileges.ClinicalVisitSummery);
        TreeNode node706 = new PrivilageNode("History", node7, Privileges.ClinicalHistory);
        TreeNode node707 = new PrivilageNode("Administration", node7, Privileges.ClinicalAdministration);
        TreeNode node708 = new PrivilageNode("Clinical Patient Delete", node7, Privileges.ClinicalPatientDelete);

        TreeNode node6 = new PrivilageNode("Administration", tmproot);
        TreeNode node60 = new PrivilageNode("Admin Menu", node6, Privileges.Admin);
        TreeNode node61 = new PrivilageNode("Manage Users", node6, Privileges.AdminManagingUsers);
        TreeNode node62 = new PrivilageNode("Manage Institutions", node6, Privileges.AdminInstitutions);
        TreeNode node63 = new PrivilageNode("Manage Staff", node6, Privileges.AdminStaff);
        TreeNode node64 = new PrivilageNode("Manage Items/Services", node6, Privileges.AdminItems);
        TreeNode node65 = new PrivilageNode("Manage Fees/Prices/Packages", node6, Privileges.AdminPrices);
        TreeNode node65a = new PrivilageNode("Filter Without Department", node6, Privileges.AdminFilterWithoutDepartment);
        TreeNode node68 = new PrivilageNode("Search All", node6, Privileges.SearchAll);
        TreeNode node81 = new PrivilageNode("Change Professional Fee", node6, Privileges.ChangeProfessionalFee);
        TreeNode node82 = new PrivilageNode("Change Professional Fee", node6, Privileges.ChangeCollectingCentre);
        TreeNode node69 = new PrivilageNode("Send Bulk SMS", node6, Privileges.SendBulkSMS);
        TreeNode node67 = new PrivilageNode("Only For Developers(Don't Add That)", node6, Privileges.Developers);

        TreeNode node66 = new PrivilageNode("Membership", tmproot);
        TreeNode node660 = new PrivilageNode("Membership Menu", node66, Privileges.MemberShip);
        TreeNode node6601 = new PrivilageNode("Add Members", node66, Privileges.MemberShipAdd);
        TreeNode node6602 = new PrivilageNode("Edit Members", node66, Privileges.MemberShipEdit);
        TreeNode node6603 = new PrivilageNode("Membership Reports", node66, Privileges.MembershipReports);
        TreeNode node6604 = new PrivilageNode("Membership Discount Management", node66, Privileges.MembershipDiscountManagement);
        TreeNode node6605 = new PrivilageNode("Membership Administration", node66, Privileges.MembershipAdministration);
        TreeNode node6606 = new PrivilageNode("Other", node66);

        TreeNode node661 = new PrivilageNode("Membership Schemes", node6606, Privileges.MembershipSchemes);
        TreeNode node6620 = new PrivilageNode("Inward Membership Menu", node6606, Privileges.MemberShipInwardMemberShip);
        TreeNode node6621 = new PrivilageNode("Schemes Dicounts", node6606, Privileges.MemberShipInwardMemberShipSchemesDicounts);
        TreeNode node6622 = new PrivilageNode("Inward Membership Report", node6606, Privileges.MemberShipInwardMemberShipInwardMemberShipReport);
        TreeNode node6630 = new PrivilageNode("Opd MemberShip Dis Menu", node6606, Privileges.MemberShipOpdMemberShipDis);
        TreeNode node6631 = new PrivilageNode("Discount By Department", node6606, Privileges.MemberShipOpdMemberShipDisByDepartment);
        TreeNode node6632 = new PrivilageNode("Discount By Category", node6606, Privileges.MemberShipOpdMemberShipDisByCategory);
        TreeNode node6633 = new PrivilageNode("Opd MemberShip Report", node6606, Privileges.MemberShipOpdMemberShipDisOpdMemberShipReport);
        TreeNode node664 = new PrivilageNode("Re-Activate Registed Patient", node6606, Privileges.MemberShipMemberReActive);
        TreeNode node665 = new PrivilageNode("De-Activate Registed Patient", node6606, Privileges.MemberShipMemberDeActive);

        TreeNode node9 = new PrivilageNode("Human Resource", tmproot);
        TreeNode node91 = new PrivilageNode("HR Menu", node9, Privileges.Hr);
        TreeNode node92 = new PrivilageNode("Working Time", node9);
        TreeNode node920 = new PrivilageNode("Working Time Menu", node92, Privileges.HrWorkingTime);
        TreeNode node921 = new PrivilageNode("Roster Table", node92, Privileges.HrRosterTable);
        TreeNode node922 = new PrivilageNode("Upload Attendence", node92, Privileges.HrUploadAttendance);
        TreeNode node923 = new PrivilageNode("Analyse Attendence By Roster", node92, Privileges.HrAnalyseAttendenceByRoster);
        TreeNode node924 = new PrivilageNode("Analyse Attendence By Staff", node92, Privileges.HrAnalyseAttendenceByStaff);
        TreeNode node93 = new PrivilageNode("Form", node9);
        TreeNode node930 = new PrivilageNode("Form Menu", node93, Privileges.HrForms);
        TreeNode node931 = new PrivilageNode("Leave Form", node93, Privileges.HrLeaveForms);
        TreeNode node932 = new PrivilageNode("Additional Form", node93, Privileges.HrAdditionalForms);
        TreeNode node94 = new PrivilageNode("HR Salary Advance", node9, Privileges.HrAdvanceSalary);
        TreeNode node95 = new PrivilageNode("HR Salary", node9);
        TreeNode node95a = new PrivilageNode("HR Salary Generate", node95, Privileges.HrGenerateSalary);
        TreeNode node95b = new PrivilageNode("HR Salary Generate Special", node95, Privileges.HrGenerateSalarySpecial);
        TreeNode node96 = new PrivilageNode("HR Salary Print", node9, Privileges.HrPrintSalary);
        TreeNode node97 = new PrivilageNode("HR Reports", node9);
        TreeNode node970 = new PrivilageNode("HR Reports Menu", node97, Privileges.HrReports);
        TreeNode node971 = new PrivilageNode("HR Reports Level 1", node97, Privileges.HrReportsLevel1);
        TreeNode node972 = new PrivilageNode("HR Reports Level 2", node97, Privileges.HrReportsLevel2);
        TreeNode node973 = new PrivilageNode("HR Reports Level 3", node97, Privileges.HrReportsLevel3);
//        TreeNode node974 = new PrivilageNode("HR Employee History Reports", node97, Privileges.EmployeeHistoryReport);
        TreeNode node98 = new PrivilageNode("HR Administration", node9);
        TreeNode node980 = new PrivilageNode("HR Administration Menu", node98, Privileges.HrAdmin);
        TreeNode node981 = new PrivilageNode("HR Delete Late Leave", node98, Privileges.hrDeleteLateLeave);
        TreeNode node982 = new PrivilageNode("HR Edit Retied Date", node98, Privileges.HrEditRetiedDate);
        TreeNode node983 = new PrivilageNode("HR Remove Resign Date", node98, Privileges.HrRemoveResignDate);

        TreeNode node20 = new PrivilageNode("Store", tmproot);
        TreeNode node2000 = new PrivilageNode("Store Menu", node20, Privileges.Store);
        TreeNode node2001 = new PrivilageNode("Issue", node20);
        TreeNode node20010 = new PrivilageNode("Issue Menu", node2001, Privileges.StoreIssue);
        TreeNode node20011 = new PrivilageNode("Inward Billing", node2001, Privileges.StoreIssueInwardBilling);
        TreeNode node20012 = new PrivilageNode("Search Issue Bill", node2001, Privileges.StoreIssueSearchBill);
        TreeNode node20013 = new PrivilageNode("Search Issue Bill Items", node2001, Privileges.StoreIssueBillItems);
        TreeNode node2002 = new PrivilageNode("Purchase", node20);
        TreeNode node20020 = new PrivilageNode("Purchase Menu", node2002, Privileges.StorePurchase);
        TreeNode node20021 = new PrivilageNode("Purchase Order", node2002, Privileges.StorePurchaseOrder);
        TreeNode node20022 = new PrivilageNode("PO Approve", node2002, Privileges.StorePurchaseOrderApprove);
        TreeNode node20023 = new PrivilageNode("GRN Recive", node2002, Privileges.StorePurchaseGRNRecive);
        TreeNode node20024 = new PrivilageNode("GRN Return", node2002, Privileges.StorePurchaseGRNReturn);
        TreeNode node20025 = new PrivilageNode("Purchase", node2002, Privileges.StorePurchasePurchase);
        TreeNode node20026 = new PrivilageNode("PO Approve Search", node2002, Privileges.StorePurchaseOrderApproveSearch);
        TreeNode node2003 = new PrivilageNode("Transfer", node20);
        TreeNode node20030 = new PrivilageNode("Transfer Menu", node2003, Privileges.StoreTransfer);
        TreeNode node20031 = new PrivilageNode("Request", node2003, Privileges.StoreTransferRequest);
        TreeNode node20032 = new PrivilageNode("Issue", node2003, Privileges.StoreTransferIssue);
        TreeNode node20033 = new PrivilageNode("Recive", node2003, Privileges.StoreTransferRecive);
        TreeNode node20034 = new PrivilageNode("Report", node2003, Privileges.StoreTransferReport);
        TreeNode node2004 = new PrivilageNode("Ajustment", node20);
        TreeNode node20040 = new PrivilageNode("Adjustment Menu", node2004, Privileges.StoreAdjustment);
        TreeNode node20041 = new PrivilageNode("Department Stock(Qty)", node2004, Privileges.StoreAdjustmentDepartmentStock);
        TreeNode node20042 = new PrivilageNode("Staff Stock Adjustment", node2004, Privileges.StoreAdjustmentStaffStock);
        TreeNode node20043 = new PrivilageNode("Purchase Rate", node2004, Privileges.StoreAdjustmentPurchaseRate);
        TreeNode node20044 = new PrivilageNode("Sale Rate", node2004, Privileges.StoreAdjustmentSaleRate);
        TreeNode node2005 = new PrivilageNode("Delor Payment", node20);
        TreeNode node20050 = new PrivilageNode("Delor Payment Menu", node2005, Privileges.StoreDealorPayment);
        TreeNode node20051 = new PrivilageNode("Delor Due Search", node2005, Privileges.StoreDealorPaymentDueSearch);
        TreeNode node20052 = new PrivilageNode("Delor Due By Age", node2005, Privileges.StoreDealorPaymentDueByAge);
        TreeNode node20053 = new PrivilageNode("Payment", node2005);
        TreeNode node200530 = new PrivilageNode("Payment Menu", node20053, Privileges.StoreDealorPaymentPayment);
        TreeNode node200531 = new PrivilageNode("GRN Payment", node20053, Privileges.StoreDealorPaymentPaymentGRN);
        TreeNode node200532 = new PrivilageNode("GRN Payment(Select)", node20053, Privileges.StoreDealorPaymentPaymentGRNSelect);
        TreeNode node20054 = new PrivilageNode("GRN Payment Due Search", node2005, Privileges.StoreDealorPaymentGRNDoneSearch);
        TreeNode node2006 = new PrivilageNode("Search", node20);
        TreeNode node20060 = new PrivilageNode("Search Menu", node2006, Privileges.StoreSearch);
        TreeNode node2007 = new PrivilageNode("Report", node20);
        TreeNode node20070 = new PrivilageNode("Report Menu", node2007, Privileges.StoreReports);
        TreeNode node2008 = new PrivilageNode("Summery", node20);
        TreeNode node20080 = new PrivilageNode("Summery Menu", node2008, Privileges.StoreSummery);
        TreeNode node2009 = new PrivilageNode("Administration", node20);
        TreeNode node20090 = new PrivilageNode("Administration Menu", node2009, Privileges.StoreAdministration);

        TreeNode node21 = new PrivilageNode("Search", tmproot);
        TreeNode node2100 = new PrivilageNode("Search Menu", node21, Privileges.Search);
        TreeNode node2101 = new PrivilageNode("Grand Search", node21, Privileges.SearchGrand);

        TreeNode node24 = new PrivilageNode("User", tmproot);
        TreeNode node240 = new PrivilageNode("Change Theme", node24, Privileges.ChangePreferece);

        TreeNode node22 = new PrivilageNode("Cash Transaction", tmproot);
        TreeNode node2200 = new PrivilageNode("Cash Transaction Menu", node22, Privileges.CashTransaction);
        TreeNode node2201 = new PrivilageNode("Cash In", node22, Privileges.CashTransactionCashIn);
        TreeNode node2202 = new PrivilageNode("Cash Out", node22, Privileges.CashTransactionCashOut);
        TreeNode node2203 = new PrivilageNode("List To Cash Recieve", node22, Privileges.CashTransactionListToCashRecieve);

        TreeNode node23 = new PrivilageNode("Channelling", tmproot);
        TreeNode node2300 = new PrivilageNode("Channelling Menu", node23, Privileges.Channelling);
        TreeNode node2301 = new PrivilageNode("Channel Booking", node23, Privileges.ChannellingChannelBooking);
        TreeNode node2308 = new PrivilageNode("Channel Future Booking", node23, Privileges.ChannellingFutureChannelBooking);
        TreeNode node2302 = new PrivilageNode("Past Booking", node23, Privileges.ChannellingPastBooking);
        TreeNode node2303 = new PrivilageNode("Booked List", node23, Privileges.ChannellingBookedList);
        TreeNode node2304 = new PrivilageNode("Doctor Leave Menu", node23, Privileges.ChannellingDoctorLeave);
        TreeNode node230400 = new PrivilageNode("Doctor Leave By Date", node2304, Privileges.ChannellingDoctorLeaveByDate);
        TreeNode node230401 = new PrivilageNode("Doctor Leave By Service Session", node2304, Privileges.ChannellingDoctorLeaveByServiceSession);
        TreeNode node2305 = new PrivilageNode("Channel Sheduling", node23, Privileges.ChannellingChannelSheduling);
        TreeNode node2306 = new PrivilageNode("Channel Agent Fee", node23, Privileges.ChannellingChannelAgentFee);
        TreeNode node2309 = new PrivilageNode("Channel Booking Interface", node23);
        TreeNode node2309a = new PrivilageNode("Booking", node2309, Privileges.ChannelBookingBokking);
        TreeNode node2309b = new PrivilageNode("Reprint", node2309, Privileges.ChannelBookingReprint);
        TreeNode node2309c = new PrivilageNode("Cancel", node2309, Privileges.ChannelBookingCancel);
        TreeNode node2309d = new PrivilageNode("Refunfd", node2309, Privileges.ChannelBookingRefund);
        TreeNode node2309e = new PrivilageNode("Settle", node2309, Privileges.ChannelBookingSettle);
        TreeNode node2309f = new PrivilageNode("Change", node2309, Privileges.ChannelBookingChange);
        TreeNode node2309g = new PrivilageNode("Serch", node2309, Privileges.ChannelBookingSearch);
        TreeNode node2309h = new PrivilageNode("Views", node2309, Privileges.ChannelBookingViews);
        TreeNode node2309i = new PrivilageNode("Doctor Payment", node2309, Privileges.ChannelBookingDocPay);
        TreeNode node2309j = new PrivilageNode("Restric Channel booking", node2309, Privileges.ChannelBookingRestric);
        TreeNode node2310 = new PrivilageNode("Print Past Booking Recipt", node23, Privileges.ChannellingPrintInPastBooking);
        TreeNode node2307 = new PrivilageNode("Payment", node23);
        TreeNode node23070 = new PrivilageNode("Payment Menu", node2307, Privileges.ChannellingPayment);
        TreeNode node23071 = new PrivilageNode("Pay Doctor", node2307, Privileges.ChannellingPaymentPayDoctor);
        TreeNode node23072 = new PrivilageNode("Payment Due Search", node2307, Privileges.ChannellingPaymentDueSearch);
        TreeNode node23073 = new PrivilageNode("Payment Done Search", node2307, Privileges.ChannellingPaymentDoneSearch);
        TreeNode node23011 = new PrivilageNode("Cashier Transaction", node23);
        TreeNode node23011a = new PrivilageNode("Cashier Transaction Menu", node23011, Privileges.ChannelCashierTransaction);
        TreeNode node23011b = new PrivilageNode("Income", node23011, Privileges.ChannelCashierTransactionIncome);
        TreeNode node23011c = new PrivilageNode("Income Search", node23011, Privileges.ChannelCashierTransactionIncomeSearch);
        TreeNode node23011d = new PrivilageNode("Expensses", node23011, Privileges.ChannelCashierTransactionExpencess);
        TreeNode node23011e = new PrivilageNode("Expensess Search", node23011, Privileges.ChannelCashierTransactionExpencessSearch);
        TreeNode node23010 = new PrivilageNode("Administrator", node23);
        TreeNode node23010a = new PrivilageNode("Edit Appoinment Count", node23010, Privileges.ChannellingApoinmentNumberCountEdit);
        TreeNode node23010b = new PrivilageNode("Edit Appoinment Number", node23010, Privileges.ChannellingEditSerialNo);
        TreeNode node23010c = new PrivilageNode("Edit Patient Details", node23010, Privileges.ChannellingEditPatientDetails);
        TreeNode node23010d = new PrivilageNode("Delete Shedule", node23010, Privileges.ChannellingChannelShedulRemove);
        TreeNode node23010e = new PrivilageNode("Edit Session Name", node23010, Privileges.ChannellingChannelShedulName);
        TreeNode node23010f = new PrivilageNode("Edit Session Starting No", node23010, Privileges.ChannellingChannelShedulStartingNo);
        TreeNode node23010g = new PrivilageNode("Edit Session Room No", node23010, Privileges.ChannellingChannelShedulRoomNo);
        TreeNode node23010h = new PrivilageNode("Edit Session Max Row No", node23010, Privileges.ChannellingChannelShedulMaxRowNo);
        TreeNode node23010i = new PrivilageNode("Edit Credit Limit User Level", node23010, Privileges.ChannellingEditCreditLimitUserLevel);
        TreeNode node23010j = new PrivilageNode("Edit Credit Limit Administrator Level", node23010, Privileges.ChannellingEditCreditLimitAdminLevel);
        TreeNode node23010k = new PrivilageNode("Channel Reports", node23, Privileges.ChannelReports);
        TreeNode node23010l = new PrivilageNode("Channel Summery", node23, Privileges.ChannelSummery);
        TreeNode node23012 = new PrivilageNode("Channel Mamagement", node23);
        TreeNode node23012a = new PrivilageNode("Channel Mamagement Menu", node23012, Privileges.ChannelManagement);
        TreeNode node23012b = new PrivilageNode("Channel Agencies", node23012, Privileges.ChannelAgencyAgencies);
        TreeNode node23012c = new PrivilageNode("Channel Agenciey Credit Limit Update", node23012, Privileges.ChannelAgencyCreditLimitUpdate);
        TreeNode node23012d = new PrivilageNode("Channel Agenciey Credit Limit Update (Bulk)", node23012, Privileges.ChannelAgencyCreditLimitUpdateBulk);
        TreeNode node23012e = new PrivilageNode("Add Channel Book To Agency", node23012, Privileges.ChannelAddChannelBookToAgency);
        TreeNode node23012f = new PrivilageNode("Channel Management Specialities", node23012, Privileges.ChannelManageSpecialities);
        TreeNode node23012g = new PrivilageNode("Channel Management Consultants", node23012, Privileges.ChannelManageConsultants);
        TreeNode node23012h = new PrivilageNode("Channel Editing Appoinment Count", node23012, Privileges.ChannelEditingAppoinmentCount);
        TreeNode node23012i = new PrivilageNode("Add Channelling Consultants To Institution ", node23012, Privileges.ChannelAddChannelingConsultantToInstitutions);
        TreeNode node23012j = new PrivilageNode("Channel Fee Update", node23012, Privileges.ChannelFeeUpdate);
        TreeNode node23012k = new PrivilageNode("Channel Credit Note", node23012, Privileges.ChannelCrdeitNote);
        TreeNode node23012l = new PrivilageNode("Channel Credit Note Search", node23012, Privileges.ChannelCrdeitNoteSearch);
        TreeNode node23012m = new PrivilageNode("Channel Debit Note", node23012, Privileges.ChannelDebitNote);
        TreeNode node23012n = new PrivilageNode("Channel Debit Note Search", node23012, Privileges.ChannelDebitNoteSearch);
        TreeNode node23012o = new PrivilageNode("Channel Cash Cancel Restriction", node23012, Privileges.ChannelCashCancelRestriction);
        TreeNode node23012p = new PrivilageNode("Channel Active Vat", node23012, Privileges.ChannelActiveVat);

        return tmproot;
    }

    public UserPrivilageController() {
        root = createTreeNode();
    }

    public TreeNode getRoot2() {
        return root;
    }

    public void setRoot2(TreeNode root2) {
        this.root = root2;
    }

    public String savePrivileges() {
        System.out.println("savePrivileges");
        if (currentWebUser == null) {
            System.out.println("currentWebUser = " + currentWebUser);
            UtilityController.addErrorMessage("Please select a user");
            return "";
        }
        retireAllPrivilege();
        for (TreeNode o : selectedNodes) {
            PrivilageNode pn1 = (PrivilageNode) o;
            updateSinglePrivilege(pn1.getP(), true);
        }

//        createRootForUser();
        return "/admin_view_user";
    }

    public void updateSinglePrivilege(Privileges p, boolean selected) {
        System.out.println("updateSinglePrivilege");
        System.out.println("p = " + p);
        System.out.println("selected = " + selected);
        if (p == null) {
            System.out.println("p = " + p);
            return;
        }
        WebUserPrivilege wup;
        Map m = new HashMap();
        m.put("p", p);
        m.put("wu", getCurrentWebUser());
        String sql = "SELECT i "
                + " FROM WebUserPrivilege i "
                + " where i.webUser=:wu "
                + " and i.privilege=:p ";
        wup = getEjbFacade().findFirstBySQL(sql, m);
        System.out.println("wup = " + wup);
        if (wup == null) {
            wup = new WebUserPrivilege();
            wup.setCreater(getSessionController().getLoggedUser());
            wup.setCreatedAt(Calendar.getInstance().getTime());
            wup.setPrivilege(p);
            wup.setRetired(!selected);
            wup.setWebUser(getCurrentWebUser());
            getFacade().create(wup);
        } else {
            wup.setRetired(!selected);
            getFacade().edit(wup);
        }
        System.out.println("wup.isRetired() = " + wup.isRetired());
    }

    public void retireAllPrivilege() {
        if (getCurrentWebUser() == null) {
            return;
        }
        List<WebUserPrivilege> wups;
        Map m = new HashMap();
        m.put("wu", getCurrentWebUser());
        String sql = "SELECT i "
                + " FROM WebUserPrivilege i "
                + " where i.webUser=:wu ";
        wups = getEjbFacade().findBySQL(sql, m);
        for (WebUserPrivilege wup : wups) {
            wup.setRetired(true);
            getFacade().edit(wup);
        }
    }

    public void remove() {
        if (getCurrent() == null) {
            UtilityController.addErrorMessage("Select Privilage");
            return;
        }

        getCurrent().setRetired(true);

        getFacade().edit(getCurrent());
    }

    private void recreateModel() {
        items = null;
    }

    public WebUserPrivilegeFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(WebUserPrivilegeFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public WebUserPrivilege getCurrent() {
        if (current == null) {
            current = new WebUserPrivilege();

        }
        return current;
    }

    public void setCurrent(WebUserPrivilege current) {
        this.current = current;
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
        createRootForUser();
        current = null;
        getCurrent();
    }

    private WebUserPrivilegeFacade getFacade() {
        return ejbFacade;
    }

    public void createRootForUser() {
        System.out.println("createRootForUser" );
        root = createTreeNode();
        if (getCurrentWebUser() == null) {
            return ;
        }

        String sql = "SELECT i "
                + " FROM WebUserPrivilege i "
                + " where i.webUser=:wu "
                + " and i.retired=:ret";
        Map m = new HashMap();
        m.put("wu", getCurrentWebUser());
        m.put("ret", false);
        items = getEjbFacade().findBySQL(sql, m);
        if (items == null) {
            return;
        }
  
        for (WebUserPrivilege wup : items) {
            System.out.println("wup.isRetired() = " + wup.isRetired());
            for (Object o : root.getChildren()) {
                PrivilageNode n = (PrivilageNode) o;
                System.out.println("n.getP() = " + n.getP());
                if (n.getP()!=null && wup.getPrivilege().equals(n.getP())) {
                    n.setSelected(true);
                    System.out.println("n.isSelected() = " + n.isSelected());
                }
                for (Object o1 : n.getChildren()) {
                    PrivilageNode n1 = (PrivilageNode) o1;
                    System.out.println("n1.getP() = " + n1.getP());
                    if (n1.getP()!=null && wup.getPrivilege().equals(n1.getP())) {
                        n1.setSelected(true);
                        System.out.println("n1.isSelected() = " + n1.isSelected());
                    }
                    for (Object o2 : n1.getChildren()) {
                        PrivilageNode n2 = (PrivilageNode) o2;
                        System.out.println("n2.getP() = " + n2.getP());
                        if (n2.getP()!=null && wup.getPrivilege().equals(n2.getP())) {
                            n2.setSelected(true);
                            System.out.println("n2.isSelected() = " + n2.isSelected());
                        }
                    }
                }
            }
        }

    }

//    public void markTreeNode(Privileges p, TreeNode n) {
//        if (p == null) {
//            return;
//        }
//        n.setSelected(false);
//        Map m = new HashMap();
//        m.put("wup", p);
//        String sql = "SELECT i FROM WebUserPrivilege i where i.webUser.id= " + getCurrentWebUser().getId() + " and i.privilege=:wup ";
//        List<WebUserPrivilege> tmp = getEjbFacade().findBySQL(sql, m, TemporalType.DATE);
//        if (tmp == null || tmp.isEmpty()) {
//            for (WebUserPrivilege wu : tmp) {
//                if (!wu.isRetired()) {
//                    n.setSelected(true);
//                }
//            }
//        }
//    }
    public WebUser getCurrentWebUser() {
        return currentWebUser;
    }

    public void setCurrentWebUser(WebUser currentWebUser) {
        this.currentWebUser = currentWebUser;
        root = null;
    }

    public List<Privileges> getPrivilegeList() {
        return privilegeList;
    }

    public void setPrivilegeList(List<Privileges> privilegeList) {
        this.privilegeList = privilegeList;

    }

    public List<WebUserPrivilege> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<WebUserPrivilege> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    /**
     *
     */
    @FacesConverter(forClass = WebUserPrivilege.class)
    public static class WebUserPrivilegeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserPrivilageController controller = (UserPrivilageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userPrivilegeController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof WebUserPrivilege) {
                WebUserPrivilege o = (WebUserPrivilege) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + UserPrivilageController.class.getName());
            }
        }
    }
}
