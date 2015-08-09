package eu.wilkolek.diary.exception;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;

public class OutOfDateException extends Exception implements ExceptionWithUserInterface{

    /**
     * 
     */
    private static final long serialVersionUID = 3436169790989562869L;

    public OutOfDateException(String message,CurrentUser u) {
        super(message);
        this.user = u;
    }
    private CurrentUser user;
    @Override
    public CurrentUser getUser() {
        return user;
    }

    @Override
    public void setUser(CurrentUser u) {
       this.user = u;
    }
}
