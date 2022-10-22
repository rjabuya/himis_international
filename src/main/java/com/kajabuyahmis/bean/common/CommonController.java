/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author buddhika
 */
@Named(value = "commonController")
@SessionScoped
public class CommonController implements Serializable {

    @Inject
    private SessionController sessionController;

    /**
     * Creates a new instance of CommonController
     */
    public CommonController() {
    }

    private int number;
 
    public int getNumber() {
        return number;
    }
 
    public void increment() {
        number++;
    }
    
    public String getBaseUrl() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        return url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
    }

    public Date getCurrentDateTime() {
        return new Date();
    }

    public boolean renderPaginator(List<Object> list, int count) {
        boolean render = false;
        if (list == null) {
            return render;
        }
        if (list.size() > count) {
            render = true;
        }
        return render;
    }

    public Date getDateAfterThreeMonthsCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(CommonFunctionsController.getEndOfDay(new Date()));
//        //// // System.out.println("1.cal.getTime() = " + cal.getTime());
        cal.add(Calendar.MONTH, 3);
//        //// // System.out.println("2.cal.getTime() = " + cal.getTime());
        return cal.getTime();
    }

    public boolean sameDate(Date date1, Date date2) {
        Calendar d1 = Calendar.getInstance();
        d1.setTime(date1);
        DateTime first = new DateTime(date1);
        DateTime second = new DateTime(date2);
        LocalDate firstDate = first.toLocalDate();
        LocalDate secondDate = second.toLocalDate();
        return firstDate.equals(secondDate);
    }

    public Date retiermentDate(Date dob) {
        if (dob == null) {
            dob = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dob);
        cal.add(Calendar.YEAR, 50);
        return cal.getTime();
    }

    public double dateDifferenceInMinutes(Date fromDate, Date toDate) {
        long timeInMs = toDate.getTime() - fromDate.getTime();
        return timeInMs / (1000 * 60);
    }

    public String shortDate(Date date) {
        SimpleDateFormat dt1 = new SimpleDateFormat("dMMMyy");
        return (dt1.format(date));
    }

    public double dateDifferenceInSeconds(Date fromDate, Date toDate) {
        long timeInMs = toDate.getTime() - fromDate.getTime();
        return timeInMs / 1000;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public void printReportDetails(Date fromDate, Date toDate, Date startTime, String url) {

        String s;
        s = "***************";
        s += "\n Report User :" + getSessionController().getLoggedUser().getWebUserPerson().getName();
        s += "\n Report Description : " + url;
        if (fromDate != null) {
            s += "\n Report Form :" + fromDate;
        }
        if (toDate != null) {
            s += " To :" + toDate;
        }
        s += "\n Report Start Time : " + startTime + " End Time :" + new Date();
        if (fromDate != null && toDate != null) {
            s += "\n Time Defferent : " + dateDifferenceInMinutes(fromDate, toDate);
        }
        if (startTime != null) {
            s += "\n Report Time Defferent(Miniuts) : " + dateDifferenceInMinutes(startTime, new Date());
            s += "\n Report Time Defferent(Seconds) : " + dateDifferenceInSeconds(startTime, new Date());
        }
        s += "\n ***************";


    }

    //----------Date Time Formats
    public String getDateFormat(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("YYYY-MM-dd");
        s = d.format(date);
        return s;
    }

    public String getDateFormat(Date date, String formatString) {
        String s = "";
        DateFormat d = new SimpleDateFormat(formatString);
        s = d.format(date);
        return s;
    }
    
    public String getDateFormat2(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("YYYY-MMM-dd");
        s = d.format(date);
        return s;
    }

    public String getTimeFormat12(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("hh:mm:ss a");
        s = d.format(date);
        return s;
    }

    public String getTimeFormat24(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("HH:mm:ss");
        s = d.format(date);
        return s;
    }

    public String getDateTimeFormat12(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss a");
        s = d.format(date);
        return s;
    }

    public String getDateTimeFormat24(Date date) {
        String s = "";
        DateFormat d = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        s = d.format(date);
        return s;
    }

    public Date getConvertDateTimeFormat24(String dateString) throws ParseException {
        DateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        Date date = d.parse(dateString);
//        //// // System.out.println("date = " + date +" ~ dateString = " + dateString);
        return date;
    }

    public String getDouble(double d) {
        String s = "";
        NumberFormat myFormatter = new DecimalFormat("##0.00");
        s = myFormatter.format(d);
//        //// // System.out.println("s = " + s);
        return s;
    }
    
    public Double getDouble(String s) {
        Double d =null;
        if(s==null){
            return d;
        }
        try{
            d=Double.parseDouble(s);
        }catch(NumberFormatException e){
            d=0.0;
        }
        return d;
    }

    //----------Date Time Formats
    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

}
