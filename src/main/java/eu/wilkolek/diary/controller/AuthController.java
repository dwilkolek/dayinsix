package eu.wilkolek.diary.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import scala.util.control.Exception;
import eu.wilkolek.diary.dto.UserCreateForm;
import eu.wilkolek.diary.dto.UserCreateFormValidator;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MailService;
import eu.wilkolek.diary.service.MetaService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.Email;
import eu.wilkolek.diary.util.MailUtil;
//import eu.wilkolek.diary.util.TimezoneUtils;


@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

    private UserCreateFormValidator userCreateFormValidator;
    private JavaMailSender javaMailSender;
    private MailService mailService;

    private MetaService metaService;

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @Autowired
    public AuthController(UserRepository userRepository, UserCreateFormValidator userCreateFormValidator, JavaMailSender javaMailSender, MailService mailService, MetaService metaService) {
        this.userRepository = userRepository;
        this.userCreateFormValidator = userCreateFormValidator;
        this.javaMailSender = javaMailSender;
        this.mailService = mailService;
        this.metaService= metaService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("login");
        model.getModelMap().addAttribute("sidebarNoLogin", true);
        model.getModelMap().addAttribute("errors", new HashMap<String, String>());

        
        model = metaService.updateModel(model, "/login", new Meta(), null,"");

        return model;
    }

    @RequestMapping(value = "/loginError", method = RequestMethod.GET)
    public ModelAndView loginError(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("loginError");
        model.getModelMap().addAttribute("errors", new HashMap<String, String>());
        return model;
    }

    @RequestMapping(value = "/loginSuccess", method = RequestMethod.GET)
    public ModelAndView loginSuccess(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("loginSuccess");
        model.getModelMap().addAttribute("errors", new HashMap<String, String>());
        return model;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView model = new ModelAndView("auth/register");
        // model.getModelMap().addAttribute("timezones",
        // TimezoneUtils.getTimeZones());
        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
        model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
        model.getModelMap().addAttribute("errors", new HashMap<String, String>());

        model = metaService.updateModel(model, "/register", new Meta(), null,"");

        return model;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView handleRegister(@Valid @ModelAttribute("form") UserCreateForm form, BindingResult bindingResult) throws UnsupportedEncodingException, MessagingException {
        LOGGER.debug("Processing user create form={}, bindingResult={}", form, bindingResult);

        ModelAndView model = new ModelAndView("auth/register");

        model = metaService.updateModel(model, "/register", new Meta(), null,"");

        if (bindingResult.hasErrors()) {
            // failed validation

            model.getModelMap().addAttribute("form", form);
            // model.getModelMap().addAttribute("timezones",
            // TimezoneUtils.getTimeZones());
            model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
            model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
            model.getModelMap().addAttribute("errors", bindingResult.getAllErrors());

            return model;
        }
        try {
            form.setEmail(form.getEmail().toLowerCase());
            User user = new User(form);
            String token = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
            user.setToken(token);
            user = userRepository.save(user);
           
            this.sendActivation(user);
          

        } catch (DataIntegrityViolationException e) {
            // probably email already exists - very rare case when multiple
            // admins are adding same user
            // at the same time and form validation has passed for more than one
            // of them.
            LOGGER.warn("Exception occurred when trying to save the user, assuming duplicate email", e);

            bindingResult.addError(new ObjectError("email", "Email already exists"));
            model.getModelMap().addAttribute("form", form);
            model.getModelMap().addAttribute("errors", form.createMessages(bindingResult.getAllErrors()));
            return model;
        }
        // ok, redirect

        return new ModelAndView("redirect:/thankyou");
    }

    @RequestMapping(value = "/userDisabled", method = RequestMethod.GET)
    public String logoutDisabled(){
        return "auth/logoutDisabledUser";
    }
    
    @RequestMapping(value = "/activate/{username}/{token}", method = RequestMethod.GET)
    public ModelAndView activate(@PathVariable(value = "username") String username, @PathVariable(value = "token") String token) {
        ModelAndView model = new ModelAndView("auth/activate");
        
        model = metaService.updateModel(model, "/activate", new Meta(), null,"");

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (token.equals(user.get().getToken())) {
                user.get().setToken("");
                user.get().setEnabled(true);
                userRepository.save(user.get());
                model.getModelMap().put("success", true);
                model.getModelMap().put("error", false);
                return model;
            }
        }
        model.getModelMap().put("success", false);
        model.getModelMap().put("error", true);
        return model;
    }

    @RequestMapping(value = "/thankyou", method = RequestMethod.GET)
    public ModelAndView thankyou() {
        ModelAndView model = new ModelAndView("auth/thankyou");
        model = metaService.updateModel(model, "/thankyou", new Meta(), null,"");
        return model;
    }

    public void sendActivation(User user) throws UnsupportedEncodingException, MessagingException {

            String link = "http://dayinsix.com/activate/" + user.getUsername() + "/" + user.getToken();
            // sender.setHost("mail.host.com");

            MimeMessage message = MailUtil.createMessage(javaMailSender);
            MimeMessageHelper helper = MailUtil.getHelper(message, false);
            helper.setTo(user.getEmail());
            String text = "<html><body>Welcome to dayinsix, <br />" + "Your diary is almost ready for you to write in it. <br />"
                    + "Finish the registration by opening the link below and enjoy saving your days.<br />" + "<a href='" + link + "'>" + link
                    + "</a><br /><br />Cheers, dayinsix crew</body></html>";
            helper.setText(text, true);
            helper.setSubject("Activate your account at dayinsix.com");
            mailService.sendMessage(message,text, null, false);
        
    }

//    public SimpleMailMessage sendThanks(String email) {
//        // TODO
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(email);
//        // mailMessage.setReplyTo("someone@localhost");
//        mailMessage.setFrom(MailService.FROM);
//        mailMessage.setSubject("Thanks for registering at dayinsix.com");
//        mailMessage.setText("Thanks for registering :*");
//        javaMailSender.send(mailMessage);
//        return mailMessage;
//
//    }

    public void sendNewPassowrd(User user, String password) {
        try {
            MimeMessage message = MailUtil.createMessage(javaMailSender);
            MimeMessageHelper helper = MailUtil.getHelper(message,false);
            helper.setTo(user.getEmail());
            String text = "<html><body>Hi " + user.getUsername() + ",\n" + "Seems like you've got forgotten your password. Here's the new one: " + password + " .<br />"
                    + "Log in with it and remember that you can change it in settings if you'd like to.<br /><br /><br />"
                    + "Cheers, dayinsix crew<br /></body></html>";
            helper.setText(text, true);
            helper.setSubject("Password reminder / DayInSix.com");

            mailService.sendMessage(message, text,null, false);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/remind", method = RequestMethod.GET)
    public ModelAndView remind() {
        ModelAndView model = new ModelAndView("auth/remind");
        model = metaService.updateModel(model, "/remind", new Meta(), null,"");
        return model;
    }

    @RequestMapping(value = "/remind", method = RequestMethod.POST)
    public ModelAndView remindPost(@RequestParam(value = "email") String email) throws NoSuchUserException, NoSuchAlgorithmException {
        ModelAndView model = new ModelAndView("auth/remindSuccess");

        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new NoSuchUserException("Reset failed for " + email, null);
        }

        // String token = DateTimeUtils.getCurrentUTCTimeAsString() + email +
        // "somerandomeCodeToSecureDigest";

        String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.get().setPasswordHash(encoder.encode(token));

        User uSaved = userRepository.save(user.get());

        model = metaService.updateModel(model, "/remindSuccess", new Meta(), null,"");
        this.sendNewPassowrd(uSaved, token);

        return model;
    }

}
