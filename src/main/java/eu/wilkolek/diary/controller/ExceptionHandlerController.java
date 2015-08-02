package eu.wilkolek.diary.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.repository.ErrorRepository;

@ControllerAdvice
public class ExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/exception";

    @Autowired
    private ErrorRepository errorRepository;
    
    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e, CurrentUser cu) {
            ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
            eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error(e, cu);
            errorRepository.save(ex);
        return mav;
    }
}