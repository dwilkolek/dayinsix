package eu.wilkolek.diary.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum ShareStyleEnum {
    PRIVATE("Only for you"), FOR_SELECTED("You share your days only with selected users"), PROTECTED("Only logged in users can view it"), PUBLIC("Anyone can see it");
    
    private String description;
    
    private ShareStyleEnum(String description){
        this.description = description;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public static LinkedHashMap<String, String> asMap(){
        LinkedHashMap<String, String> values = new LinkedHashMap<String,String>();
        for (ShareStyleEnum e : ShareStyleEnum.values()){
            values.put(e.name(), e.getDescription());
        }
        return values;
    }
    
}
