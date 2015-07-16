package eu.wilkolek.diary;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.controller.UserController;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;

@Component
public class CustomAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = userRepository.findOne(currentUser.getId());
        Date date = DateTimeUtils.getCurrentUTCTime();
        user.setLastLogIn(date);

        currentUser.setUser(userRepository.save(user));

    }

}
