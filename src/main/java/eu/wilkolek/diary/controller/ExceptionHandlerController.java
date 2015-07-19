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
public class ExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "__error";

    @ExceptionHandler(value = {OutOfDateException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception{
            ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
            
            
                mav.addObject("datetime", new Date());
                mav.addObject("exception", e);
                mav.addObject("url", request.getRequestURL());

//                eu.wilkolek.diary.model.Error error = new eu.wilkolek.diary.model.Error();
//                if (currentUser != null) {error.setUser(currentUser.getUser()); }
//                error.setMessage(e.getMessage());
//                error.setException(e);
//                errorRepository.save(error);
            
        return mav;
    }
}