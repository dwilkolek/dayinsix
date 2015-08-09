package eu.wilkolek.diary.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.service.MetaService;

@ControllerAdvice
public class UnsupportedEncodingExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error/exception";
    @Autowired
    private ErrorRepository errorRepository;
    
    @Autowired
    private MetaService metaService;
    
    @ExceptionHandler(value = { UnsupportedEncodingException.class, NullPointerException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);

        metaService.updateModel(mav, "/error", new Meta(), null, null);
        
        if (errorRepository != null){
            eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error(e);
            errorRepository.save(ex);
        }
        
        return mav;
    }
}
