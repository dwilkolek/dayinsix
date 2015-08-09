package eu.wilkolek.diary;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import eu.wilkolek.diary.controller.UserController;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
            ServletException {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = userRepository.findOne(currentUser.getId());
        Date date = DateTimeUtils.getUTCDate();
        user.setLastLogIn(date);

        currentUser.setUser(userRepository.save(user));

        String redirectTo = request.getParameter("redirect");
        Gson gson = new Gson();
        String x = request.getRequestURL().toString();
        if (!StringUtils.isEmpty(redirectTo)) {
            
            int start = x.indexOf("login");
            x = x.substring(0,start);
            if (redirectTo.contains("thankyou") || redirectTo.contains("userDisabled") || redirectTo.contains("activate")  || x.equals(redirectTo)) {
                redirectStrategy.sendRedirect(request, response, "/user/day/list");
                return;
            }
            redirectStrategy.sendRedirect(request, response, redirectTo);

        }

    }

}
