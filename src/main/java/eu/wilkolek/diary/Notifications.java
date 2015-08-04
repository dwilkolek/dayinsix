package eu.wilkolek.diary;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MailService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.MailUtil;

@Component
public class Notifications {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailService mailService;

    // @Scheduled(fixedDelay = 5000L)
    @Scheduled(cron = "0 0 */1 * * ?")
    public void sendNotification() {
        System.out.println("sentNotification " + DateTimeUtils.getCurrentUTCTime() + " | " + (new Date(System.currentTimeMillis())));

        if (this.userRepository != null) {
            List<User> users = this.userRepository.findAll();

            for (User u : users) {

                try {
                    MailUtil.sendNotification(u, userRepository, javaMailSender, mailService);
                } catch (Exception e) {
                    if (errorRepository != null) {
                        eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error(e, u);
                        ex.setMessage("Notification : " + ex.getMessage());
                        errorRepository.save(ex);
                    }
                }

            }

        }

    }

}
