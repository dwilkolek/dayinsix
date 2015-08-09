package eu.wilkolek.diary.exception;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;

public class UserIsDisabledException extends Exception implements ExceptionWithUserInterface{
    public UserIsDisabledException(String message, CurrentUser u) {
        super(message);
        this.user = u;
    }
    
    @Override
    public CurrentUser getUser() {
        return user;
    }
    private CurrentUser user;
    @Override
    public void setUser(CurrentUser u) {
       this.user = u;
    }
}
