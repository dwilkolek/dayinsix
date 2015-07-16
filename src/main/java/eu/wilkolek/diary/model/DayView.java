package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Date;

public class DayView {
    private Sentence sentence;

    private ArrayList<Word> words;

    private Date creationDate;

    private boolean empty;

    private Boolean canAdd;

    public DayView(Sentence sentence, ArrayList<Word> words, Date creationDate) {
        super();
        this.sentence = sentence;
        this.words = words;
        this.creationDate = creationDate;
        this.empty = false;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setCanAdd(Boolean canAdd) {
        this.canAdd = canAdd;
    }
    
    public Boolean isCanAdd(){
        return this.canAdd;
    }
    
    public Boolean getCanAdd(){
        return this.canAdd;
    }
    

}
