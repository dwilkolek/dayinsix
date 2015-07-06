package eu.wilkolek.diary.model;

import java.util.HashMap;

public enum ShareStyleEnum {
    PRIVATE("Only for you"), PROTECTED("Only logged in users can view it"), PUBLIC("Anyone can see it");
    
    private String description;
    
    private ShareStyleEnum(String description){
        this.description = description;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public static HashMap<String, String> asMap(){
        HashMap<String, String> values = new HashMap<String,String>();
        for (ShareStyleEnum e : ShareStyleEnum.values()){
            values.put(e.name(), e.getDescription());
        }
        return values;
    }
    
}
