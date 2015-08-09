package eu.wilkolek.diary.exception;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;

public class NoSuchUserException extends Exception implements ExceptionWithUserInterface {

    /**
     * 
     */
    private static final long serialVersionUID = 297294281129923884L;

    private CurrentUser user;
    
    public NoSuchUserException(String message, CurrentUser u) {
        super(message);
        this.user = u;
    }

    @Override
    public CurrentUser getUser() {
        return user;
    }

    @Override
    public void setUser(CurrentUser u) {
       this.user = u;
    }
    
}
