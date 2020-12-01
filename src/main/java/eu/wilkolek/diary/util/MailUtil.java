package eu.wilkolek.diary.util;

import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MailUtil {

    private static final Logger logger = Logger.getLogger(MailUtil.class.getName());

    public final static String NAME = "DayInSix crew";
    public final static String FROM = "dayinsix@dayinsix.com";
    public final static String FEEDBACK = "dayinsixdiary@gmail.com";
    

    public static MimeMessageHelper getHelper(MimeMessage message, boolean feedback) throws MessagingException, UnsupportedEncodingException {

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(FROM, NAME);
        if (feedback) {
            helper.setTo(FEEDBACK);
        }
        return helper;
    }

    public static MimeMessage createMessage(JavaMailSender javaMailSender){
        return javaMailSender.createMimeMessage();
    }
    
    public static void sendNotification(User u, UserRepository userRepository, JavaMailSender javaMailSender, MailService mailService) throws MessagingException, UnsupportedEncodingException {
        if (!checkights(u)) {
            return;
        }

        long diff = DateTimeUtils.diffInDays(u.getLastLogIn(), DateTimeUtils.getUTCDate());

        MimeMessage message = createMessage(javaMailSender);
        MimeMessageHelper helper = getHelper(message, false);
//        String value = new String(""+TimeUnit.MILLISECONDS.toDays(diff));
        helper.setTo(u.getEmail());
        
        String dayText = "1 day";
        if (diff > 1L){
            dayText = diff + " days";
        }
        
        String text = "<html><body>Hello " + u.getUsername() + ", <br />" + "You haven't written in your diary for " + dayText + " .<br />"
                + "Quickly, log in <a href='http://dayinsix.com'>DayInSix.com</a> and make up for all these days. <br />"
                + "Otherwise you're going to lost many beautiful memories.<br /><br />" + "DayInSix crew" + "</body></html>";
        
        helper.setText(text, true);
        helper.setSubject("Absence notification / DayInSix.com");
        logger.info("Message sent: " + u.getUsername());
        u.setLastNotification(DateTimeUtils.getUTCDate());
        userRepository.save(u);
        mailService.sendMessage(helper.getMimeMessage(),text, null,false);
    }
    public static boolean checkights(User u) {
        Date now = DateTimeUtils.getUTCDate();
        String freq = u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY);

        Long daysL = Long.parseLong(NotificationTypesEnum.getInDays(freq));

        Date canNotifyAfter = new Date(now.getTime() - TimeUnit.DAYS.toMillis(daysL));

        boolean shouldNotifyLastLogin = canNotifyAfter.after(u.getLastLogIn());
        boolean shouldNotifyLastNotify = canNotifyAfter.after(u.getLastNotification());

        return (shouldNotifyLastNotify && shouldNotifyLastLogin && u.isEnabled() && !NotificationTypesEnum.NONE.name().equals(
                u.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY)));

    }
}
