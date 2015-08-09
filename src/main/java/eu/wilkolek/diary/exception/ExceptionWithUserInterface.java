package eu.wilkolek.diary.exception;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;

public interface ExceptionWithUserInterface {
    public CurrentUser getUser();
    public void setUser(CurrentUser u);
}
