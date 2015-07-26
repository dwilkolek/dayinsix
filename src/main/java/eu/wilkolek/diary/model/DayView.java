package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Date;

public class DayView {
    private Sentence sentence;

    private ArrayList<Word> words;

    private Date creationDate;

    private boolean empty;

    private Boolean canAdd;

    private Boolean canEdit;

    private String note;

    private String shareStyle;
    
    private String policy; //shareStyle in nice words...

    private boolean canSee;

    private String username;

    public DayView(Sentence sentence, ArrayList<Word> words, Date creationDate, String note, String dayShareStyle, User whosDay, User whoWatches,
            boolean checkUserProfileVisibility) {
        super();
        this.sentence = sentence;
        this.words = words;
        this.creationDate = creationDate;
        this.empty = false;
        this.setUsername(whosDay.getUsername());
        this.setShareStyle(dayShareStyle != null ? dayShareStyle : whosDay.getOptions().get(UserOptions.PROFILE_VISIBILITY));// whosDay.getOptions().get(UserOptions.SHARE_STYLE));
        this.setNote(note);
        this.setPolicy(this.getShareStyle());

        // if (this.shareStyle == ShareStyleEnum.PUBLIC.name() || (whoWatches !=
        // null && this.shareStyle == ShareStyleEnum.PROTECTED.name())) {
        // this.canSee = true;
        // }
        this.canSee = true;
        if (this.shareStyle.equals(ShareStyleEnum.PRIVATE.name())) {
            this.canSee = false;
        }
        if ((this.shareStyle.equals(ShareStyleEnum.PROTECTED.name()) || this.shareStyle.equals(ShareStyleEnum.FOR_SELECTED.name())) && whoWatches == null) {
            this.canSee = false;
        }
        if (checkUserProfileVisibility) {
            if (this.shareStyle.equals(ShareStyleEnum.FOR_SELECTED.name())) {

                boolean canShare = false;
                if (whoWatches != null) {
                    if (this.shareStyle != null && whosDay.getSharingWith() != null) {
                        for (String id : whosDay.getSharingWith()) {
                            if (id.equals(whoWatches.getId())) {
                                canShare = true;
                            }
                        }
                    }
                    if (whosDay.getId().equals(whoWatches.getId())) {
                        canShare = true;
                    }
                    if (!canShare) {
                        this.canSee = false;
                    }
                } else {
                    this.canSee = false;
                }
            }
        }

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

    public Boolean isCanAdd() {
        return this.canAdd;
    }

    public Boolean getCanAdd() {
        return this.canAdd;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShareStyle() {
        return shareStyle;
    }

    public void setShareStyle(String shareStyle) {
        this.shareStyle = shareStyle;
    }

    public boolean isCanSee() {
        return canSee;
    }

    public void setCanSee(boolean canSee) {
        this.canSee = canSee;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = ShareStyleEnum.asMap().get(policy).split(" - ")[0];
    }

}
