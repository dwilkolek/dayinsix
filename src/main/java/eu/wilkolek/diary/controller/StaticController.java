package eu.wilkolek.diary.controller;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.Mail;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.MailRepository;
import eu.wilkolek.diary.service.MailService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.DayHelper;
import eu.wilkolek.diary.util.MailUtil;
import eu.wilkolek.diary.util.MetadataHelper;

@Controller
public class StaticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticController.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private DayRepository dayRepository;
    
    @Autowired
    private MailService mailService;
    
    @RequestMapping("/")
    public ModelAndView home(CurrentUser currentUser) {
        ModelAndView model = new ModelAndView("static/home");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Welcome"));
        if (currentUser == null){
            LinkedHashSet<DayView> dayViews = DayHelper.getLastPublicDays(dayRepository, ShareStyleEnum.PUBLIC, 10, ShareStyleEnum.PUBLIC.name());
            model.getModelMap().addAttribute("days", dayViews);
        } else {
            LinkedHashSet<DayView> dayViews = DayHelper.getLastPublicDays(dayRepository, ShareStyleEnum.PROTECTED, 10, ShareStyleEnum.PUBLIC.name());
            model.getModelMap().addAttribute("days", dayViews);
        }
        
        LOGGER.debug("Getting home page");
        return model;
    }

    @RequestMapping("/faq")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView("static/faq");
        model.getModelMap().addAttribute("title", MetadataHelper.title("How dayinsix works"));
        LOGGER.debug("Getting about page");
        return model;
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    public ModelAndView contact() {
        ModelAndView model = new ModelAndView("static/feedback");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Feedback"));
        LOGGER.debug("Getting feedback page");
        return model;
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public ModelAndView contactPOST(@RequestParam(value = "mail") String mail, CurrentUser currentUser) {
        ModelAndView model = new ModelAndView("static/feedbackSent");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Feedback"));
        String msg = this.createMsg(currentUser != null ? currentUser.getUser() : null, mail);
        
        
        
        this.sendEmail(msg,  model );

        LOGGER.debug("Feedback POST");
        return model;
    }

    private String createMsg(User user, String msg) {
        String start = "";
        if (user != null) {
            start += "Email:" + user.getEmail() + "<br />";
            start += "Username:" + user.getUsername() + "<br />";
            start += "ID:" + user.getId() + "<br />";
            start += "<br /><br />";
        } else {
            start += "User is not logged in";
            start += "<br /><br />";
        }
        return "<html><body>"+start +msg+"</body></html>";
    }

    private void sendEmail(String msg, ModelAndView model) {

        try {

            // sender.setHost("mail.host.com");

            MimeMessage message = MailUtil.createMessage(javaMailSender);

            MimeMessageHelper helper = MailUtil.getHelper(message, true);

            helper.setText(msg, true);
            helper.setSubject("Feedback required");
            mailService.sendMessage(message,msg, null, true);
            model.getModelMap().addAttribute("sent", true);
        } catch (MessagingException e) {
            model.getModelMap().addAttribute("sent", false);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            model.getModelMap().addAttribute("sent", false);
            e.printStackTrace();
        }

    }

}
