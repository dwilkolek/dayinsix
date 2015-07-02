package eu.wilkolek.diary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "words")
public class Word {

    @Id
	private String id;    
    
    @DBRef
    private DictionaryWord value;
    
    private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DictionaryWord getValue() {
		return value;
	}

	public void setValue(DictionaryWord value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    
    
}
