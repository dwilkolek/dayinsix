package eu.wilkolek.diary.model;

import java.util.HashMap;

public enum InputTypeEnum {
	SENTENCE("Use full 6-word sentence"), WORDS("Use 6 words as come separated");

    
    private String description;
    
    private InputTypeEnum(String description){
        this.description = description;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public static HashMap<String, String> asMap(){
        HashMap<String, String> values = new HashMap<String,String>();
        for (InputTypeEnum e : InputTypeEnum.values()){
            values.put(e.name(), e.getDescription());
        }
        return values;
    }
}
