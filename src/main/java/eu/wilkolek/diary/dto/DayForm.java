package eu.wilkolek.diary.dto;

import java.util.ArrayList;
import java.util.HashMap;

import eu.wilkolek.diary.model.DictionaryWord;

public class DayForm {

	private ArrayList<String> words;
	private ArrayList<DictionaryWord> dictionaryWords;
	private ArrayList<String> wordsStatuses;
	
	private String sentence;
	private String sentenceStatus;


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
		if (words == null) {
			return new ArrayList<String>();
		}
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
