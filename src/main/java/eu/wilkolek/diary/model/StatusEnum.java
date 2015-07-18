package eu.wilkolek.diary.model;

import java.util.HashMap;

public enum StatusEnum {
	POSITIVE, NEUTRAL, NEGATIVE;
	
	public static HashMap<String, String> asMap(){
        HashMap<String, String> values = new HashMap<String,String>();
        for (StatusEnum e : StatusEnum.values()){
            values.put(e.name(), e.name());
        }
        return values;
    }
}
