package eu.wilkolek.diary.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.ErrorRepository;
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

    public void sendMessage(MimeMessage message, User user) {
        try {
            boolean canSend = true;
            if (user != null) {
                canSend = MailUtil.checkights(user);
            }
            if (canSend) {
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
