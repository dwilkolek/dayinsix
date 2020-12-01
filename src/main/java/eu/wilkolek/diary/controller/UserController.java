package eu.wilkolek.diary.controller;

import eu.wilkolek.diary.dto.*;
import eu.wilkolek.diary.exception.NoSuchUserException;
import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.*;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MetaService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.DayHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import eu.wilkolek.diary.util.TimezoneUtils;


@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final DayRepository dayRepository;
    private final DictionaryWordRepository dictionaryWordRepository;
    private final DayFormValidator dayFormValidator;
    private final ProfileFormValidator profileFormValidator;

    private MetaService metaService;

    @InitBinder("form")
    public void initBinderProfile(WebDataBinder binder) {
        binder.addValidators(profileFormValidator);
    }

    @InitBinder("dayForm")
    public void initBinderDay(WebDataBinder binder) {
        binder.addValidators(dayFormValidator);
    }

    @Autowired
    public UserController(UserRepository userRepository, DayRepository dayRepository, DictionaryWordRepository dictionaryWordRepository, MetaService metaService) {
        this.userRepository = userRepository;
        this.dayRepository = dayRepository;
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.dayFormValidator = new DayFormValidator(userRepository);
        this.profileFormValidator = new ProfileFormValidator(userRepository);
        this.metaService = metaService;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/list")
    public ModelAndView days(CurrentUser currentUser, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws Exception {

        int DAYS_PER_PAGE = 10;

        Optional<User> user = userRepository.findById(currentUser.getId());
        if (!user.isPresent()) {
            throw new NoSuchUserException("[UserController.days] CurrentUser not found id=" + currentUser.getId(), currentUser);
        }

        ModelAndView model = new ModelAndView("user/day/list");

        DayViewData helper = DayHelper.createDataForView(dayRepository, page, user.get(), DAYS_PER_PAGE, user.get());

        model.getModelMap().put("cPage", helper.getcPage());
        model.getModelMap().put("days", helper.getDays());
        model.getModelMap().put("pages", helper.getPages());
        model.getModelMap().put("tPage", helper.gettPage());
        model.getModelMap().put("sPage", helper.getsPage());
        
        HashMap<String, String> replacement = new HashMap<String, String>();
        replacement.put("{page}",helper.getcPage()+"");
        model = metaService.updateModel(model, "/day/list{page}", new Meta(), replacement,"");

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
           
            model = metaService.updateModel(model, "/day/edit", new Meta(), null,"");
            model.getModelMap().addAttribute("shareStyles", ShareStyleEnum.asMap());
            return model;
        }
        User user = currentUser.getUser();
        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.stringDateToDate(dateStr);
        }
        if (date.after(DateTimeUtils.getCurrentUTCTime()) || date.before(currentUser.getUser().getCreated())) {
            throw new OutOfDateException("[UserController.editDaySave] Date is wrong", currentUser);
        }
        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(date, user);

        if (!isDay.isPresent()) {
            throw new OutOfDateException("[UserController.editDaySave] Can't edit ghost day.", currentUser);
        }

        if (date.getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("[UserController.editDaySave] Date is wrong", currentUser);
        }

        dayForm.setDayDate(date);

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
        boolean partialEdit = false;

        Optional<Day> dayToDelete = dayRepository.findByCreationDateAndUser(date, user);
        try {
            DayHelper.checkDate(null, dayToDelete.get().getCreationDate(), false, null, "This day isn't editable.");
        } catch (OutOfDateException e) {
            partialEdit = true;
            dayToDelete.get().setShareStyle(dayForm.getShareStyle());
            dayRepository.save(dayToDelete.get());
        }
        if (!partialEdit) {
            Day toSave = new Day( dayForm, user);
            toSave.setStoreDate(dayToDelete.get().getStoreDate());
            dayRepository.delete(dayToDelete.get());
            dayRepository.save(toSave);
        }

        return new ModelAndView("redirect:/user/day/list");
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/edit/{dateStr}", method = RequestMethod.GET)
    public ModelAndView editDay(CurrentUser currentUser, @PathVariable(value = "dateStr") String dateStr) throws ParseException, OutOfDateException {

        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.stringDateToDate(dateStr);
        }

        if (date.after(DateTimeUtils.getCurrentUTCTime()) || date.before(currentUser.getUser().getCreated())) {
            throw new OutOfDateException("[UserController.editDay] Date is wrong", currentUser);
        }
        User user = currentUser.getUser();
        Optional<Day> day = dayRepository.findByCreationDateAndUser(date, user);
        DayForm dayForm = new DayForm();
        dayForm.setDayDate(date);

        // if (!day.isPresent()) {
        // throw new OutOfDateException("You can't modify this day.");
        // }

        if (day.get().getSentence() != null || day.get().getWords() != null) {
            dayForm.assignDay(day.get());
        } else {
            throw new OutOfDateException("[UserController.editDay] Propably it's a wrong day (no sentence nor words)", currentUser);
        }
        boolean partialEdit = false;
        try {
            DayHelper.checkDate(null, day.get().getCreationDate(), false, null, "This day isn't editable.");
        } catch (OutOfDateException e) {
            partialEdit = true;
        }
        ModelAndView model = new ModelAndView("user/day/edit");
        model.getModelMap().put("status", StatusEnum.asMap());
        model.getModelMap().put("dayForm", dayForm);
        model.getModelMap().put("partialEdit", partialEdit);
        
        model = metaService.updateModel(model, "/day/edit", new Meta(), null,"");
        
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
            

            model = metaService.updateModel(model, "/day/add", new Meta(), null,"");
            
            model.getModelMap().put("shareStyles", ShareStyleEnum.asMap());
            return model;
        }
        User user = currentUser.getUser();
        Date date = DateTimeUtils.getCurrentUTCTime();

        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.stringDateToDate(dateStr);
        }
        if (date.after(DateTimeUtils.getCurrentUTCTime()) || date.before(currentUser.getUser().getCreated())) {
            throw new OutOfDateException("[UserController.saveDay] Date is wrong", currentUser);
        }
        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(date, user);

        if (isDay.isPresent()) {
            throw new OutOfDateException("[UserController.saveDay] This day is already saved.",currentUser);
        }

        if (date.getTime() > DateTimeUtils.getCurrentUTCTime().getTime()) {
            throw new OutOfDateException("[UserController.saveDay] Date is wrong",currentUser);
        }

        dayForm.setDayDate(date);

//        logger.info("User id:" + user.getId());
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
        dayToSave.setStoreDate(DateTimeUtils.getUTCDate());
        dayRepository.save(dayToSave);
        LOGGER.debug("User " + user.getEmail() + " added new day");

        return new ModelAndView("redirect:/user/day/list");
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/day/add/{dateStr}", method = RequestMethod.GET)
    public String add(Model model, @PathVariable(value = "dateStr") String dateStr, CurrentUser currentUser) throws ParseException, OutOfDateException {
        model.addAttribute("dayForm", new DayForm());
        model.addAttribute("date", dateStr);

        Date date = DateTimeUtils.getCurrentUTCTime();
        if (!StringUtils.isEmpty(dateStr)) {
            date = DateTimeUtils.stringDateToDate(dateStr);
        }
        
        Optional<Day> isDay = dayRepository.findByCreationDateAndUser(DateTimeUtils.stringDateToDate(dateStr), currentUser.getUser());

        if (isDay.isPresent()) {
            throw new OutOfDateException("[UserController.add] This day is already saved.", currentUser);
        }

        if (date.after(DateTimeUtils.getCurrentUTCTime()) || date.before(currentUser.getUser().getCreated())) {
            throw new OutOfDateException("[UserController.add] Date is wrong", currentUser);
        }

        model.asMap().put("status", StatusEnum.asMap());
        model.asMap().put("dayForm", new DayForm());
        
        model = metaService.updateModel(model, "/day/add", new Meta(), null,"");
        
        model.asMap().put("shareStyles", ShareStyleEnum.asMap());
        return "user/day/add";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/profile", method = RequestMethod.GET)
    public String profile(CurrentUser currentUser, Model model) {

        User user = currentUser.getUser();
        user = userRepository.findById(user.getId()).get();
        ProfileForm profileForm = new ProfileForm(user);

        prepareDataForModel(model);

        model.addAttribute("form", profileForm);

        model = metaService.updateModel(model, "/profile", new Meta(), null,"");
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
        User user = userRepository.findById(currentUser.getId()).get();
        profileForm.setEmail(profileForm.getEmail().toLowerCase());

        ProfileFormCustomValidator.validate(user, userRepository, profileForm, result);

        if (!result.hasErrors()) {
            user.updateUser(profileForm);
            user = userRepository.save(user);
            currentUser.setUser(user);

            List<Day> storedDays = dayRepository.findAllByUser(user);
            for (Day storedDay : storedDays) {
                if (!user.isEnabled()) {
                    storedDay.setUserProfileVisibility(ShareStyleEnum.PRIVATE.name());
                } else {
                    storedDay.setUserProfileVisibility(user.getOptions().get(UserOptions.PROFILE_VISIBILITY));
                }
            }
            dayRepository.saveAll(storedDays);
        } else {
            model.addAttribute("errors", profileForm.createMessages(result.getAllErrors()));
        }

        ProfileForm profileFormClean = new ProfileForm(user);

        prepareDataForModel(model);

        model.addAttribute("form", profileFormClean);
        model.addAttribute("saveSuccess", !result.hasErrors());


        metaService.updateModel(model, "/profile", new Meta(), null,"");

        if (!user.isEnabled()) {
            return "redirect:/logout/userDisabled";
        }
        return "user/profile";

    }

    // @PreAuthorize("hasAuthority('USER')")
    // @RequestMapping(value = "/user/share/{username}", method =
    // RequestMethod.POST)
    // public String followByPathParam(CurrentUser currentUser,Model model,
    // @PathVariable(value="username") String username) throws
    // NoSuchUserException {
    // return this.followBy(currentUser,model, username);
    // }

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
        model.asMap().put("follows", DayHelper.selectEnabled(userRepository.findAllById(currentUser.getUser().getFollowingBy())));
        

        model = metaService.updateModel(model, "/following", new Meta(), null,"");
        
        return "redirect:/user/following";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/share", method = RequestMethod.GET)
    public String shareWith(CurrentUser currentUser, Model model) {
        ArrayList<String> lookFor = currentUser.getUser().getSharingWith();
        if (lookFor == null) {
            lookFor = new ArrayList<String>();
        }
        model.asMap().put("shares", DayHelper.selectEnabled(userRepository.findAllById(lookFor)));

        model = metaService.updateModel(model, "/share", new Meta(), null,"");
        return "user/share";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/share/{username}", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView shareWithPathParam(CurrentUser currentUser, @PathVariable(value = "username") String username) throws NoSuchUserException {
        return this.shareWith(currentUser, username);
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/share", method = RequestMethod.POST)
    public ModelAndView shareWith(CurrentUser currentUser, @RequestParam(value = "username", required = true) String username) throws NoSuchUserException {
        Boolean isSharing = false;
        ModelAndView model = new ModelAndView("user/share");
        Optional<User> user = userRepository.findByUsername(username);
        
        model = metaService.updateModel(model, "/share", new Meta(), null,"");
        
        if (!user.isPresent()) {
            throw new NoSuchUserException("[UserController.shareWith] You can't share with non existing user", currentUser);
        }

        if (currentUser.getUser().getSharingWith() == null) {
            currentUser.getUser().setSharingWith(new ArrayList<String>());
        }

        for (String u : currentUser.getUser().getSharingWith()) {
            if (u.equals(user.get().getId())) {
                isSharing = true;

            }
        }

        if (isSharing) {
            model.getModelMap().put("msg", "You already share your diary with:" + user.get().getUsername() + ".");
        } else if (currentUser.getId().equals(user.get().getId())) {
            model.getModelMap().put("msg", "You always share your diary with yourself.");
        } else {
            model.getModelMap().put("msg", "From now on you will be sharing your days with: " + user.get().getUsername() + ".");
        }

        if (!isSharing && !currentUser.getId().equals(user.get().getId())) {
            currentUser.getUser().getSharingWith().add(user.get().getId());
            currentUser.setUser(userRepository.save(currentUser.getUser()));
        }

        model.getModelMap().put("username", user.get().getUsername());
        model.getModelMap().put("shares", DayHelper.selectEnabled(userRepository.findAllById(currentUser.getUser().getSharingWith())));
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
        model.asMap().put("follows", DayHelper.selectEnabled(userRepository.findAllById(lookFor)));

        model = metaService.updateModel(model, "/following", new Meta(), null,"");
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
        model.asMap().put("follows", DayHelper.selectEnabled(userRepository.findAllById(lookFor)));

        model = metaService.updateModel(model, "/followed", new Meta(), null,"");
        return "user/followed";
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/unfollow/{username}", method = { RequestMethod.POST, RequestMethod.GET })
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
    @RequestMapping(value = "/user/unshare/{username}", method = { RequestMethod.POST, RequestMethod.GET })
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

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/user/archive/{year}/{month}", method = RequestMethod.GET)
    public String archive(Model model, CurrentUser currentUser, @PathVariable(value = "year") Integer year, @PathVariable(value = "month") Integer month) throws OutOfDateException {
        User u = userRepository.findById(currentUser.getUser().getId()).get();
        this.dateCheckForArchive(currentUser,year,month);
        int monthEnd = month - 1;
        int yearEnd = year;
        monthEnd++;
        if (12 == monthEnd) {
            monthEnd = 1;
            yearEnd++;
        }
        Date dateStart = new GregorianCalendar(year, month - 1, 1).getTime();
        Date dateEnd = new GregorianCalendar(yearEnd, monthEnd, 1).getTime();
        dateEnd = new Date(dateEnd.getTime() - TimeUnit.DAYS.toMillis(1L));
        if (dateEnd.after(DateTimeUtils.getUTCDate())){
            dateEnd = DateTimeUtils.getCurrentUTCTime();
        }
        long dateStartMilis = dateStart.getTime();
        long dateCreatedMilis = u.getCreated().getTime();
        long dateEndMilis = dateEnd.getTime();

        Calendar nowCal = new GregorianCalendar();
        nowCal.setTime(new Date(DateTimeUtils.getCurrentUTCTime().getTime() + TimeUnit.DAYS.toMillis(31L)));

        
        
        if (dateCreatedMilis > dateStartMilis) {
            dateStart = new Date(dateCreatedMilis);
        }
      
        ArrayList<Day> days = dayRepository.getDaysFromDateToDate(u, dateStart, dateEnd);
        ArrayList<DayView> daysView = DayHelper.fillDates(days, dateStart, dateEnd, "e msg", u, u);

        model.asMap().put("days", daysView);
        model.asMap().put("dateStr", DayHelper.createDateStr(month - 1, year));
        
        HashMap<String, String> replacement = new HashMap<String, String>();
        replacement.put("{dateString}",DayHelper.createDateStr(month - 1, year));
        model = metaService.updateModel(model, "/archive", new Meta(), replacement,"");
        
        return "user/day/archive";
    }

    private void dateCheckForArchive(CurrentUser u, Integer y, Integer m) throws OutOfDateException {
        LinkedHashMap<String,String> dateChaeck = UserController.createArchiveMenu(u.getUser());
               for (String date : dateChaeck.keySet()){
                   if (date.equals(y+"/"+m)){
                       return;
                   }
               }
        throw new OutOfDateException("[UserController.dateCheckForArchive] Date is wrong",u);
    }

    public static LinkedHashMap<String, String> createArchiveMenu(User u) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(u.getCreated());
        int monthStart = cal.get(Calendar.MONTH);
        int yearStart = cal.get(Calendar.YEAR);

        Calendar calNow = new GregorianCalendar();
        calNow.setTime(DateTimeUtils.getCurrentUTCTime());
        int monthEnd = calNow.get(Calendar.MONTH);
        int yearEnd = calNow.get(Calendar.YEAR);

        int m = monthEnd;
        int y = yearEnd;

        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

        while (!(monthStart == m && yearStart == y)) {
            String text = DayHelper.createDateStr(m, y);
            result.put(y + "/" + (m + 1), text);
            m--;
            if (m < 0) {
                m = 11;
                y--;
                result.put("divider" + y, "divider");
            }
        }
        if (monthStart == m && yearStart == y){
            String text = DayHelper.createDateStr(m, y);
            result.put(y + "/" + (m + 1), text);
        }
        
        return result;

    }

}
