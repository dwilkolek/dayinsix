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
public class MessagingExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/messagingException";

    @Autowired
    private ErrorRepository errorRepository;
    
    @ExceptionHandler(value = { MessagingException.class })
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e, CurrentUser cu) throws Exception {
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        
        if (errorRepository != null){
            eu.wilkolek.diary.model.Error ex = new eu.wilkolek.diary.model.Error();
            Gson gson = new Gson();
            
            ex.setStacktrace(gson.toJson(e.getStackTrace()));
            ex.setMessage(e.getMessage());
            ex.setUser(cu != null ? cu.getUser() : null);
            errorRepository.save(ex);
        }
        
        return mav;
    }
}
