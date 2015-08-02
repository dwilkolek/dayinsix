package eu.wilkolek.diary.controller;


import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.repository.ErrorRepository;

@ControllerAdvice
public class ConnectExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error/exception";

    @Autowired
    private ErrorRepository errorRepository;
    
    @ExceptionHandler(value = { java.net.ConnectException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e, CurrentUser cu) throws Exception {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        
        if (errorRepository != null){
            eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error(e, cu);
            errorRepository.save(ex);
        }
        
        return mav;
    }
}
