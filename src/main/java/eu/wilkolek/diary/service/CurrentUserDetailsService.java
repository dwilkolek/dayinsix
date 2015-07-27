package eu.wilkolek.diary.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;


@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserDetailsService.class);
    private final UserRepository userRepository;

    @Autowired
    public CurrentUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Authenticating user with email={}", email.replaceFirst("@.*", "@***"));
        Optional<User> user = userRepository.findByEmail(email.toLowerCase());
        if (!user.isPresent()){
        	new UsernameNotFoundException(String.format("User with email=%s was not found", email));
        }
        if (!user.get().isEnabled() && !StringUtils.isEmpty(user.get().getToken())){
            throw new UsernameNotFoundException(String.format("User with email=%s isn't enabled", email));
        }
        if (!user.get().isEnabled() && StringUtils.isEmpty(user.get().getToken())){
            user.get().setEnabled(true);
            return new CurrentUser(userRepository.save(user.get()));
        }
        
        return new CurrentUser(user.get());
    }

}