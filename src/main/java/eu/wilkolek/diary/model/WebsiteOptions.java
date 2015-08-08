package eu.wilkolek.diary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config")
public class WebsiteOptions {
    @Id
    private String id;
    
    private String opt;
    
    private String val;

    
    public WebsiteOptions() {}
    
    public WebsiteOptions(String opt, String val) {
        super();
        this.opt = opt;
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
    
    
    
    
    
    
}
