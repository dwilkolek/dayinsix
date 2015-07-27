package eu.wilkolek.diary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    static final String DATEFORMAT = "yyyy-MM-dd";

    public static Date getCurrentUTCTime() {
        try {
            return StringDateToDate(getCurrentUTCTimeAsString());
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static String getCurrentUTCTimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static String checkAndFormat(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return dateFormat.format(date);
    }

    public static String format(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return dateFormat.format(date);
    }
    
    public static Date StringDateToDate(String StrDate) throws ParseException {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public static Boolean isLowerThanMinTimeDiff(Date lastUpdate, Date now, Long minTimeDiff) {
        long diffinDays = diffInDays(lastUpdate, now);
        Boolean resultValue = minTimeDiff < diffinDays;
        return resultValue;

    }

    public static Long diffInDays(Date date1, Date date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);

            String lastUpdateInUTCString = sdf.format(date1);
            String nowInUTCString = sdf.format(date2);

            Date lastUpdateInUTC;

            lastUpdateInUTC = StringDateToDate(lastUpdateInUTCString);

            Date nowInUTC = StringDateToDate(nowInUTCString);
            
            long nowInUTCMilis = nowInUTC.getTime();
            long lastUpdateInUTCMilis = lastUpdateInUTC.getTime();
            
            long diff =  nowInUTCMilis - lastUpdateInUTCMilis;

            TimeUnit tu = TimeUnit.DAYS;
            long diffinDays = tu.convert(diff, TimeUnit.MILLISECONDS);
            return diffinDays;
        } catch (ParseException e) {
            return Long.MIN_VALUE;
        }

    }

    public static boolean isDiffIsBiggerThanMin(Date lastLogIn, Date now, Long daysL) {
        long diffinDays = diffInDays(lastLogIn, now);
        Boolean resultValue = diffinDays > daysL;
        return resultValue;
    }
    
}
