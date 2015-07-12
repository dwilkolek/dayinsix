package eu.wilkolek.diary.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum NotificationTypesEnum {
    DAY("1 day"),WEEK("1 week"), TWO_WEEKS("2 weeks"), MONTH("1 month"), THREE_MONTHS(
            "3 months"), YEAR("1 year");

    private String description;

    private NotificationTypesEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static HashMap<String, String> asMap() {
        LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
        for (NotificationTypesEnum e : NotificationTypesEnum.values()) {
            values.put(e.name(), e.getDescription());
        }
        return values;
    }

    public static String getInDays(String n) {
        if (n == null) {
            n = WEEK.name();
        }

        if (n.equals(DAY.name())) {
            return "1";
        } else
        
        if (n.equals(WEEK.name())) {
            return "7";
        } else

        if (n.equals(TWO_WEEKS.name())) {
            return "14";
        } else

        if (n.equals(MONTH.name())) {
            return "30";
        } else

        if (n.equals(THREE_MONTHS.name())) {
            return "92";
        } else

        if (n.equals(YEAR.name())) {
            return "365";
        } else {
            return "7";
        }

    }

    public static NotificationTypesEnum getInEnum(String days) {
        if (days == null) {
            days = "7";
        }
        if ("1".equals(days)){
            return DAY;
        }
        if ("7".equals(days)) {
            return WEEK;
        } else

        if ("14".equals(days)) {
            return TWO_WEEKS;
        } else

        if ("30".equals(days)) {
            return MONTH;
        } else

        if ("92".equals(days)) {
            return THREE_MONTHS;
        } else

        if ("365".equals(days)) {
            return YEAR;
        } else {
            return WEEK;
        }
    }
}
