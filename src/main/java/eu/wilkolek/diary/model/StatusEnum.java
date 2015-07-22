package eu.wilkolek.diary.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum StatusEnum {
	POSITIVE, NEUTRAL, NEGATIVE;
	
	public static LinkedHashMap<String, String> asMap(){
	    LinkedHashMap<String, String> values = new LinkedHashMap<String,String>();
        for (StatusEnum e : StatusEnum.values()){
            values.put(e.name(), e.name());
        }
        return values;
    }
}
