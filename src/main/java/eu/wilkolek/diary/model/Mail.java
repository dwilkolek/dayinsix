package eu.wilkolek.diary.model;

import java.util.Date;

import javax.mail.Address;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mails")
public class Mail {
    
    @Id
    private String id;
    
    private String message;

    private Date date;

    private String subject;

    public String getSubject() {
        return subject;
    }

    public Address[] getTo() {
        return to;
    }

    private Address[] to;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setSubject(String subject) {
        this.subject = subject;
        
    }

    public void setTo(Address[] addresses) {
        this.to = addresses;
        
    }
    
    
}
