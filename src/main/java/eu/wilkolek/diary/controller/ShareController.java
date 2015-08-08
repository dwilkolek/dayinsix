package eu.wilkolek.diary.controller;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.exception.UserIsDisabledException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.DayViewData;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.StatusEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MetaService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.DayHelper;


@Controller
public class ShareController {
    
    private final UserRepository userRepository;
    private final DayRepository dayRepository;
    private MetaService metaService;
    
    @Autowired
    public ShareController(UserRepository userRepository, DayRepository dayRepository, MetaService metaService) {
        this.dayRepository = dayRepository;
        this.userRepository = userRepository;
        this.metaService = metaService;
    }
    
    
    @RequestMapping("/s/{username}")
    public String sharePageStart(Model model,  @PathVariable(value = "username") String username, CurrentUser currentUser) throws NoSuchUserException, UserIsDisabledException {
        return this.sharePage(model, username, 1, currentUser);
    }
    
    @RequestMapping("/s/{username}/{page}")
    public String sharePage(Model model,  @PathVariable(value = "username") String username, @PathVariable(value = "page") Integer page, CurrentUser currentUser) throws NoSuchUserException, UserIsDisabledException {
        
        Optional<User> user = userRepository.findByUsername(username);
        
        if (!user.isPresent()){
            throw new NoSuchUserException("Looking for user ["+username+"] failed.");
        }
        if (!user.get().isEnabled()){
            throw new UserIsDisabledException("User ["+username+"] is disabled.");
        }
        if (user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY).equals(ShareStyleEnum.PRIVATE.name())){
            if (currentUser == null){
                return "sharePage/private";
            }
            if (!currentUser.getUser().getId().equals(user.get().getId())){
                return "sharePage/private";
            }
        }
        if ((user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY).equals(ShareStyleEnum.PROTECTED.name()) || user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY).equals(ShareStyleEnum.FOR_SELECTED.name())) && currentUser == null){
            model.asMap().put("username", user.get().getUsername()+"'s");
            if (user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY).equals(ShareStyleEnum.FOR_SELECTED.name()) && currentUser == null){
                return "sharePage/cantShare";
            }
            return "sharePage/notLoggedIn";
        }
        if (user.get().getOptions().get(UserOptions.PROFILE_VISIBILITY).equals(ShareStyleEnum.FOR_SELECTED.name())){
            
            boolean canShare = false;
            if (user.get().getSharingWith() != null){
                for (String id : user.get().getSharingWith()){
                    if (id.equals(currentUser.getUser().getId())){
                        canShare = true;
                    }
                }
            }
            if (user.get().getId().equals(currentUser.getId())){
                canShare = true;
            }
            if (!canShare){
                return "sharePage/cantShare";
            }
        }
        
        model.asMap().put("user", user.get());
        
        int DAYS_PER_PAGE = 10;

        DayViewData helper = DayHelper.createDataForView(dayRepository, page, user.get(), DAYS_PER_PAGE, (currentUser != null) ? currentUser.getUser() : null);
        
        model.asMap().put("cPage", helper.getcPage());
        model.asMap().put("days", helper.getDays());
        model.asMap().put("pages", helper.getPages());
        model.asMap().put("tPage", helper.gettPage());
        model.asMap().put("sPage", helper.getsPage());
        model.asMap().put("usersTitle", username+"'s diary");
        model.asMap().put("user", user.get());
        
        HashMap<String, String> replacement = new HashMap<String, String>();
        replacement.put("{page}",helper.getcPage()+"");
        replacement.put("{user}",username);
        model = metaService.updateModel(model, "/s{page}", new Meta(), replacement,(!StringUtils.isEmpty(user.get().getAbout()) && user.get().getAbout().length() > 160) ? user.get().getAbout().substring(0, 155) + "..." : "");
        
        if (currentUser != null){
        
            ArrayList<String> following = currentUser.getUser().getFollowingBy();
            if (following == null){
                following = new ArrayList<String>();
            }
            
            model.asMap().put("follows", following.contains(user.get().getId()));
        } else {
            model.asMap().put("follows", false);
        }
        return "sharePage/sharePage";
    }
    
}
