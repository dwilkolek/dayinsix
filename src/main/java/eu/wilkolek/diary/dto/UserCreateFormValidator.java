package eu.wilkolek.diary.dto;


import java.util.EnumSet;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.TimezoneUtils;

@Component
public class UserCreateFormValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateFormValidator.class);
    private final UserRepository userRepository;

    @Autowired
    public UserCreateFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserCreateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LOGGER.debug("Validating {}", target);
        UserCreateForm form = (UserCreateForm) target;
        validatePasswords(errors, form);
        validateEmail(errors, form);      
        validateInputType(errors, form);
        validateUsername(errors, form);
        validateShareStyle(errors, form);
        validateTimezone(errors, form);
    }

    private void validateInputType(Errors errors, UserCreateForm form) {
		for (InputTypeEnum type : InputTypeEnum.values()){
			if (type.name().equals(form.getInputType())){
				return;
			}
		}
		errors.reject("inputType.not_provided", "Wrong input type");
	}

	private void validatePasswords(Errors errors, UserCreateForm form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.reject("password.no_match", "Passwords do not match");
        }
        if (StringUtils.isEmpty(form.getPassword())){
        	errors.reject("password.is_empty", "Password is empty");
        }else{
            if (form.getPassword().length() < 6){
                errors.reject("password.too_weak", "Password must be at least 6 characters");
            }
        }
        
        
    }

    private void validateEmail(Errors errors, UserCreateForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.reject("email.exists", "User with this email already exists");
        }
        if (!EmailValidator.getInstance().isValid(form.getEmail())){
        	errors.reject("email.wrong", "This is not an email address");
        }
    }
    
    private void validateShareStyle(Errors errors, UserCreateForm form) {
        for (ShareStyleEnum type : ShareStyleEnum.values()){
            if (type.name().equals(form.getShareStyle())){
                return;
            }
        }
        errors.reject("sharestyle.not_provided", "Wrong share style");
    }
    
    private void validateTimezone(Errors errors, UserCreateForm form) {
        LinkedHashMap<String,String> map = TimezoneUtils.getTimeZones();
        if (!map.containsKey(form.getTimezone())){
            errors.reject("timezone.not_provided", "Wrong timezone");
        }
    }

    
    private void validateUsername(Errors errors, UserCreateForm form) {
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            errors.reject("username.exists", "User with this username already exists");
        }
        if (StringUtils.isEmpty(form.getUsername())){
            errors.reject("username.is_empty", "Username is empty");
        }else{
            if (form.getPassword().length() < 3){
                errors.reject("username.too_weak", "Username must be at least 3 characters");
            }
        }
    }
    
}
