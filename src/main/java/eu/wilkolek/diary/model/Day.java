package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.repository.WordRepository;

@Document(collection = "days")
public class Day {
	
	
	public Day(){}
	public Day(DayForm dayForm, User user) {
		
		this.user = user;
		
		
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
				w.setStatus((StatusEnum.valueOf(dayForm.getWordsStatuses().get(i))).toString());
				i++;
				this.words.add(w);
			}
			
		}
		this.creationDate = new Date();
	}

	@Id
    private String id;
	
	@DBRef
	private User user;
	
	private Sentence sentence;
	
	private ArrayList<Word> words;
		
	private Date creationDate;

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
	
	
	

}
