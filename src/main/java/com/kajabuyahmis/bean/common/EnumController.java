/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import com.kajabuyahmis.data.ApplicationInstitution;
import com.kajabuyahmis.data.BillClassType;
import com.kajabuyahmis.data.BillType;
import com.kajabuyahmis.data.CalculationType;
import com.kajabuyahmis.data.CssVerticalAlign;
import com.kajabuyahmis.data.Dashboard;
import com.kajabuyahmis.data.DepartmentListMethod;
import com.kajabuyahmis.data.DepartmentType;
import com.kajabuyahmis.data.FeeType;
import com.kajabuyahmis.data.InvestigationItemType;
import com.kajabuyahmis.data.InvestigationItemValueType;
import com.kajabuyahmis.data.PaperType;
import com.kajabuyahmis.data.PaymentMethod;
import com.kajabuyahmis.data.ReportItemType;
import com.kajabuyahmis.data.SessionNumberType;
import com.kajabuyahmis.data.Sex;
import com.kajabuyahmis.data.MessageType;
import com.kajabuyahmis.data.Title;
import com.kajabuyahmis.data.hr.DayType;
import com.kajabuyahmis.data.hr.LeaveType;
import com.kajabuyahmis.data.hr.PaysheetComponentType;
import com.kajabuyahmis.data.hr.Times;
import com.kajabuyahmis.data.inward.AdmissionTypeEnum;
import com.kajabuyahmis.data.inward.InwardChargeType;
import com.kajabuyahmis.data.inward.PatientEncounterComponentType;
import com.kajabuyahmis.data.lab.Priority;
import com.kajabuyahmis.entity.PaymentScheme;
import com.kajabuyahmis.entity.Person;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class EnumController implements Serializable {

    private PaymentScheme paymentScheme;

    SessionNumberType[] sessionNumberTypes;

    public Priority[] getPriorities() {
        return Priority.values();
    }

    public Dashboard[] getDashboardTypes() {
        return Dashboard.values();
    }

    public SessionNumberType[] getSessionNumberTypes() {
        sessionNumberTypes = SessionNumberType.values();
        return sessionNumberTypes;
    }

    public CssVerticalAlign[] getCssVerticalAlign() {
        return CssVerticalAlign.values();
    }

    public DepartmentListMethod[] getDepartmentListMethods() {
        return DepartmentListMethod.values();
    }

    public DepartmentType[] getDepartmentType() {
        return DepartmentType.values();
    }

    public ApplicationInstitution[] getApplicationInstitutions() {
        return ApplicationInstitution.values();
    }

    public PaperType[] getPaperTypes() {
        return PaperType.values();
    }

    public ReportItemType[] getReportItemTypes() {
        Person p;
        return ReportItemType.values();
    }

    public LeaveType[] getLeaveType() {
        LeaveType[] ltp = {LeaveType.Annual,
            LeaveType.AnnualHalf,
            LeaveType.Casual,
            LeaveType.CasualHalf,
            LeaveType.DutyLeave,
            LeaveType.DutyLeaveHalf,
            LeaveType.Lieu,
            LeaveType.LieuHalf,
            LeaveType.Maternity1st,
            LeaveType.Maternity2nd,
            LeaveType.Medical,
            LeaveType.No_Pay,
            LeaveType.No_Pay_Half};
        return ltp;
    }

    public Times[] getTimeses() {
        return new Times[]{Times.inTime, Times.outTime};
    }

    public void setSessionNumberTypes(SessionNumberType[] sessionNumberTypes) {
        this.sessionNumberTypes = sessionNumberTypes;
    }

    public FeeType[] getFeeTypes() {
        return FeeType.values();
    }

    public DayType[] getDayTypes() {
        return DayType.values();
    }

    public DayType[] getDayTypeForShift() {
        DayType[] dayTypes = {DayType.Normal, DayType.DayOff, DayType.SleepingDay, DayType.Poya};

        return dayTypes;
    }

    public DayType[] getDayTypesForPh() {
        DayType[] dayTypes = {DayType.MurchantileHoliday, DayType.Poya, DayType.PublicHoliday};

        return dayTypes;
    }

    public InvestigationItemType[] getInvestigationItemTypes() {
        return InvestigationItemType.values();
    }

    public InvestigationItemValueType[] getInvestigationItemValueTypes() {
        return InvestigationItemValueType.values();
    }

    public PaysheetComponentType[] getAddingComponentTypes() {
        return PaysheetComponentType.addition.children();

    }

    public BillType[] getBillTypes() {
        return BillType.values();
    }

    public BillClassType[] getBillClassTypes() {
        return BillClassType.values();
    }

    public CalculationType[] getCalculationTypes() {
        return CalculationType.values();
    }

    public PaysheetComponentType[] getDiductionComponentTypes() {
        return PaysheetComponentType.subtraction.children();

    }

    public PaysheetComponentType[] getPaysheetComponentTypes() {
        List<PaysheetComponentType> list = new ArrayList<>();

        for (PaysheetComponentType pct : PaysheetComponentType.addition.children()) {
            list.add(pct);
        }

        for (PaysheetComponentType pct : PaysheetComponentType.subtraction.children()) {
            list.add(pct);
        }

        return list.toArray(new PaysheetComponentType[list.size()]);
    }

    public List<PaysheetComponentType> getPaysheetComponentTypesUserDefinded() {
        return PaysheetComponentType.addition.getUserDefinedComponents();
    }

    public List<PaysheetComponentType> getPaysheetComponentTypesSystemDefinded() {
        return PaysheetComponentType.addition.getSystemDefinedComponents();
    }

    public Title[] getTitle() {
        return Title.values();
    }

    public Title[] getTitleDoctor() {
        Title[] tem = {
            Title.Dr,
            Title.DrMrs,
            Title.DrMs,
            Title.DrMiss,
            Title.Prof,
            Title.ProfMrs,
            Title.Mr,
            Title.Ms,
            Title.Miss,
            Title.Mrs,
            Title.Other,};
        return tem;
    }

    public Sex[] getSex() {
        return Sex.values();
    }

    public Sex[] getGender() {
        Sex[] sexes = {Sex.Male, Sex.Female};
        return sexes;
    }

    public PaymentMethod[] getPaymentMethodForAdmission() {
        PaymentMethod[] tmp = {PaymentMethod.Credit, PaymentMethod.Cash};
        return tmp;
    }

    public InwardChargeType[] getInwardChargeTypes() {
        return InwardChargeType.values();
    }

    public InwardChargeType[] getInwardChargeTypesForSetting() {
        InwardChargeType[] b = {
            InwardChargeType.AdmissionFee,
            InwardChargeType.Medicine,
            InwardChargeType.BloodTransfusioncharges,
            InwardChargeType.Immunization,
            InwardChargeType.Equipment,
            InwardChargeType.MealCharges,
            InwardChargeType.OperationTheatreCharges,
            InwardChargeType.OperationTheatreNursingCharges,
            InwardChargeType.OperationTheatreMachineryCharges,
            InwardChargeType.LarbourRoomCharges,
            InwardChargeType.ETUCharges,
            InwardChargeType.TreatmentCharges,
            InwardChargeType.IntensiveCareManagement,
            InwardChargeType.AmbulanceCharges,
            InwardChargeType.HomeVisiting,
            InwardChargeType.GeneralIssuing,
            InwardChargeType.WardProcedures,
            InwardChargeType.ReimbursementCharges,
            InwardChargeType.DressingCharges,
            InwardChargeType.OxygenCharges,
            InwardChargeType.physiotherapy,
            InwardChargeType.Laboratory,
            InwardChargeType.X_Ray,
            InwardChargeType.CT,
            InwardChargeType.Scanning,
            InwardChargeType.ECG_EEG,
            InwardChargeType.MedicalServices,
            InwardChargeType.AdministrationCharge,
            InwardChargeType.LinenCharges,
            InwardChargeType.MaintainCharges,
            InwardChargeType.MedicalCareICU,
            InwardChargeType.MOCharges,
            InwardChargeType.NursingCharges,
            InwardChargeType.RoomCharges,
            InwardChargeType.CardiacMonitoring,
            InwardChargeType.Nebulisation,
            InwardChargeType.Echo,
            InwardChargeType.SyringePump,
            InwardChargeType.TheaterConsumbale,
            InwardChargeType.ExerciseECG,
            InwardChargeType.TheaterConsumbale,
            InwardChargeType.VAT,
            InwardChargeType.EyeLence,
            InwardChargeType.AccessoryCharges,
            InwardChargeType.HospitalSupportService,
            InwardChargeType.ExtraMedicine,
            InwardChargeType.DialysisTreatment,
            InwardChargeType.OtherCharges};

        return b;
    }

    public PatientEncounterComponentType[] getPatientEncounterComponentTypes() {
        return PatientEncounterComponentType.values();
    }

    public BillType[] getCashFlowBillTypes() {
        BillType[] b = {
            BillType.OpdBill,
            BillType.PaymentBill,
            BillType.PettyCash,
            BillType.CashRecieveBill,
            BillType.AgentPaymentReceiveBill,
            BillType.InwardPaymentBill,
            BillType.PharmacySale,
            BillType.ChannelCash,
            BillType.ChannelPaid,
            BillType.GrnPaymentPre,
            BillType.CollectingCentrePaymentReceiveBill,//            BillType.PharmacyPurchaseBill,
        //            BillType.GrnPayment,
        };

        return b;
    }

    public BillType[] getCashFlowBillTypesCashier() {
        BillType[] b = {
            BillType.OpdBill,
            BillType.PaymentBill,
            BillType.PettyCash,
            BillType.CashRecieveBill,
            BillType.AgentPaymentReceiveBill,
            BillType.InwardPaymentBill,
            BillType.PharmacySale,
            BillType.GrnPaymentPre,
            BillType.CollectingCentrePaymentReceiveBill,};

        return b;
    }

    public BillType[] getCashFlowBillTypesChannel() {
        BillType[] b = {
            BillType.ChannelCash,
            BillType.ChannelPaid,
            BillType.ChannelProPayment,
            BillType.ChannelIncomeBill,
            BillType.ChannelExpenesBill,};

        return b;
    }

    public BillType[] getStoreBillTypes() {

        BillType[] b = {
            BillType.StoreGrnBill,
            BillType.StoreGrnReturn,
            BillType.StoreOrder,
            BillType.StoreOrderApprove,
            BillType.StorePre,
            BillType.StorePurchase,
            BillType.StoreSale,
            BillType.StoreAdjustment,
            BillType.StorePurchaseReturn,
            BillType.StoreTransferRequest,
            BillType.StoreTransferIssue,};

        return b;
    }

    public BillType[] getPharmacyBillTypes() {
        BillType[] b = {
            BillType.PharmacyGrnBill,
            BillType.PharmacyGrnReturn,
            BillType.PharmacyOrder,
            BillType.PharmacyOrderApprove,
            BillType.PharmacyPre,
            BillType.PharmacyPurchaseBill,
            BillType.PharmacySale,
            BillType.PharmacyAdjustment,
            BillType.PurchaseReturn,
            BillType.GrnPayment,
            BillType.PharmacyTransferRequest,
            BillType.PharmacyTransferIssue,
            BillType.PharmacyWholeSale,
            BillType.PharmacyIssue,
            BillType.PharmacyTransferReceive};

        return b;
    }

    public BillType[] getPharmacyBillTypes2() {
        BillType[] b = {
            BillType.PharmacySale,
            BillType.PharmacyAdjustment,
            BillType.PharmacyTransferIssue,
            BillType.PharmacyIssue,
            BillType.PharmacyBhtPre};

        return b;
    }

    public BillType[] getPharmacyBillTypesForMovementReports() {
        BillType[] b = {
            BillType.PharmacySale,
            BillType.PharmacyAdjustment,
            BillType.PharmacyTransferIssue,
            BillType.PharmacyIssue,
            BillType.PharmacyBhtPre};
        return b;
    }

    public BillType[] getPharmacyBillTypes3() {
        BillType[] b = {
            BillType.PharmacyPre,
            BillType.PharmacyWholesalePre,
            BillType.PharmacyAdjustment,
            BillType.PharmacyTransferIssue,
            BillType.PharmacyIssue,
            BillType.PharmacyBhtPre};

        return b;
    }

    public BillType[] getPharmacySaleBillTypes() {
        BillType[] bt = {
            BillType.PharmacySale,
            BillType.PharmacyWholeSale,};
        return bt;
    }

    public PaymentMethod[] getPaymentMethods() {
        PaymentMethod[] p = {
            PaymentMethod.Cash,
            PaymentMethod.Card,
            PaymentMethod.Cheque,
            PaymentMethod.Slip,
            PaymentMethod.Credit,};

        return p;
    }

    public PaymentMethod[] getCollectingCentrePaymentMethods() {
        PaymentMethod[] p = {
            PaymentMethod.Agent,};
        return p;
    }

    public PaymentMethod[] getPaymentMethodsWithoutCredit() {
        PaymentMethod[] p = {PaymentMethod.Cash,
            PaymentMethod.Card,
            PaymentMethod.Cheque,
            PaymentMethod.Slip};

        return p;
    }

    public PaymentMethod[] getPaymentMethodsForPo() {
        PaymentMethod[] p = {PaymentMethod.Cash, PaymentMethod.Credit};

        return p;
    }

    public PaymentMethod[] getPaymentMethodsForChannel() {
        PaymentMethod[] p = {PaymentMethod.OnCall, PaymentMethod.Cash, PaymentMethod.Agent, PaymentMethod.Staff, PaymentMethod.Card, PaymentMethod.Cheque, PaymentMethod.Slip};
        return p;
    }

    public PaymentMethod[] getPaymentMethodsForChannelSettle() {
        PaymentMethod[] p = {PaymentMethod.Cash, PaymentMethod.Card};
        return p;
    }

    public PaymentMethod[] getPaymentMethodsForChannelAgentSettle() {
        PaymentMethod[] p = {PaymentMethod.Cash, PaymentMethod.Agent};
        return p;
    }

    public BillType[] getChannelType() {
        BillType[] bt = {BillType.Channel, BillType.XrayScan};
        return bt;
    }

//    public boolean checkPaymentScheme(PaymentScheme scheme, String paymentMathod) {
//        if (scheme != null && scheme.getPaymentMethod() != null) {
//            //System.err.println("Payment Scheme : " + scheme.getPaymentMethod());
//            //System.err.println("Payment Method : " + PaymentMethod.valueOf(paymentMathod));
//            if (scheme.getPaymentMethod().equals(PaymentMethod.valueOf(paymentMathod))) {
//                //System.err.println("Returning True");
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        return false;
//
//    }
//    public boolean checkPaymentScheme(String paymentMathod) {
//        if (getPaymentScheme() != null && getPaymentScheme().getPaymentMethod() != null) {
//            //System.err.println("Payment Scheme : " +getPaymentScheme().getPaymentMethod());
//            //System.err.println("Payment Method : " + PaymentMethod.valueOf(paymentMathod));
//            if (getPaymentScheme().getPaymentMethod().equals(PaymentMethod.valueOf(paymentMathod))) {
//                //System.err.println("Returning True");
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        return false;
//
//    }
    public boolean checkPaymentMethod(PaymentMethod paymentMethod, String paymentMathodStr) {
        if (paymentMethod != null) {
            //System.err.println("Payment method : " + paymentMethod);
            //System.err.println("Payment Method String : " + PaymentMethod.valueOf(paymentMathodStr));
            if (paymentMethod.equals(PaymentMethod.valueOf(paymentMathodStr))) {
                //System.err.println("Returning True");
                return true;
            } else {
                return false;
            }
        }

        return false;

    }

    public AdmissionTypeEnum[] getAdmissionTypeEnum() {
        return AdmissionTypeEnum.values();
    }

    public MessageType[] getSmsType() {
        return MessageType.values();
    }

    /**
     * Creates a new instance of EnumController
     */
    public EnumController() {
    }

    public PaymentScheme getPaymentScheme() {
        return paymentScheme;
    }

    public void setPaymentScheme(PaymentScheme paymentScheme) {
        this.paymentScheme = paymentScheme;
    }

}
