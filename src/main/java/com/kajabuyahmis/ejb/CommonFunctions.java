package com.kajabuyahmis.ejb;

import com.kajabuyahmis.data.dataStructure.DateRange;
import com.kajabuyahmis.data.dataStructure.YearMonthDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.ejb.Singleton;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 * @author Buddhika
 */
@Singleton
public class CommonFunctions {

    public DateRange getDateRangeForOT(Date date) {
        DateRange dateRange = new DateRange();
        Date startOfThisMonth = com.kajabuyahmis.java.CommonFunctions.getStartOfMonth(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startOfThisMonth);
        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date startOfPrevMonth = com.kajabuyahmis.java.CommonFunctions.getStartOfMonth(cal.getTime());
        Date from = getFirstDayOfWeek(startOfPrevMonth);
        Date endOfPrevMonth = com.kajabuyahmis.java.CommonFunctions.getEndOfMonth(cal.getTime());
        Date to = getFirstDayOfWeek(endOfPrevMonth);
        cal.setTime(endOfPrevMonth);
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            cal.setTime(to);
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }

        to = cal.getTime();

        dateRange.setFromDate(from);
        dateRange.setToDate(to);

        return dateRange;
    }

    public DateRange getDateRange(Date from, int range) {
        DateRange dateRange = new DateRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DATE, range);
        Date to = cal.getTime();

        dateRange.setFromDate(from);
        dateRange.setToDate(to);

        //System.err.println("From " + dateRange.getFromDate());
        //System.err.println("To " + dateRange.getToDate());
        return dateRange;
    }
    
    public static String formatDate(Date date, String pattern) {
        String dateString = "";
        if (date == null) {
            return dateString;
        }
        if (pattern == null || pattern.trim().equals("")) {
            pattern = "dd-MM-yyyy";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dateString = simpleDateFormat.format(date);
        return dateString;
    }

    public static Date parseDate(String dateInString, String format) {
        if (format == null || format.trim().equals("")) {
            format = "dd MM yyyy";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        Date date;
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException ex) {
            date = null;
        }
        return date;
    }


    public boolean checkToDateAreInSameDay(Date firstDate, Date secondDate) {

        Date startOfDay = getStartOfDay(firstDate);
        Date endOfDay = getEndOfDay(firstDate);

        if (startOfDay.before(secondDate) && endOfDay.after(secondDate)) {
            return true;
        } else {
            return false;
        }
    }

    public Date getAddedDate(Date date, int range) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, range);
        Date returnDate = cal.getTime();

        return returnDate;
    }

    public Long getDayCountTillNow(Date date) {
        if (date == null) {
            return 0l;
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);

        Long inDays = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        //System.err.println("INDAYS "+inDays);
        return inDays;

    }

    public Long getDayCount(Date frm, Date to) {
        if (frm == null) {
            return 0l;
        }

        if (to == null) {
            to = new Date();
        }

        to = getEndOfDay(to);
        frm = getStartOfDay(frm);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(to);
        cal2.setTime(frm);

//        System.err.println("cal 1 " + cal1.getTimeInMillis());
//        System.err.println("cal 2 " + cal2.getTimeInMillis());
//        System.err.println("Frm " + frm);
//        System.err.println("TO " + to);
        Long inDays = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 60 * 60 * 24);
//        System.err.println("INDAYS " + inDays);

        //we need to 1 because date rangs is missing one day as it between days
        inDays++;
        return inDays;

    }

    public Date guessDob(String docStr) {
        //////// // System.out.println("year string is " + docStr);
        try {
            int years = Integer.valueOf(docStr);
            //////// // System.out.println("int year is " + years);
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
            //////// // System.out.println("now before is " + now);
            now.add(Calendar.YEAR, -years);
            //////// // System.out.println("now after is " + now);
            //////// // System.out.println("now time is " + now.getTime());
            return now.getTime();
        } catch (Exception e) {
            //////// // System.out.println("Error is " + e.getMessage());
            return new Date();

        }
    }

    public Date guessDob(YearMonthDay yearMonthDay) {
        // //////// // System.out.println("year string is " + docStr);
        int years = 0;
        int month = 0;
        int day = 0;
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        try {
            if (yearMonthDay.getYear() != null && !yearMonthDay.getYear().isEmpty()) {
                years = Integer.valueOf(yearMonthDay.getYear());
                now.add(Calendar.YEAR, -years);
            }

            if (yearMonthDay.getMonth() != null && !yearMonthDay.getMonth().isEmpty()) {
                month = Integer.valueOf(yearMonthDay.getMonth());
                now.add(Calendar.MONTH, -month);
            }

            if (yearMonthDay.getDay() != null && !yearMonthDay.getDay().isEmpty()) {
                day = Integer.valueOf(yearMonthDay.getDay());
                now.add(Calendar.DATE, -day);
            }

            return now.getTime();
        } catch (Exception e) {
            //////// // System.out.println("Error is " + e.getMessage());
            return new Date();

        }
    }

    public Date guessDobFromMonth(String docStr) {
        //////// // System.out.println("year string is " + docStr);
        try {
            int month = Integer.valueOf(docStr);
            //////// // System.out.println("int month is " + month);
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
            //////// // System.out.println("now before is " + now);
            now.add(Calendar.MONTH, -month);
            //////// // System.out.println("now after is " + now);
            //////// // System.out.println("now time is " + now.getTime());
            return now.getTime();
        } catch (Exception e) {
            //////// // System.out.println("Error is " + e.getMessage());
            return new Date();

        }
    }

    public String calculateAge(Date dob, Date toDate) {
        if (dob == null || toDate == null) {
            return "";
        }
        long ageInDays;
        ageInDays = (toDate.getTime() - dob.getTime()) / (1000 * 60 * 60 * 24);
        if (ageInDays < 0) {
            return "";
        }
        //////// // System.out.println("Age in days " + ageInDays);
        if (ageInDays < 60) {
            return ageInDays + " days";
        } else if (ageInDays < 366) {
            return (ageInDays / 30) + " months";
        } else {
            return (ageInDays / 365) + " years";
        }
    }

    public long calculateAgeInDays(Date dob, Date toDate) {
        if (dob == null || toDate == null) {
            return 0l;
        }
        long ageInDays;
        ageInDays = (toDate.getTime() - dob.getTime()) / (1000 * 60 * 60 * 24);
        if (ageInDays < 0) {
            ageInDays = 0;
        }
        return ageInDays;
    }

    public long calculateDurationTime(Date dob, Date toDate) {
        if (dob == null || toDate == null) {
            return 0;
        }
        long durationHours;
        durationHours = (toDate.getTime() - dob.getTime()) / (1000 * 60 * 60);
        return durationHours;
    }

    public long calculateDurationMin(Date dob, Date toDate) {
        if (dob == null || toDate == null || dob.getTime() > toDate.getTime()) {
            return 0;
        }
        long dMin;
        dMin = (toDate.getTime() - dob.getTime()) / (1000 * 60);
        return dMin;
    }

    public static Date getStartOfDay() {
        return getStartOfDay(new Date());
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        ////// // System.out.println("calendar.getTime() = " + calendar.getTime());
        return calendar.getTime();
    }
    
     public static LocalDateTime getLocalDateTime(Date dateTime){
        Date input = dateTime;
        LocalDateTime date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    public static Date getStartOfMonth() {
        return getStartOfMonth(new Date());
    }

    public static Date getStartOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, 1, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay() {
        return getEndOfDay(new Date());
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        ////// // System.out.println("calendar.getTime() = " + calendar.getTime());
        return calendar.getTime();
    }

    public static Date getEndOfMonth() {
        return getEndOfMonth(new Date());
    }

    public static Date getEndOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);

        calendar.add(Calendar.DATE, -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }

    public Date getFirstDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        //////// // System.out.println("First : " + cal.getTime());
        return cal.getTime();
    }

    public Date getFirstDayOfYear() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime();
    }

    public Date getLastDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DATE, 31);
        //////// // System.out.println("Last : " + cal.getTime());
        return cal.getTime();
    }

    public Date getLastDayOfYear() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DATE, 31);
        cal.set(cal.get(Calendar.YEAR), 11, 31, 23, 59, 59);
        return cal.getTime();
    }

    public Date getFirstDayOfWeek(Date date) {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        //      //////// // System.out.println("Start of this week:       " + cal.getTime());
        //       //////// // System.out.println("... in milliseconds:      " + cal.getTimeInMillis());

// start of the next week
//        cal.add(Calendar.WEEK_OF_YEAR, 1);
//        //////// // System.out.println("Start of the next week:   " + cal.getTime());
//        //////// // System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
        return cal.getTime();
    }

    public Date getLastDayOfWeek(Date date) {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        //   //////// // System.out.println("Start of this week:       " + cal.getTime());
        //     //////// // System.out.println("... in milliseconds:      " + cal.getTimeInMillis());

        cal.add(Calendar.DATE, 7);

// start of the next week
//        cal.add(Calendar.WEEK_OF_YEAR, 1);
//        //////// // System.out.println("Start of the next week:   " + cal.getTime());
//        //////// // System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
        return cal.getTime();
    }
    
    public static Date getStartOfBeforeDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        ////// // System.out.println("calendar.getTime() = " + calendar.getTime());
        return calendar.getTime();
    }

    public static Date removeTime(Date date) {
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //      System.err.println("Only Date " + cal.getTime());
        return cal.getTime();
    }

    public static double roundToTwoDecimalPlaces(double num) {
        return roundToTwoDecimalPlaces(num, 2);
    }

    public static double roundToTwoDecimalPlaces(double num, int decimalPlaces) {
        double mul = Math.pow(10, decimalPlaces);
        double roundOff = (double) Math.round(num * mul) / mul;
        return roundOff;
    }

    public YearMonthDay guessAge(Date dofb) {
        YearMonthDay yearMonthDay=new YearMonthDay();
        
//        Calendar cal=Calendar.getInstance();
//        cal.setTime(dob);
//        //// // System.out.println("cal.get(Calendar.YEAR) = " + cal.get(Calendar.YEAR));
//        //// // System.out.println("cal.get(Calendar.MONTH) = " + cal.get(Calendar.MONTH)+1);
//        //// // System.out.println("cal.get(Calendar.DATE) = " + cal.get(Calendar.DATE));
//        LocalDate birthDay=new LocalDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
//        LocalDate now=new LocalDate();
        
        LocalDate dob = new LocalDate(dofb);
        LocalDate date = new LocalDate(new Date());

        Period period = new Period(dob, date, PeriodType.yearMonthDay());
        int ageYears = period.getYears();
        int ageMonths = period.getMonths();
        int ageDays = period.getDays();
        
//        Years years=Years.yearsBetween(birthDay, now);
//        Months months=Months.monthsBetween(birthDay, now);
//        Days days=Days.daysBetween(birthDay, now);
//        //// // System.out.println("years.getYears() = " + years.getYears());
        yearMonthDay.setYear(Integer.toString(ageYears));
//        //// // System.out.println("months.getMonths() = " + months.getMonths());
        yearMonthDay.setMonth(Integer.toString(ageMonths));
//        //// // System.out.println("days.getDays() = " + days.getDays());
        yearMonthDay.setDay(Integer.toString(ageDays));
        
        return yearMonthDay;
    }

}
