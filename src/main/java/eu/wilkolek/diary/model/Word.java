package eu.wilkolek.diary.model;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class Word {
    
    @DBRef
    private DictionaryWord value;
    
    private String word;
    
    private String status;

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    
    
    
}
