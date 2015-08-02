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

@Service
public class MailService {
    public final String NAME = "DayInSix crew";
    public final String FROM = "dayinsix@dayinsix.com";
    public final String FEEDBACK = "dayinsixdiary@gmail.com";

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
                canSend = this.checkights(user);
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

    private boolean checkights(User u) {
        Date now = DateTimeUtils.getUTCDAte();
        String freq = u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY);

        Long daysL = Long.parseLong(NotificationTypesEnum.getInDays(freq));

        Date canNotifyAfter = new Date(now.getTime() - TimeUnit.DAYS.toMillis(daysL));

        boolean shouldNotifyLastLogin = canNotifyAfter.after(u.getLastLogIn());
        boolean shouldNotifyLastNotify = canNotifyAfter.after(u.getLastNotification());

        return (shouldNotifyLastNotify && shouldNotifyLastLogin && u.isEnabled() && !NotificationTypesEnum.NONE.name().equals(
                u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY)));

    }

    public MimeMessage createMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    public MimeMessageHelper getHelper(MimeMessage message, boolean feedback) throws MessagingException, UnsupportedEncodingException {

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(this.FROM, this.NAME);
        if (feedback) {
            helper.setTo(this.FEEDBACK);
        }
        return helper;
    }

    public void sendNotification(User u) throws MessagingException, UnsupportedEncodingException {
        if (!this.checkights(u)) {
            return;
        }

        long diff = DateTimeUtils.diffInDays(u.getLastLogIn(), DateTimeUtils.getCurrentUTCTime());

        MimeMessage message = this.createMimeMessage();
        MimeMessageHelper helper = this.getHelper(message, false);
        String value = NotificationTypesEnum.valueOf(u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY)).getDescription();
        helper.setText("<html><body>Hello " + u.getUsername() + ", <br />" + "You haven't written in your diary for " + value + " .<br />"
                + "Quickly, log in <a href='http://dayinsix.com'>DayInSix.com</a> and make up for all these days. <br />"
                + "Otherwise you're going to lost many beautiful memories.<br /><br />" + "DayInSix crew" + "</body></html>", true);
        helper.setSubject("Absence notification / DayInSix.com");

        System.out.println("Message sent: " + u.getUsername());
        u.setLastNotification(DateTimeUtils.getCurrentUTCTime());
        userRepository.save(u);
        this.sendMessage(message, null);

    }

}
