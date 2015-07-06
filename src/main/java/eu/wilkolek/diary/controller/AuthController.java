package eu.wilkolek.diary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.dto.UserCreateForm;
import eu.wilkolek.diary.dto.UserCreateFormValidator;
import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.TimezoneUtils;

import java.util.HashMap;
import java.util.Optional;

import javax.validation.Valid;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

	private UserCreateFormValidator userCreateFormValidator;
	
	@InitBinder("form")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(userCreateFormValidator);	
	}
    
    
    @Autowired
    public AuthController(UserRepository userRepository, UserCreateFormValidator userCreateFormValidator) {
		this.userRepository = userRepository;
		this.userCreateFormValidator = userCreateFormValidator;
	}
	
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        LOGGER.debug("Getting login page, error={}", error);
        ModelAndView model = new ModelAndView("login");
        model.getModelMap().addAttribute("errors", new HashMap<String,String>());
        return model;
    }

    
    @RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView model = new ModelAndView("auth/register");
        model.getModelMap().addAttribute("timezones", TimezoneUtils.getTimeZones());
        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
        model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
		model.getModelMap().addAttribute("errors", new HashMap<String,String>());
		return model;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView handleRegister(
			@Valid @ModelAttribute("form") UserCreateForm form,
			BindingResult bindingResult) {
		LOGGER.debug("Processing user create form={}, bindingResult={}", form,
				bindingResult);
			
		ModelAndView model = new ModelAndView("/auth/register");
				
		if (bindingResult.hasErrors()) {
			// failed validation
			
			model.getModelMap().addAttribute("form", form);
			model.getModelMap().addAttribute("timezones", TimezoneUtils.getTimeZones());
	        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
	        model.getModelMap().addAttribute("inputTypes", InputTypeEnum.asMap());
			model.getModelMap().addAttribute("errors", form.createMessages(bindingResult.getAllErrors()));
			return model;
		}
		try {
			User user = new User(form);
			user = userRepository.save(user);
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
		return new ModelAndView("auth/thankyou");
	}
    
}
