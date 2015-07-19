package eu.wilkolek.diary.dto;

import java.util.ArrayList;
import java.util.Date;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;

public class DayForm {

	private ArrayList<String> words;
	private ArrayList<DictionaryWord> dictionaryWords;
	private ArrayList<String> wordsStatuses;
	
	private String sentence;
	private String sentenceStatus;

	private Date dayDate;

	public DayForm() {
	   	    
    }
	
	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}



	public String getSentenceStatus() {
		return sentenceStatus;
	}

	public void setSentenceStatus(String sentenceStatus) {
		this.sentenceStatus = sentenceStatus;
	}

	public ArrayList<String> getWordsStatuses() {
		return wordsStatuses;
	}

	public void setWordsStatuses(ArrayList<String> wordsStatuses) {
		this.wordsStatuses = wordsStatuses;
	}

	public ArrayList<String> getWords() {
		return words;
	}

	public void setWords(ArrayList<String> words) {
		this.words = words;
	}

	public ArrayList<DictionaryWord> getDictionaryWords() {
		return dictionaryWords;
	}

	public void setDictionaryWords(ArrayList<DictionaryWord> dictionaryWords) {
		this.dictionaryWords = dictionaryWords;
	}

    public void setDayDate(Date date) {
        this.dayDate = date;    
    }

    public Date getDayDate() {
        return this.dayDate;
    }

    public void assignDay(Day day) {
        this.dayDate = day.getCreationDate();
        if (day.getSentence() != null){
            this.sentence = day.getSentence().getValue();
            this.words = null;
        }
        if (day.getWords() != null){
            this.words = new ArrayList<String>();
            this.wordsStatuses = new ArrayList<String>();
            for (int i = 0; i<day.getWords().size(); i++){
                this.words.add(day.getWords().get(i).getValue().getValue());
                this.wordsStatuses.add(day.getWords().get(i).getStatus());
            }
        }
       
    }
	
	
	
	
}
