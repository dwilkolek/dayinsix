package eu.wilkolek.diary.dto;

import java.util.ArrayList;

import eu.wilkolek.diary.model.DictionaryWord;

public class DayForm {

	private ArrayList<String> words;
	private ArrayList<DictionaryWord> dictionaryWords;
	private ArrayList<String> wordsStatuses;
	
	private String sentence;
	private String sentenceStatus;


	public DayForm() {
	    words = new ArrayList<String>(6);
	    for(int i=0;i<6;i++){
	        words.add(new String());
	    }
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
	
	
	
	
}
