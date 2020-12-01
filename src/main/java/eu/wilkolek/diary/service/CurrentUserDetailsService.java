package eu.wilkolek.diary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.UserRepository;


@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserDetailsService.class);
    private final UserRepository userRepository;
    private final DayRepository dayRepository;

    @Autowired
    public CurrentUserDetailsService(UserRepository userRepository, DayRepository dayRepository) {
        this.userRepository = userRepository;
        this.dayRepository = dayRepository;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Authenticating user with email={}", email.replaceFirst("@.*", "@***"));
        Optional<User> user = userRepository.findByEmail(email.toLowerCase());
        if (!user.isPresent()){
            return null;//new UsernameNotFoundException(String.format("User with email=%s was not found", email));
        }
        if (!user.get().isEnabled() && !StringUtils.isEmpty(user.get().getToken())){
            return null;//throw new UsernameNotFoundException(String.format("User with email=%s isn't enabled", email));
        }
        if (!user.get().isEnabled() && StringUtils.isEmpty(user.get().getToken())){
            user.get().setEnabled(true);
            List<Day> storedDays = dayRepository.findAllByUser(user.get());
            for (Day storedDay : storedDays) {          
                storedDay.setUserProfileVisibility(user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY));
            }
            
            dayRepository.saveAll(storedDays);
//            logger.info("Stored : "+storedDays.size());
            return new CurrentUser(userRepository.save(user.get()));
        }
        
        return new CurrentUser(user.get());
    }

}