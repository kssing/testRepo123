package com.power2sme.dms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtility {
	static Logger logger = Logger.getLogger(DateUtility.class);
	static SimpleDateFormat sdfDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");
	static SimpleDateFormat sdfDDMMYYYY1 = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sdfYYYYMMDD1 = new SimpleDateFormat("yyyy/MM/dd");
    static SimpleDateFormat sdfDDMMYY = new SimpleDateFormat("dd/MM/yy");
	static SimpleDateFormat timstampFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");	
	static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
    static SimpleDateFormat sdfddMMMMyyyy = new SimpleDateFormat("dd MMMM yyyy");
    
   public static int diff(Date date1, Date date2) {
       Calendar c1 = Calendar.getInstance();
       Calendar c2 = Calendar.getInstance();

       c1.setTime(date1);
       c2.setTime(date2);
       int diffDay = 0;

       if (c1.before(c2)) {
           diffDay = 0;//countDiffDay(c1, c2);
       } else {
           diffDay = countDiffDay(c2, c1);
       }
       return diffDay;
   }
   
   
   public static int countDayDiff(Date date1, Date date2) {
       Calendar c1 = Calendar.getInstance();
       Calendar c2 = Calendar.getInstance();

       c1.setTime(date1);
       c2.setTime(date2);
       int diffDay = 0;

       if (c1.before(c2)) {
           diffDay = countDiffDay(c1, c2);
       } else {
           diffDay = countDiffDay(c2, c1);
           diffDay = -diffDay;
       }
       return diffDay;
   }

   
   public static int countdiffInDays(Date date1, Date date2) {
       Calendar c1 = Calendar.getInstance();
       Calendar c2 = Calendar.getInstance();

       c1.setTime(date1);
       c2.setTime(date2);
       int diffDay = 0;

       if (c1.before(c2)) {
           diffDay = countDiffDay(c1, c2);
       } else {
           diffDay = -countDiffDay(c2, c1);
       }
       return diffDay;
   }
    
	public static int countDiffDay(Calendar c1, Calendar c2) {
		int returnInt = 0;
		while (!c1.after(c2)) {
			c1.add(Calendar.DAY_OF_MONTH, 1);
			returnInt++;
		}

		if (returnInt > 0) {
			returnInt = returnInt - 1;
		}

		return (returnInt);
	}
	
	public static  Date getPreviousDayDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		Date previousDate = cal.getTime();
		return previousDate;
	}
	
	public static String changeDateFormat(String date) {	//Changes date from yyyy-MM-dd to dd-MM-yyyy
		String d[] = date.split("-|\\.|\\/");
		date = d[2]+"-"+d[1]+"-"+d[0];
		System.out.println(date);
		return date;
	}
		
	public static Date getMonthStartDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		Date monthStartDate = cal.getTime();
		return monthStartDate;
	}
	
	public static String getFormattedDateInDDMMYYYY(Date date) {
		String formattedDate = "";
		if(date != null) {
			formattedDate = sdfDDMMYYYY.format(date);
		}
		return formattedDate;
	}
	
	public static String getFormattedDateInDDMMYYYY1(Date date) {
		String formattedDate = "";
		if(date != null) {
			formattedDate = sdfDDMMYYYY1.format(date);
		}
		return formattedDate;
	}
	
	public static String getFormattedDateInYYYYMMDD(Date date) {
		String formattedDate = "";
		if(date != null) {
			formattedDate = sdfYYYYMMDD.format(date);
		}
		return formattedDate;
	}
	
	public static String getTimeStampStr(Date date) {
		String timeStampStr = "";
		if(date != null) {
			timeStampStr = timstampFormat.format(date);
		}
		return timeStampStr;
	}
	
	public static Date parseDateInTimeStamp(String dateStr) {
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				formattedDate = timstampFormat.parse(dateStr);	
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in parseDateInTimeStamp: " + e.getMessage());
			}			
		}
		return formattedDate;
	}
	
	public static Date parseDateInDDMMYYYY(String dateStr) {
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				formattedDate = sdfDDMMYYYY.parse(dateStr);	
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in parseDateInDDMMYYYY: " + e.getMessage());
			}			
		}
		return formattedDate;
	}
	
	public static Date parseDateInDDMMYYYY1(String dateStr) {	//TODO refactoring needed for name.
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				formattedDate = sdfDDMMYYYY1.parse(dateStr);	
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in parseDateInDDMMYYYY: " + e.getMessage());
			}			
		}
		return formattedDate;
	}
	
	public static Date parseDateInYYYYMMDD(String dateStr) {
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				formattedDate = sdfYYYYMMDD1.parse(dateStr);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in parseDateInYYYYMMDD: " + e.getMessage());
			}			
		}
		return formattedDate;
	}
	
	
	public static Date parseDateInYYYYMMDDFormat(String dateStr) {
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				formattedDate = sdfYYYYMMDD.parse(dateStr);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in parseDateInYYYYMMDD: " + e.getMessage());
			}			
		}
		return formattedDate;
	}
	
	public static Date getDateObjectFromString(String dateStr){
		Date date= null;
		try {
			date= sdfDDMMYYYY.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
    
    public static String getFormattedDateInDDMMYY(Date date) {
        String formattedDate = "";
        if(date != null) {
            formattedDate = sdfDDMMYY.format(date);
        }
        return formattedDate;
    }
     
    public static String getFormattedDateInYYYYMMDD1(Date date) {
        String formattedDate = "";
        if(date != null) {
            formattedDate = sdfYYYYMMDD1.format(date);
        }
        return formattedDate;
    }

    public static String getFormattedDateInDDMMMMyyyy(Date date) {
        String formattedDate = "";
        if(date != null) {
            formattedDate = sdfddMMMMyyyy.format(date);
        }
        return formattedDate;
    }
    
    public static Date parseDateInDDMMYY(String dateStr){
    	 Date date= null;
         try {
             date= sdfDDMMYY.parse(dateStr);
         } catch (ParseException e) {
             e.printStackTrace();
         }
         return date;
    }
    
	public static Date getDateWithoutTimeAndMilliSecond(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	
	public static Date getDateAfterAddingDaysWithoutTimeAndMilliSecond(Date date,int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static String getDateTimeStr(Date date) {
		String timeStampStr = "";
		if(date != null) {
			timeStampStr = dateTimeFormat.format(date);
		}
		return timeStampStr;
	}
	
	public static Date parseDateInDateTimeStr(String dateStr) {
		Date formattedDate = null;
		if(dateStr != null && !dateStr.isEmpty()) {
			try {
				dateTimeFormat.setLenient(false);
				formattedDate = dateTimeFormat.parse(dateStr);	
			} catch (Exception e) {
				logger.debug("Error in parseDateInDateTimeStr: ", e);
			}			
		}
		return formattedDate;
	}
	
}
