package eu.wilkolek.diary.controller;

import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
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
public class AccessDeniedExceptionHandlerController {

    public static final String DEFAULT_ERROR_VIEW = "error/messagingException";

    @Autowired
    private ErrorRepository errorRepository;
    
    @Autowired
    private MetaService metaService;
    
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    @ExceptionHandler(value = { org.springframework.security.access.AccessDeniedException.class })
    public void handler(HttpServletRequest request,HttpServletResponse response, Exception e) throws Exception {

           String url = "?redirect="+request.getRequestURI();
//           logger.info();
           redirectStrategy.sendRedirect(request, response, "/login?redirect="+request.getRequestURL()+"");

    }
}
