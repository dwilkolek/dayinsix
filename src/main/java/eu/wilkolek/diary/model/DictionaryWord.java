package eu.wilkolek.diary.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dictionary")
public class DictionaryWord {
    
    public DictionaryWord(){}
    public DictionaryWord(User user,String value){
        this.value = value;
        this.user = user;
    }
    
	@Id
	private String id;

	private String value;
	
	@DBRef
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
	
	
	
	
}
