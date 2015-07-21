package eu.wilkolek.diary.exception;

public class NoSuchUserException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 297294281129923884L;

    public NoSuchUserException(String message) {
        super(message);
    }
    
}
