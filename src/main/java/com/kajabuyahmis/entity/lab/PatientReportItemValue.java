/*
* Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.lab;

import com.kajabuyahmis.entity.Patient;
import com.kajabuyahmis.entity.PatientEncounter;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author Buddhika
 */
@Entity
public class PatientReportItemValue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    Patient patient;
    @ManyToOne
    PatientEncounter patientEncounter;
    @ManyToOne
    InvestigationItem investigationItem;
    @ManyToOne
    PatientReport patientReport;
    String strValue;
    @Lob
    private String lobValue;
    @Lob
    byte[] baImage;
    String fileName;
    String fileType;
    Double doubleValue;

    @Transient
    private String value;

    public String getStrValue() {
        if (strValue != null) {
            strValue = strValue.trim();
        }
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public String getLobValue() {
        return lobValue;
    }

    public void setLobValue(String lobValue) {
        this.lobValue = lobValue;
    }

    public byte[] getBaImage() {
        return baImage;
    }

    public void setBaImage(byte[] baImage) {
        this.baImage = baImage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Patient getPatient() {
        //////// // System.out.println("");
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public PatientEncounter getPatientEncounter() {
        return patientEncounter;
    }

    public void setPatientEncounter(PatientEncounter patientEncounter) {
        this.patientEncounter = patientEncounter;
    }

    public InvestigationItem getInvestigationItem() {
        return investigationItem;
    }

    public void setInvestigationItem(InvestigationItem investigationItem) {
        this.investigationItem = investigationItem;
    }

    public PatientReport getPatientReport() {
        return patientReport;
    }

    public void setPatientReport(PatientReport patientReport) {
        this.patientReport = patientReport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof PatientReportItemValue)) {
            return false;
        }
        PatientReportItemValue other = (PatientReportItemValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kajabuyahmis.entity.PatientInvestigationItemValue[ id=" + id + " ]";
    }

    public String getValue() {
        if(this.investigationItem==null || this.investigationItem.ixItemValueType==null){
            return "";
        }
        switch (this.investigationItem.ixItemValueType) {
            case Double:
            case Long:
                if (this.doubleValue == null) {
                    value = "";
                } else {
                    value = Double.toString(this.doubleValue);
                }
                break;
            case Varchar:
                value = this.strValue;
                break;
            case Memo:
                value = this.lobValue;
                break;
            default:
                value = this.investigationItem.ixItemValueType.toString();
        }
        return value;
    }
}
