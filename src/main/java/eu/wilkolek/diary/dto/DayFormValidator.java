package eu.wilkolek.diary.dto;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eu.wilkolek.diary.model.StatusEnum;
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
        DayForm dayForm = (DayForm) target;

        this.validateContainingValue(dayForm, errors);
        this.validateWords(dayForm, errors);
        this.validateSentence(dayForm, errors);
    }

    private void validateContainingValue(DayForm dayForm, Errors errors) {
        if (dayForm.getSentence() == null && dayForm.getWords() == null) {
            errors.reject("dayForm.empy_wrds_and_sentence","Looks like form is empty");
        }
    }

    private void validateWords(DayForm dayForm, Errors errors) {
        boolean emptyWords = false;
        boolean emptyStatus = false;
        if (dayForm.getWords() != null) {
            for (String word : dayForm.getWords()) {
                word = word.trim();
                if (StringUtils.isEmpty(word) || word.contains(" ")) {
                    emptyWords = true;
                }
            }
            if (emptyWords) {
                errors.reject("dayform.word_error","Words can't contain ' ', nor be empty.");
            }
            for (String wordStatus : dayForm.getWordsStatuses()) {

                if (StringUtils.isEmpty(wordStatus) || StringUtils.isEmpty(StatusEnum.valueOf(wordStatus).name())) {
                    emptyStatus = true;
                }
            }
            if (emptyStatus) {
                errors.reject("dayform.word_status","Words status can't be empty.");
            }
        }

    };

    private void validateSentence(DayForm dayForm, Errors errors) {
        if (dayForm.getSentence() != null) {
            String sentence = dayForm.getSentence();
            sentence = sentence.trim();
            if (sentence.split(" ").length != 6) {
                errors.reject("dayform.sentence_words","Sentence has wrong number of words");
            }

            if (StringUtils.isEmpty(dayForm.getSentenceStatus()) || StringUtils.isEmpty(StatusEnum.valueOf(dayForm.getSentenceStatus()).name())) {
                errors.reject("dayform.sentence_status","Sentence status can't be empty.");
            }

        }

    };

}
