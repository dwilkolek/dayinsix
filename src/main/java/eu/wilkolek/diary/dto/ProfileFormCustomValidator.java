package eu.wilkolek.diary.dto;

import java.util.Date;

import org.apache.commons.validator.EmailValidator;
import org.springframework.validation.Errors;

import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;

public class ProfileFormCustomValidator {

    public static void validate(User user, UserRepository userRepository, ProfileForm form, Errors errors){
    
        validateDateInputType(user, userRepository, form, errors);
        validateEmail(user, userRepository, form, errors);
        validateUsername(user, userRepository, form, errors);
   }

    private static void validateUsername(User user, UserRepository userRepository, ProfileForm form, Errors errors) {
        if (!user.getUsername().equals(form.getUsername())){
            if (userRepository.findByUsername(form.getUsername()).isPresent()) {
                errors.reject("username.exists", "User with this username already exists");
            }
        }
      
    }

    private static void validateDateInputType(User user, UserRepository userRepository, ProfileForm form, Errors errors) {
        Date inputTypeDate = user.getOptionsLastUpdate()
                .get(UserOptions.INPUT_TYPE);
        String inputTypeValue = user.getOptions().get(UserOptions.INPUT_TYPE);
        Boolean equalsType = !inputTypeValue.equals(form.getInputType());
        Boolean canChange = !DateTimeUtils.canChange(inputTypeDate, new Date(), 30L);
        if (equalsType && canChange) {
            errors.reject("inputType.change_to_often",
                    "Input type can be changed only once per month");
        }
    }

    private static void validateEmail(User user, UserRepository userRepository, ProfileForm form, Errors errors) {
        if (!user.getEmail().equals(form.getEmail())){
            if (userRepository.findByEmail(form.getEmail()).isPresent()) {
                errors.reject("email.exists", "User with this email already exists");
            }
        }
        if (!EmailValidator.getInstance().isValid(form.getEmail())) {
            errors.reject("email.wrong", "This is not an email address");
        }
    }

}
