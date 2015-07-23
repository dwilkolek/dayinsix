package eu.wilkolek.diary.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

import javax.mail.Session;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import scala.util.control.Exception;
import eu.wilkolek.diary.dto.UserCreateForm;
import eu.wilkolek.diary.dto.UserCreateFormValidator;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.Email;
import eu.wilkolek.diary.util.MailUtils;
//import eu.wilkolek.diary.util.TimezoneUtils;
import eu.wilkolek.diary.util.MetadataHelper;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

	private UserCreateFormValidator userCreateFormValidator;
	private JavaMailSender javaMailSender;
	
	@InitBinder("form")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(userCreateFormValidator);	
	}
   
    
    @Autowired
    public AuthController(UserRepository userRepository, UserCreateFormValidator userCreateFormValidator,JavaMailSender javaMailSender) {
		this.userRepository = userRepository;
		this.userCreateFormValidator = userCreateFormValidator;
		this.javaMailSender = javaMailSender;
	}
	
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("login");
        model.getModelMap().addAttribute("sidebarNoLogin", true);
        model.getModelMap().addAttribute("errors", new HashMap<String,String>());
        
        model.getModelMap().addAttribute("title", MetadataHelper.title("Login"));
        
        
        return model;
    }

    
    @RequestMapping(value = "/loginError", method = RequestMethod.GET)
    public ModelAndView loginError(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("loginError");
        model.getModelMap().addAttribute("errors", new HashMap<String,String>());
        return model;
    }
    @RequestMapping(value = "/loginSuccess", method = RequestMethod.GET)
    public ModelAndView loginSuccess(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("loginSuccess");
        model.getModelMap().addAttribute("errors", new HashMap<String,String>());
        return model;
    }
    @RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView model = new ModelAndView("auth/register");
//        model.getModelMap().addAttribute("timezones", TimezoneUtils.getTimeZones());
        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
        model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
		model.getModelMap().addAttribute("errors", new HashMap<String,String>());
		
		model.getModelMap().addAttribute("title", MetadataHelper.title("Register"));
		
		return model;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView handleRegister(
			@Valid @ModelAttribute("form") UserCreateForm form,
			BindingResult bindingResult) {
		LOGGER.debug("Processing user create form={}, bindingResult={}", form,
				bindingResult);
			
		ModelAndView model = new ModelAndView("/auth/register");
				
		model.getModelMap().addAttribute("title", MetadataHelper.title("Register"));
		
		if (bindingResult.hasErrors()) {
			// failed validation
			
			model.getModelMap().addAttribute("form", form);
//			model.getModelMap().addAttribute("timezones", TimezoneUtils.getTimeZones());
	        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
	        model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
			model.getModelMap().addAttribute("errors", form.createMessages(bindingResult.getAllErrors()));
			
			
			
			return model;
		}
		try {
			User user = new User(form);
			user = userRepository.save(user);
			SimpleMailMessage msg = this.sendThanks(user.getEmail());
			if (msg != null) { System.out.println("sent!"); }else { System.out.println("not send");};
		} catch (DataIntegrityViolationException e) {
			// probably email already exists - very rare case when multiple
			// admins are adding same user
			// at the same time and form validation has passed for more than one
			// of them.
			LOGGER.warn(
					"Exception occurred when trying to save the user, assuming duplicate email",
					e);
			
			bindingResult.addError(new ObjectError("email", "Email already exists"));
			model.getModelMap().addAttribute("form", form);
			model.getModelMap().addAttribute("errors", form.createMessages(bindingResult.getAllErrors()));
			return model;
		}
		// ok, redirect
		
		
		
		return new ModelAndView("redirect:/thankyou");
	}
	
	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public ModelAndView thankyou() {
	    ModelAndView model = new ModelAndView("auth/thankyou");;
	    model.getModelMap().addAttribute("title", MetadataHelper.title("Thank you"));
		return model;
	}
	

	public SimpleMailMessage sendThanks(String email){
	    SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
//        mailMessage.setReplyTo("someone@localhost");
        mailMessage.setFrom(MailUtils.FROM);
        mailMessage.setSubject("Thanks for registering at dayinsix.com");
        mailMessage.setText("Thanks for registering :*");
        javaMailSender.send(mailMessage);
        return mailMessage;
	}
	
	public SimpleMailMessage sendNewPassowrd(String email, String password){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
//        mailMessage.setReplyTo("someone@localhost");
        mailMessage.setFrom(MailUtils.FROM);
        mailMessage.setText("Your password has been reseted. Your new password is : "+password+"." + "Now you can log in and change your password.");
        mailMessage.setSubject("You've got new password at dayinsix.com");
        javaMailSender.send(mailMessage);
        return mailMessage;
    }
	
	@RequestMapping(value = "/remind", method = RequestMethod.GET)
    public ModelAndView remind() {
        ModelAndView model = new ModelAndView("auth/remind");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Reset your password"));
        return model;
    }
	
	@RequestMapping(value = "/remind", method = RequestMethod.POST)
    public ModelAndView remindPost(@RequestParam(value="email") String email ) throws NoSuchUserException, NoSuchAlgorithmException {
	    ModelAndView model = new ModelAndView("auth/remindSuccess");  

	    Optional<User> user = userRepository.findByEmail(email);
	    if (!user.isPresent()){
	        throw new NoSuchUserException("Reset failed for "+email);
	    }
	    
	    MessageDigest dig = MessageDigest.getInstance("MD5");
	    
	   //String token = DateTimeUtils.getCurrentUTCTimeAsString() + email + "somerandomeCodeToSecureDigest";
	   
	    String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
	   
	    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    
	    user.get().setPasswordHash(encoder.encode(token));
	    
	    userRepository.save(user.get());
	    
        model.getModelMap().addAttribute("title", MetadataHelper.title("Reset successful"));
        this.sendNewPassowrd(user.get().getEmail(), token);
        
        return model;
    }
    
}
