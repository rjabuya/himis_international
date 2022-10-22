/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.entity.hr;

import com.kajabuyahmis.entity.Staff;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class AmendmentForm extends HrForm {

    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate;
    @ManyToOne
    private Staff fromStaff;
    @ManyToOne
    private Staff toStaff;
    @ManyToOne
    private StaffShift fromStaffShift;
    @ManyToOne
    StaffShift fromStaffShiftSecond;
    @ManyToOne
    private StaffShift toStaffShift;
    @ManyToOne
    private StaffShift toStaffShiftSecond;
    @ManyToOne
    Shift toShift;
    @ManyToOne
    Shift toShiftSecond;

    public Shift getToShiftSecond() {
        return toShiftSecond;
    }

    public void setToShiftSecond(Shift toShiftSecond) {
        this.toShiftSecond = toShiftSecond;
    }
    
    

    public StaffShift getFromStaffShiftSecond() {
        return fromStaffShiftSecond;
    }

    public void setFromStaffShiftSecond(StaffShift fromStaffShiftSecond) {
        this.fromStaffShiftSecond = fromStaffShiftSecond;
    }

    public StaffShift getToStaffShiftSecond() {
        return toStaffShiftSecond;
    }

    public void setToStaffShiftSecond(StaffShift toStaffShiftSecond) {
        this.toStaffShiftSecond = toStaffShiftSecond;
    }
    
    

    public Shift getToShift() {
        return toShift;
    }

    public void setToShift(Shift toShift) {
        this.toShift = toShift;
    }

    public Staff getFromStaff() {
        return fromStaff;
    }

    public void setFromStaff(Staff fromStaff) {
        this.fromStaff = fromStaff;
    }

    public Staff getToStaff() {
        return toStaff;
    }

    public void setToStaff(Staff toStaff) {
        this.toStaff = toStaff;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public StaffShift getFromStaffShift() {
        return fromStaffShift;
    }

    public void setFromStaffShift(StaffShift fromStaffShift) {
        this.fromStaffShift = fromStaffShift;
    }

    public StaffShift getToStaffShift() {
        return toStaffShift;
    }

    public void setToStaffShift(StaffShift toStaffShift) {
        this.toStaffShift = toStaffShift;
    }

}
