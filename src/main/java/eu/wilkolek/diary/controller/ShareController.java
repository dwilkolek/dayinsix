package eu.wilkolek.diary.controller;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.DayViewData;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.StatusEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.DayHelper;


@Controller
public class ShareController {
    
    private final UserRepository userRepository;
    private final DayRepository dayRepository;
    
    @Autowired
    public ShareController(UserRepository userRepository, DayRepository dayRepository) {
        this.dayRepository = dayRepository;
        this.userRepository = userRepository;
    }
    
    
    @RequestMapping("/s/{username}")
    public String sharePageStart(Model model,  @PathVariable(value = "username") String username, CurrentUser currentUser) throws NoSuchUserException {
        return this.sharePage(model, username, 1, currentUser);
    }
    
    @RequestMapping("/s/{username}/{page}")
    public String sharePage(Model model,  @PathVariable(value = "username") String username, @PathVariable(value = "page") Integer page, CurrentUser currentUser) throws NoSuchUserException {
        
        Optional<User> user = userRepository.findByUsername(username);
        
        if (!user.isPresent()){
            throw new NoSuchUserException("Looking for user ["+username+"] failed.");
        }
        
        if (user.get().getOptions().get(UserOptions.SHARE_STYLE).equals(ShareStyleEnum.PRIVATE.name())){
            return "sharePage/private";
        }
        if ((user.get().getOptions().get(UserOptions.SHARE_STYLE).equals(ShareStyleEnum.PROTECTED.name()) || user.get().getOptions().get(UserOptions.SHARE_STYLE).equals(ShareStyleEnum.FOR_SELECTED.name())) && currentUser == null){
            return "sharePage/notLoggedIn";
        }
        if (user.get().getOptions().get(UserOptions.SHARE_STYLE).equals(ShareStyleEnum.FOR_SELECTED.name())){
            
            boolean canShare = false;
            
            for (String id : user.get().getSharingWith()){
                if (id.equals(currentUser.getUser().getId())){
                    canShare = true;
                }
            }
            
            if (!canShare){
                return "sharePage/cantShare";
            }
        }
        
        model.asMap().put("user", user.get());
        model.asMap().put("title", "Share page of user "+username);
        model.asMap().put("description", "Share page of user");
        
        int DAYS_PER_PAGE = 10;

        DayViewData helper = DayHelper.createDataForView(dayRepository, page, user.get(), DAYS_PER_PAGE);
        
        model.asMap().put("cPage", helper.getcPage());
        model.asMap().put("days", helper.getDays());
        model.asMap().put("pages", helper.getPages());
        model.asMap().put("tPage", helper.gettPage());
        model.asMap().put("sPage", helper.getsPage());
       
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
