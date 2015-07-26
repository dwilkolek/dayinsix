package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import eu.wilkolek.diary.dto.DayForm;


@Document(collection = "days")
public class Day {
	
	
	public Day(){}
	public Day(DayForm dayForm, User user) {
		
		this.user = user;
		
		this.shareStyle = dayForm.getShareStyle();
		
		if (!StringUtils.isEmpty(dayForm.getSentence())){
			this.sentence = new Sentence();
			this.sentence.setStatus((StatusEnum.valueOf(dayForm.getSentenceStatus())).toString());
			this.sentence.setValue(dayForm.getSentence());
			
		} else {
			this.words = new ArrayList<Word>();
			int i = 0;
			
			for (DictionaryWord d : dayForm.getDictionaryWords()){
				Word w = new Word();
				w.setValue(d);
				w.setWord(d.getValue());
				w.setStatus((StatusEnum.valueOf(dayForm.getWordsStatuses().get(i))).toString());
				i++;
				this.words.add(w);
			}
			
		}
		
		this.note = dayForm.getNote();
		
		this.creationDate = dayForm.getDayDate();
	}

	@Id
    private String id;
	
	@DBRef
	private User user;
	
	private Sentence sentence;
	
	private ArrayList<Word> words;
		
	private Date creationDate;
	
	private String note;
	
	private Date storeDate;
	
	private String shareStyle;
	

	public String getId() {
		return id;
	}
	public User getUser() {
		return user;
	}
	public Sentence getSentence() {
		return sentence;
	}
	public ArrayList<Word> getWords() {
		return words;
	}
	public Date getCreationDate() {
		return creationDate;
	}
    public void setId(String id) {
        this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }
    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Date getStoreDate() {
        return storeDate;
    }
    public void setStoreDate(Date storeDate) {
        this.storeDate = storeDate;
    }
    public String getShareStyle() {
        return shareStyle;
    }
    public void setShareStyle(String shareStyle) {
        this.shareStyle = shareStyle;
    }
	
	
	

}
