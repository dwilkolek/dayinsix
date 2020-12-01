package eu.wilkolek.diary;

import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MailService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Component
public class Notifications {

    private static final Logger logger = Logger.getLogger(Notifications.class.getName());

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
        logger.info("sentNotification " + DateTimeUtils.getCurrentUTCTime() + " | " + (new Date(System.currentTimeMillis())));

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
