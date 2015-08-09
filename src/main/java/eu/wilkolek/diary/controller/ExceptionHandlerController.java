package eu.wilkolek.diary.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.service.MetaService;

@ControllerAdvice
public class ExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/exception";

    @Autowired
    private ErrorRepository errorRepository;
    
    @Autowired
    private MetaService metaService;
    

    @ExceptionHandler(value = { Exception.class, RuntimeException.class, org.springframework.security.access.AccessDeniedException.class,NoHandlerFoundException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        
        metaService.updateModel(mav, "/error", new Meta(), null, null);
        
        eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error(e);
        errorRepository.save(ex);
        return mav;
    }

   
}