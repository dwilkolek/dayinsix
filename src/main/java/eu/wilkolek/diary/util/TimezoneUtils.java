//package eu.wilkolek.diary.util;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.TimeZone;
//
//public class TimezoneUtils {
//    private static final String TIMEZONE_ID_PREFIXES =
//
//    "^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*";
//
//    private static List<TimeZone> timeZones;
//    private static LinkedHashMap<String,String> timeZonesMap;
//
//    public static LinkedHashMap<String,String> getTimeZones() {
//
//        if (timeZones == null) {
//
//            timeZones = new ArrayList<TimeZone>();
//
//            final String[] timeZoneIds = TimeZone.getAvailableIDs();
//
//            for (final String id : timeZoneIds) {
//
//                if (id.matches(TIMEZONE_ID_PREFIXES)) {
//
//                    timeZones.add(TimeZone.getTimeZone(id));
//
//                }
//
//            }
//
//            Collections.sort(timeZones, new Comparator<TimeZone>() {
//
//                public int compare(final TimeZone t1, final TimeZone t2) {
//
//                    return t1.getID().compareTo(t2.getID());
//
//                }
//
//            });
//
//            
//            
//            
//            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//            for (TimeZone t : timeZones){
//                map.put(t.getID(), getName(t));
//            }
//            timeZonesMap = map;
//            
//        }
//        
//        return timeZonesMap;
//
//    }
//
//    public static String getName(TimeZone timeZone) {
//
//        return timeZone.getID().replaceAll("_", " ") + " - "
//                + timeZone.getDisplayName();
//
//    }
//}
