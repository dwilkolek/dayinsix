package eu.wilkolek.diary.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.gs.collections.impl.list.Interval;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.dto.DayFormValidator;
import eu.wilkolek.diary.dto.ProfileForm;
import eu.wilkolek.diary.dto.ProfileFormCustomValidator;
import eu.wilkolek.diary.dto.ProfileFormValidator;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.DayViewData;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.StatusEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
//import eu.wilkolek.diary.util.TimezoneUtils;
import eu.wilkolek.diary.util.DayHelper;
import eu.wilkolek.diary.util.MetadataHelper;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final DayRepository dayRepository;
    private final DictionaryWordRepository dictionaryWordRepository;
    private final DayFormValidator dayFormValidator;
    private final ProfileFormValidator profileFormValidator;
    private CurrentUser currentUser;

    @InitBinder("form")
    public void initBinderProfile(WebDataBinder binder) {
        binder.addValidators(profileFormValidator);
    }

    @InitBinder("dayForm")
    public void initBinderDay(WebDataBinder binder) {
        binder.addValidators(dayFormValidator);
    }

    @Autowired
    public UserController(UserRepository userRepository, DayRepository dayRepository, DictionaryWordRepository dictionaryWordRepository) {
        this.userRepository = userRepository;
        this.dayRepository = dayRepository;
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.dayFormValidator = new DayFormValidator(userRepository);
        this.profileFormValidator = new ProfileFormValidator(userRepository);
    }

//    @PreAuthorize("hasAuthority('USER')")
//    @RequestMapping(value = "/user/details")
//    public ModelAndView details() {
//        return new ModelAndView("/user/details");
//    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/list")
    public ModelAndView days(CurrentUser currentUser, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws Exception {
        
        int DAYS_PER_PAGE = 10;

        Optional<User> user = userRepository.findById(currentUser.getId());
        if (!user.isPresent()) {
            throw new NoSuchUserException("CurrentUser not found id=" + currentUser.getId());
        }

        ModelAndView model = new ModelAndView("user/day/list");

        DayViewData helper = DayHelper.createDataForView(dayRepository, page, user.get(), DAYS_PER_PAGE, user.get());

        model.getModelMap().put("cPage", helper.getcPage());
        model.getModelMap().put("days", helper.getDays());
        model.getModelMap().put("pages", helper.getPages());
        model.getModelMap().put("tPage", helper.gettPage());
        model.getModelMap().put("sPage", helper.getsPage());
        
        model.getModelMap().addAttribute("title", MetadataHelper.title("Your diary"));
        
        return model;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/")
    public String defaultPage(CurrentUser currentUser) throws Exception {
        return "redirect:/user/day/list";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/edit/{dateStr}", method = RequestMethod.POST)
    public ModelAndView editDaySave(CurrentUser currentUser, @PathVariable(value = "dateStr") String dateStr, @Valid DayForm dayForm, BindingResult result)
            throws ParseException, OutOfDateException {

       // this.dayFormValidator.validate(dayForm, result);

        if (result.hasErrors()) {
            ModelAndView model = new ModelAndView("user/day/edit");
            model.getModelMap().put("dayForm", dayForm);
            model.getModelMap().put("status", StatusEnum.asMap());
            model.getModelMap().put("errors", result.getAllErrors());
            model.getModelMap().addAttribute("title", MetadataHelper.title("Change your day"));
            model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
            return model;
        }
        User user = currentUser.getUser();
        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.StringDateToDate(dateStr);
        }

        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(date, user);

        if (!isDay.isPresent()) {
            throw new OutOfDateException("This day is already saved.");
        }

        if (date.getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("Your date is newer than UTC+0");
        }

        dayForm.setDayDate(date);

        System.out.println("User id:" + user.getId());
        if (dayForm.getWords() != null && dayForm.getWords().size() > 0) {
            ArrayList<DictionaryWord> resultList = new ArrayList<DictionaryWord>();
            for (int i = 0; i < dayForm.getWords().size(); i++) {
                String value = dayForm.getWords().get(i);

                Optional<DictionaryWord> dictWordO = dictionaryWordRepository.findByValueAndUser(value, user);
                if (dictWordO.isPresent()) {
                    resultList.add(dictWordO.get());
                } else {
                    DictionaryWord dw = new DictionaryWord();
                    dw.setValue(value);
                    dw.setUser(user);
                    DictionaryWord dwSaved = dictionaryWordRepository.save(dw);
                    resultList.add(dwSaved);
                }
            }
            dayForm.setDictionaryWords(resultList);
        }

        Optional<Day> dayToDelete = dayRepository.findByCreationDateAndUser(date, user);
        dayRepository.delete(dayToDelete.get());
        dayRepository.save(new Day(dayForm, user));
        LOGGER.debug("User " + user.getEmail() + " added new day");

        
        
        return new ModelAndView("redirect:/user/day/list");
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/edit/{dateStr}", method = RequestMethod.GET)
    public ModelAndView editDay(CurrentUser currentUser, @PathVariable(value = "dateStr") String dateStr) throws ParseException, OutOfDateException {

        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.StringDateToDate(dateStr);
        }

        if (date.getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("Your date is newer than UTC+0");
        }
        User user = currentUser.getUser();
        Optional<Day> day = dayRepository.findByCreationDateAndUser(date, user);
        DayForm dayForm = new DayForm();
        dayForm.setDayDate(date);

        if (!day.isPresent()) {
            throw new OutOfDateException("You can't modify this day.");
        }

        if (day.get().getSentence() != null || day.get().getWords() != null) {
            dayForm.assignDay(day.get());
        } else {
            throw new OutOfDateException("Propably it's a wrong day...");
        }

        DayHelper.checkDate(null, day.get().getCreationDate(), false, null, "This day isn't editable.");

        ModelAndView model = new ModelAndView("user/day/edit");
        model.getModelMap().put("status", StatusEnum.asMap());
        model.getModelMap().put("dayForm", dayForm);
        model.getModelMap().addAttribute("title", MetadataHelper.title("Change your day"));
        model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
        return model;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/add/{dateStr}", method = RequestMethod.POST)
    public ModelAndView saveDay(CurrentUser currentUser, @Valid DayForm dayForm, BindingResult result, @PathVariable(value = "dateStr") String dateStr)
            throws ParseException, OutOfDateException {

       // this.dayFormValidator.validate(dayForm, result);

        if (result.hasErrors()) {
            ModelAndView model = new ModelAndView("user/day/add");
            model.getModelMap().put("dayForm", dayForm);
            model.getModelMap().put("status", StatusEnum.asMap());
            model.getModelMap().put("errors", result.getAllErrors());
            model.getModelMap().addAttribute("title", MetadataHelper.title("Save the day"));
            model.getModelMap().put("shareStyles", ShareStyleEnum.asMap());
            return model;
        }
        User user = currentUser.getUser();
        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.StringDateToDate(dateStr);
        }

        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(date, user);

        if (isDay.isPresent()) {
            throw new OutOfDateException("This day is already saved.");
        }

        if (date.getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("Your date is newer than UTC+0");
        }

        dayForm.setDayDate(date);
        

        System.out.println("User id:" + user.getId());
        if (dayForm.getWords() != null && dayForm.getWords().size() > 0) {
            ArrayList<DictionaryWord> resultList = new ArrayList<DictionaryWord>();
            for (int i = 0; i < dayForm.getWords().size(); i++) {
                String value = dayForm.getWords().get(i);

                Optional<DictionaryWord> dictWordO = dictionaryWordRepository.findByValueAndUser(value, user);
                if (dictWordO.isPresent()) {
                    resultList.add(dictWordO.get());
                } else {
                    DictionaryWord dw = new DictionaryWord();
                    dw.setValue(value);
                    dw.setUser(user);
                    DictionaryWord dwSaved = dictionaryWordRepository.save(dw);
                    resultList.add(dwSaved);
                }
            }
            dayForm.setDictionaryWords(resultList);
        }
        Day dayToSave = new Day(dayForm, user);
        dayToSave.setStoreDate(DateTimeUtils.getCurrentUTCTime());
        dayRepository.save(dayToSave);
        LOGGER.debug("User " + user.getEmail() + " added new day");

        return new ModelAndView("redirect:/user/day/list");
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/add/{date}", method = RequestMethod.GET)
    public String add(Model model, @PathVariable(value = "date") String date, CurrentUser currentUser) throws ParseException, OutOfDateException {
        model.addAttribute("dayForm", new DayForm());
        model.addAttribute("date", date);

        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(DateTimeUtils.StringDateToDate(date), currentUser.getUser());

        if (isDay.isPresent()) {
            throw new OutOfDateException("This day is already saved.");
        }

        if (DateTimeUtils.StringDateToDate(date).getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("Your date is newer than UTC+0");
        }

        model.asMap().put("status", StatusEnum.asMap());
        model.asMap().put("dayForm", new DayForm());
        model.asMap().put("title", MetadataHelper.title("Save the day"));
        model.asMap().put("shareStyles", ShareStyleEnum.asMap());
        return "user/day/add";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/profile", method = RequestMethod.GET)
    public String profile(CurrentUser currentUser, Model model) {

        User user = currentUser.getUser();
        user = userRepository.findOne(user.getId());
        ProfileForm profileForm = new ProfileForm(user);

        prepareDataForModel(model);

        model.addAttribute("form", profileForm);
        model.asMap().put("title", MetadataHelper.title("Your profile"));
        return "user/profile";
    }

    private void prepareDataForModel(Model model) {
        // model.addAttribute("timezones", TimezoneUtils.getTimeZones());
        model.addAttribute("shareStyles", ShareStyleEnum.asMap());
        model.addAttribute("inputTypes", InputTypeEnum.asMap());
        model.addAttribute("notificationFrequencyTypes", NotificationTypesEnum.asMap());
        
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/profile", method = RequestMethod.POST)
    public String profileSave(CurrentUser currentUser, Model model, ProfileForm profileForm, BindingResult result) {
        boolean saveSuccess = false;
        User user = userRepository.findOne(currentUser.getId());
        profileForm.setEmail(profileForm.getEmail().toLowerCase());
       // ProfileFormCustomValidator validator = new ProfileFormCustomValidator();
        //validator.validate(user, userRepository, profileForm, result);

        ProfileFormCustomValidator.validate(user, userRepository, profileForm, result);

        if (!result.hasErrors()) {
            user.updateUser(profileForm);
            user = userRepository.save(user);
            currentUser.setUser(user);
            
            List<Day> storedDays = dayRepository.findAllByUser(user);
            for (Day storedDay : storedDays){
                storedDay.setUserProfileVisibility(user.getOptions().get(UserOptions.PROFILE_VISIBILITY));
            }
            storedDays = dayRepository.save(storedDays);
        } else {
            model.addAttribute("errors", profileForm.createMessages(result.getAllErrors()));
        }

        ProfileForm profileFormClean = new ProfileForm(user);

        prepareDataForModel(model);

        model.addAttribute("form", profileFormClean);
        saveSuccess = true;
        model.addAttribute("saveSuccess", !result.hasErrors());
        model.addAttribute("title", MetadataHelper.title("Your profile"));
        
        return "user/profile";

    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/follow/{username}")
    public String followBy(CurrentUser currentUser, Model model, @PathVariable(value = "username") String username) {
        Boolean isFollowing = false;

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            return "exception/noSuchUserException";
        }

        User cu = userRepository.findById(currentUser.getUser().getId()).get();

        if (cu.getFollowingBy() == null) {
            cu.setFollowingBy(new ArrayList<String>());
        }

        for (String u : cu.getFollowingBy()) {
            if (u.equals(user.get().getId())) {
                isFollowing = true;

            }
        }
        if (!isFollowing) {
            cu.getFollowingBy().add(user.get().getId());
            currentUser.setUser(userRepository.save(cu));
            if (user.get().getFollowedBy() == null) {
                user.get().setFollowedBy(new ArrayList<String>());
            }
            user.get().getFollowedBy().add(cu.getId());
            userRepository.save(user.get());
        }

        model.asMap().put("username", user.get().getUsername());
        model.asMap().put("follows", userRepository.findAll(currentUser.getUser().getFollowingBy()));
        model.asMap().put("title", MetadataHelper.title("Followed by you"));
        return "redirect:/user/following";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/share", method = RequestMethod.GET)
    public String shareWith(CurrentUser currentUser, Model model) {
        ArrayList<String> lookFor = currentUser.getUser().getSharingWith();
        if (lookFor == null) {
            lookFor = new ArrayList<String>();
        }
        model.asMap().put("shares", userRepository.findAll(lookFor));
        model.asMap().put("title", MetadataHelper.title("Users you share days with"));
        return "user/share";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/share", method = RequestMethod.POST)
    public ModelAndView shareWith(CurrentUser currentUser, @RequestParam(value = "username", required = true) String username) throws NoSuchUserException {
        Boolean isSharing = false;
        ModelAndView model = new ModelAndView("user/share");
        Optional<User> user = userRepository.findByUsername(username);
        model.getModelMap().addAttribute("title", MetadataHelper.title("Users you share days with"));
        if (!user.isPresent()) {
            throw new NoSuchUserException("You can't share with non existing user");
        }

        if (currentUser.getUser().getSharingWith() == null) {
            currentUser.getUser().setSharingWith(new ArrayList<String>());
        }

        for (String u : currentUser.getUser().getSharingWith()) {
            if (u.equals(user.get().getId())) {
                isSharing = true;

            }
        }
        if (!isSharing) {
            currentUser.getUser().getSharingWith().add(user.get().getId());
            currentUser.setUser(userRepository.save(currentUser.getUser()));
        }

        model.getModelMap().put("username", user.get().getUsername());
        model.getModelMap().put("shares", userRepository.findAll(currentUser.getUser().getSharingWith()));
        return model;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/following", method = RequestMethod.GET)
    public String following(CurrentUser currentUser, Model model) {
        User userByDb = userRepository.findById(currentUser.getId()).get();
        ArrayList<String> lookFor = userByDb.getFollowingBy();
        if (lookFor == null) {
            lookFor = new ArrayList<String>();
        }
        currentUser.setUser(userByDb);
        model.asMap().put("follows", userRepository.findAll(lookFor));
        model.asMap().put("title", MetadataHelper.title("Followed by you"));
        return "user/following";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/followed", method = RequestMethod.GET)
    public String followed(CurrentUser currentUser, Model model) {
        User userByDb = userRepository.findById(currentUser.getId()).get();
        ArrayList<String> lookFor = userByDb.getFollowedBy();
        if (lookFor == null) {
            lookFor = new ArrayList<String>();
        }
        currentUser.setUser(userByDb);
        model.asMap().put("follows", userRepository.findAll(lookFor));
        model.asMap().put("title", MetadataHelper.title("Your followers"));
        return "user/followed";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/unfollow/{username}")
    public String unfollow(CurrentUser currentUser, @PathVariable(value = "username") String username) throws NoSuchUserException {
        User currentUserDb = userRepository.findById(currentUser.getUser().getId()).get();
        Optional<User> otherUserDb = userRepository.findByUsername(username);
        ArrayList<String> newCurrentArray = new ArrayList<String>();
        if (currentUserDb.getFollowingBy() != null) {
            /* logged user */
            for (String uop : currentUserDb.getFollowingBy()) {
                if (!uop.equals(otherUserDb.get().getId())) {
                    newCurrentArray.add(uop);
                }
            }
            currentUserDb.setFollowingBy(newCurrentArray);
            currentUser.setUser(userRepository.save(currentUserDb));
        }
        if (otherUserDb.get().getFollowedBy() != null) {
            ArrayList<String> newOtherArray = new ArrayList<String>();
            for (String uop : otherUserDb.get().getFollowedBy()) {
                if (!uop.equals(currentUser.getId())) {
                    newOtherArray.add(uop);
                }
            }

            
            otherUserDb.get().setFollowedBy(newOtherArray);

            userRepository.save(otherUserDb.get());

            
        }

        return "redirect:/user/following";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/unshare/{username}", method = RequestMethod.GET)
    public String unshare(Model model, CurrentUser currentUser, @PathVariable(value = "username") String username) {
        User u = userRepository.findById(currentUser.getUser().getId()).get();
        Optional<User> op = userRepository.findByUsername(username);

        ArrayList<String> toRemove = new ArrayList<String>();

        if (u.getSharingWith() != null) {
            for (String uop : u.getSharingWith()) {
                if (uop.equals(op.get().getId())) {
                    toRemove.add(uop);
                }
            }
            u.getSharingWith().removeAll(toRemove);
            currentUser.setUser(userRepository.save(u));
        }
        return "redirect:/user/share";
    }
    
    
    

}
