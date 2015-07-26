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
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.MailUtils;

@Component
public class Notifications {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Scheduled(cron = "0 0 2 * * ?")
    public void sendNotyfication() {
        Date now = DateTimeUtils.getCurrentUTCTime();
        if (this.userRepository != null) {
            List<User> users = this.userRepository.findAll();

            for (User u : users) {

                String freq = u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY);

                Long daysL = Long.parseLong(NotificationTypesEnum.getInDays(freq));

                boolean shouldNotify = !DateTimeUtils.isLowerThanMinTimeDiff(u.getLastLogIn(), now, daysL);
                long diff = DateTimeUtils.diffInDays(u.getLastLogIn(), now);
                if (shouldNotify) {

                    this.sendEmail(u.getEmail(), u.getUsername(), diff);
                }

            }

        }

    }

    private void sendEmail(String email, String username, long diff) {
        try {

            // sender.setHost("mail.host.com");

            MimeMessage message = this.javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(email);

            helper.setFrom(MailUtils.FROM, MailUtils.NAME);
            helper.setText("<html><body>Hello " + username + ", <br />" + "You haven't written in your diary for " + diff + ".<br />"
                    + "Quickly, log in <a href='http://dayinsix.com'>DayInSix.com</a> and make up for all these days. <br />"
                    + "Otherwise you're going to lost many beautiful memories.<br /><br />" + "DayInSix crew" + "</body></html>", true);
            helper.setSubject("Activate your account at dayinsix.com");
            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
