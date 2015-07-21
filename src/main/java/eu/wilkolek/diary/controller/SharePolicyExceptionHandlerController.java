package eu.wilkolek.diary.controller;

import org.springframework.security.access.AccessDeniedException;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.repository.ErrorRepository;

@ControllerAdvice
public class SharePolicyExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/sharePolicyException";

    @ExceptionHandler(value = { OutOfDateException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        return mav;
    }
}