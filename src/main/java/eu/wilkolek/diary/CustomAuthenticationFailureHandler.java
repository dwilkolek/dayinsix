package eu.wilkolek.diary;

import com.google.gson.Gson;
import eu.wilkolek.diary.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class CustomAuthenticationFailureHandler implements
        AuthenticationFailureHandler {

    private static final Logger logger = Logger.getLogger(CustomAuthenticationFailureHandler.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public CustomAuthenticationFailureHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        Map<String, String[]> map = request.getParameterMap();

        Gson gson = new Gson();
        logger.info(gson.toJson(map));
        String url = "";
        if (map.containsKey("redirect")) {
            url = "?redirect=" + map.get("redirect")[0];
        }
        if (!StringUtils.isEmpty((String) request.getAttribute("redirect"))) {
            url = "?redirect=" + request.getAttribute("redirect");
        }
        if (!StringUtils.isEmpty(request.getParameter("redirect"))) {
            url = "?redirect=" + request.getParameter("redirect");
        }

        redirectStrategy.sendRedirect(request, response, "/login" + url + "&error=1");

    }


}
