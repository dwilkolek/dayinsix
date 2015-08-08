package eu.wilkolek.diary.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meta")
public class Meta {
    @Id
    private String id;
    
    private String url;
    
    private String title;
    
    private String description;
    
    private String keywords;

    public Meta(){};
    
    public Meta(String url, String title, String description, String keywords) {
        super();
        this.url = url;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    @Override
    public Meta clone(){
        if (StringUtils.isEmpty(this.url)){
            return null;
        }
        Meta m = new Meta();
        m.setDescription(this.description);
        m.setTitle(this.title);
        m.setKeywords(this.keywords);
        m.setUrl(this.url);
        return m;
    }
    
    
}
