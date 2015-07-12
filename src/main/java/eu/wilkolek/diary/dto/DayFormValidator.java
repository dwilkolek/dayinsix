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


}
