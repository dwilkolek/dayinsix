package eu.wilkolek.diary.util;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.springframework.beans.factory.annotation.Autowired;

public class Email {

 // Recipient's email ID needs to be mentioned.
    String to = "abcd@gmail.com";

    // Sender's email ID needs to be mentioned
    String from = "support@dayinsix.com";

    // Assuming you are sending email from localhost
    String host = "localhost";

    // Get system properties
    Properties properties = System.getProperties();
    String msg = "";
    // Setup mail server
//    properties.setProperty("mail.smtp.host", host);

    // Get the default Session object.
    Session session;

    private String subject;
    
    @Autowired
    public Email(String destination,String subject, String message, Session session) {
        this.to = destination;
        this.msg = message;
        this.session = session;
        this.subject= subject;
        
    }
    
    public void send(){
        try{
           // Create a default MimeMessage object.
           MimeMessage message = new MimeMessage(session);
    
           // Set From: header field of the header.
           message.setFrom(new InternetAddress(from));
    
           // Set To: header field of the header.
           message.addRecipient(Message.RecipientType.TO,
                                    new InternetAddress(to));
    
           // Set Subject: header field
           message.setSubject(this.subject);
    
           // Now set the actual message
           message.setText(this.msg);
    
           // Send message
           Transport.send(message);
           System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
           mex.printStackTrace();
        }
    }
    
}
