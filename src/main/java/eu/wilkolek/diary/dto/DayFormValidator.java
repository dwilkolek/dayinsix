package eu.wilkolek.diary.dto;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eu.wilkolek.diary.repository.UserRepository;

@Component
public class DayFormValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DayFormValidator.class);
    private final UserRepository userRepository;

    @Autowired
    public DayFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(DayForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
      
    }

//    private void validatePasswords(Errors errors, UserCreateForm form) {
//        if (!form.getPassword().equals(form.getPasswordRepeated())) {
//            errors.reject("password.no_match", "Passwords do not match");
//        }
//    }
//
//    private void validateEmail(Errors errors, UserCreateForm form) {
//        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
//            errors.reject("email.exists", "User with this email already exists");
//        }
//    }
}
