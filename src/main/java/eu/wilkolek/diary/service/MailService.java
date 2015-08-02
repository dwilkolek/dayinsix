package eu.wilkolek.diary.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.wilkolek.diary.model.Mail;
import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.MailRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.MailUtil;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private MailRepository mailRepository;

    public void sendMessage(MimeMessage message, String text, User user, Boolean attachIdToSubject) {
        try {
            boolean canSend = true;
            if (user != null) {
                canSend = MailUtil.checkights(user);
            }
            if (canSend) {
                Mail m = new Mail();
                m.setDate(DateTimeUtils.getUTCDAte());
                m.setMessage(text);
                m.setSubject(message.getSubject());
                m.setTo(message.getAllRecipients());
                m = mailRepository.save(m);
                
                if (attachIdToSubject) {
                    m.setSubject(m.getSubject() + " ID:" + m.getId());
                    m = mailRepository.save(m);
                    message.setSubject(message.getSubject() + " ID:" + m.getId());
                }
                this.javaMailSender.send(message);
            }
        } catch (Exception e) {
            if (errorRepository != null) {
                eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error();
                Gson gson = new Gson();

                ex.setStacktrace(gson.toJson(e.getStackTrace()));
                ex.setMessage("SendMessage : " + e.getMessage());
                ex.setUser(user);
                errorRepository.save(ex);
            }
        }

    }

    
}
