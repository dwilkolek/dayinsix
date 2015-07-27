package eu.wilkolek.diary.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.exception.NoSuchUserException;

@ControllerAdvice
public class MessagingExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/messagingException";

    @ExceptionHandler(value = { MessagingException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);

        return mav;
    }
}
