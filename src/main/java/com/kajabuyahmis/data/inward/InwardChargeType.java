/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.inward;

/**
 *
 * @author Buddhika
 */
public enum InwardChargeType {

    AdmissionFee("Admission Fee"),
    AmbulanceCharges("Ambulance Charges"),
    AdministrationCharge("Administration Fee"),//GOES WITH ROOM CHARGE
    BloodTransfusioncharges("Blood Transfusion Charges"),
    CT("CT Scan"),
    DressingCharges("Dressing Charges"),
    Equipment("Equipment Charges"),
    ECG_EEG("ECG/EEG"),//"ECG/EEG/ECHO/EXECG" request by piumini
    ETUCharges("ETU Charges"),
    GeneralIssuing("General Issuing"),
    HomeVisiting("Home Visiting"),
    Immunization("Immunization"),
    IntensiveCareManagement("Intensive Care Management"),
    LarbourRoomCharges("Larbour Room Charges"),
    LinenCharges("Linen Charges"),//GOES WITH PATIENT ROOM
    Laboratory("Laboratory Charges"),
    MealCharges("Meal Charges"),
    MedicalCareICU("Medical Care"),//Goes With Room
    MedicalServices("Medical Services"),
    Medicine("Medicine"),//For BHT ISSUE
    MOCharges("MO Charges"),//GOES WITH PATIENT ROOM
    MaintainCharges("Maintain Charges"),//GOES WITH PATIENT ROOM    
    DoctorAndNurses("Assisting Charge"),//Set Doctor && Nurse Fees
    NursingCharges("Nursing Care"),//GOES WITH PATIENT ROOM
    OxygenCharges("Oxygen Charges"),
    OtherCharges("Other Charges"),
    OperationTheatreCharges("Operation Theatre Charges"),
    ProfessionalCharge("Professional Charge"),//Only for Consultant Fees
    ReimbursementCharges("Reimbursement Charges"),
    RoomCharges("Room Charges"),//GOES WITH PATIENT ROOM
    physiotherapy("Physiotherapy"),
    Scanning("Scanning"),
    TreatmentCharges("Treatment Charges"),
    X_Ray("X-Ray"),
    WardProcedures("Surgical Procedures"),
    CardiacMonitoring("Holter Monitoring"),
    Nebulisation("Nebulisation"),
    Echo("Echo"),
    SyringePump("Syringe Pump"),
    ExerciseECG("Exercise E.C.G"),
    OperationTheatreNursingCharges("Operation Theatre Nursing Charges"),
    OperationTheatreMachineryCharges("Operation Theatre Machinery Charges"),
    TheaterConsumbale("Theater Consumables & Drugs"),
    VAT("VAT (15%)"),
    AccessoryCharges("Endoscopy Charges"),
    EyeLence("Opthalmic Technician & Lense Fee"),
    HospitalSupportService("Hospital Support Service"),
    ExtraMedicine("Extra Medicine"),
    DialysisTreatment("Dialysis Treatment Charges"),
    @Deprecated
    Investigations("Investigations"),
    @Deprecated
    MedicalCare("MedicalCare")
    
    
    
    ;

    private final String nameAsString;

    private InwardChargeType(String nameAsString) {
        this.nameAsString = nameAsString;
    }

    @Override
    public String toString() {
        return nameAsString;
    }

    public String getLabel() {
        return toString();
    }
}
