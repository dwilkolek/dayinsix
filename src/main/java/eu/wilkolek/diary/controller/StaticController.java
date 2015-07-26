package eu.wilkolek.diary.controller;

import java.io.UnsupportedEncodingException;

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
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.util.MailUtils;
import eu.wilkolek.diary.util.MetadataHelper;

@Controller
public class StaticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticController.class);
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @RequestMapping("/")
    public ModelAndView home() {
    	ModelAndView model = new ModelAndView("static/home");
    	model.getModelMap().addAttribute("title", MetadataHelper.title("Welcome"));
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
    
    @RequestMapping(value="/feedback",method = RequestMethod.GET)
    public ModelAndView contact() {
        ModelAndView model = new ModelAndView("static/feedback");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Feedback"));
        LOGGER.debug("Getting feedback page");
        return model;
    }
    
    @RequestMapping(value="/feedback",method = RequestMethod.POST)
    public ModelAndView contactPOST(@RequestParam(value="mail") String mail, CurrentUser currentUser) {
        ModelAndView model = new ModelAndView("static/feedbackSent");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Feedback"));
        this.sendEmail(mail, currentUser!=null ? currentUser.getUser() : null, model);
        LOGGER.debug("Getting feedback page");
        return model;
    }
    
    private void sendEmail(String mail, User user, ModelAndView model) {
        try {

            // sender.setHost("mail.host.com");

            MimeMessage message = this.javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(MailUtils.FEEDBACK);
            String start = "";
            if (user != null){
                start += "Email:"+user.getEmail()+"<br />";
                start += "Username:"+user.getUsername()+"<br />";
                start += "ID:"+user.getId()+"<br />";
                start += "<br /><br />";
            } else {
                start += "User is not logged in";
                start += "<br /><br />";
            }
            helper.setFrom(MailUtils.FROM, MailUtils.NAME);
            helper.setText("<html><body>"+user + mail+"</body></html>", true);
            helper.setSubject("Activate your account at dayinsix.com");
            this.javaMailSender.send(message);
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
