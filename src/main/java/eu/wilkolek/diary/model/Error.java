package eu.wilkolek.diary.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.gson.Gson;

import eu.wilkolek.diary.util.DateTimeUtils;

@Document(collection = "errors")
public class Error {
    
    private String id;
    
    private String stacktrace;
    
    private Date date;
    
    private String exceptionName;
    
    @DBRef
    private User user;
    
    private String message;
    
    
    @Deprecated
    public Error() {
        this.date = DateTimeUtils.getUTCDate();
    }
    
    public Error(Exception ex, CurrentUser cu){
            Gson gson = new Gson();
            
            this.setStacktrace(gson.toJson(ex.getStackTrace()));
            this.setMessage(ex.getMessage());
            this.setUser(cu != null ? cu.getUser() : null);
            this.setExceptionName(ex.getClass().getName());
            this.date = DateTimeUtils.getUTCDate();
            
    }
    public Error(Exception ex, User cu){
        Gson gson = new Gson();
        
        this.setStacktrace(gson.toJson(ex.getStackTrace()));
        this.setMessage(ex.getMessage());
        this.setUser(cu);
        this.setExceptionName(ex.getClass().getName());
        this.date = DateTimeUtils.getUTCDate();
        
}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getException() {
        return stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public void setException(String exception) {
        this.stacktrace = exception;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }
    

    

}
