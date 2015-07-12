package eu.wilkolek.diary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date getCurrentUTCTime() {
        return StringDateToDate(getCurrentUTCTimeAsString());
    }

    public static String getCurrentUTCTimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static String format(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return dateFormat.format(date);
    }
    
    public static Date StringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }
    
    public static Boolean canChange(Date lastUpdate, Date now, Long minTimeDiff){
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        
        String lastUpdateInUTCString = sdf.format(lastUpdate);
        String nowInUTCString = sdf.format(now);
        
        Date lastUpdateInUTC = StringDateToDate(lastUpdateInUTCString);
        Date nowInUTC = StringDateToDate(nowInUTCString);
        
       long diff = nowInUTC.getTime() - lastUpdateInUTC.getTime();
       
       TimeUnit tu = TimeUnit.DAYS;
       long diffinDays = tu.convert(diff, TimeUnit.MILLISECONDS);
       Boolean resultValue =   minTimeDiff <  diffinDays;
       return resultValue;
    }

}
