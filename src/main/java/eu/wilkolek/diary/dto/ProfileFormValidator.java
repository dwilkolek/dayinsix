package eu.wilkolek.diary.dto;


import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.repository.UserRepository;
//import eu.wilkolek.diary.util.TimezoneUtils;

@Component
public class ProfileFormValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileFormValidator.class);
    private final UserRepository userRepository;
    
    @Autowired
    public ProfileFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ProfileForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LOGGER.debug("Validating {}", target);
        ProfileForm form = (ProfileForm) target;
        validatePasswords(errors, form);
     //   validateEmail(errors, form);      
        validateInputType(errors, form);
    //    validateUsername(errors, form);
        validateShareStyle(errors, form);
//        validateTimezone(errors, form);
        validateNotificationFrequency(errors, form);
    }

    private void validateNotificationFrequency(Errors errors, ProfileForm form) {
        for (NotificationTypesEnum type : NotificationTypesEnum.values()) {
            if (type.name().equals(form.getNotificationFrequency())){
                return;
            }
        }
        
        errors.reject("notificationFrequency.notRecognized", "Notification frequency must be choosen from those provided in select box");
    }

    private void validateInputType(Errors errors, ProfileForm form) {
		for (InputTypeEnum type : InputTypeEnum.values()){
			if (type.name().equals(form.getInputType())){
				return;
			}
		}
		errors.reject("inputType.not_provided", "Wrong input type");
	}

	private void validatePasswords(Errors errors, ProfileForm form) {
        if (!StringUtils.isEmpty(form.getPassword())){
            if (!form.getPassword().equals(form.getPasswordRepeated())){
                errors.reject("password.missmatch", "If you want to change password, you must provide it correctly.");
            }
        }
    }

    
    
    private void validateShareStyle(Errors errors, ProfileForm form) {
        for (ShareStyleEnum type : ShareStyleEnum.values()){
            if (type.name().equals(form.getShareStyle())){
                return;
            }
        }
        errors.reject("sharestyle.not_provided", "Wrong share style");
    }
    
//    private void validateTimezone(Errors errors, ProfileForm form) {
////        LinkedHashMap<String,String> map = TimezoneUtils.getTimeZones();
////        if (!map.containsKey(form.getTimezone())){
////            errors.reject("timezone.not_provided", "Wrong timezone");
////        }
//    }

    
   
    
}
