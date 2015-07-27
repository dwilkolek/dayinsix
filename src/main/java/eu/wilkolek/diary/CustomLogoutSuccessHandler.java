package eu.wilkolek.diary;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.controller.UserController;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;

@Component
public class CustomLogoutSuccessHandler implements
        LogoutSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    public CustomLogoutSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
        
        
        String redirect ="/"; // = determineTargetUrl(request, response);
        String addr = request.getRequestURI().replace("/logout", "");
        if (!addr.equals("/ok")){
            redirect = addr;
        }
        redirectStrategy.sendRedirect(request, response, redirect);
    }

}
