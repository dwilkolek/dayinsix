package eu.wilkolek.diary.exception;

public class UserIsDisabledException extends Exception{
    public UserIsDisabledException(String message) {
        super(message);
    }
}
