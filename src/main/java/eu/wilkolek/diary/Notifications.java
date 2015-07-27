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

import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MailService;
import eu.wilkolek.diary.util.DateTimeUtils;

@Component
public class Notifications {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

//    @Scheduled(fixedDelay = 5000L)
    @Scheduled(cron = "0 0 1 * * ?")
    public void sendNotification() {
        System.out.println("sentNotification "+DateTimeUtils.getCurrentUTCTime() + " | "+(new Date(System.currentTimeMillis())));
        
        if (this.userRepository != null) {
            List<User> users = this.userRepository.findAll();

            for (User u : users) {
               try {
                mailService.sendNotification(u);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
            } catch (Exception e) {
//                e.printStackTrace();
            }

            }

        }

    }

    private void sendEmail(String email, String username, long diff) {
        

    }

}
