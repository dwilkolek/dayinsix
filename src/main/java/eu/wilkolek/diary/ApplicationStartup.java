package eu.wilkolek.diary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import ch.qos.logback.core.util.TimeUtil;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.InputTypeEnum;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.Sentence;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.StatusEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;
import eu.wilkolek.diary.model.WebsiteOptions;
import eu.wilkolek.diary.model.Word;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.MailRepository;
import eu.wilkolek.diary.repository.MetaRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.repository.WebsiteOptionsRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.OptionMap;

//import eu.wilkolek.diary.util.TimezoneUtils;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private DictionaryWordRepository dictionaryWordRepository;
    private UserRepository userRepository;
    private DayRepository dayRepository;
    private MailRepository mailRepository;
    private ErrorRepository errorRepository;
    private MetaRepository metaRepository;
    private WebsiteOptionsRepository websiteOptionsRepository;

    @Autowired
    public ApplicationStartup(DictionaryWordRepository dictionaryWordRepository, UserRepository userRepository, DayRepository dayRepository,
            ErrorRepository errorRepository, MailRepository mailRepository, MetaRepository metaRepository, WebsiteOptionsRepository websiteOptionsRepository) {
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.dayRepository = dayRepository;
        this.userRepository = userRepository;
        this.mailRepository = mailRepository;
        this.errorRepository = errorRepository;
        this.metaRepository = metaRepository;
        this.websiteOptionsRepository = websiteOptionsRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!StringUtils.isEmpty(System.getProperty("clean"))) {
            this.cleanUp();
        }
        if (!StringUtils.isEmpty(System.getProperty("dictionary.words"))) {
            this.setupDictionaryEN();
        }
        if (!StringUtils.isEmpty(System.getProperty("debug"))) {
            this.prepareTestUserWithData();
            this.prepareNotyficationTest();
        }
        if (!StringUtils.isEmpty(System.getProperty("preload"))) {
            this.preloadOptions();
            this.preloadMeta();
        }
    }

    private void cleanUp() {
        this.dictionaryWordRepository.deleteAll();
        this.dayRepository.deleteAll();
        this.userRepository.deleteAll();
        this.mailRepository.deleteAll();
        this.errorRepository.deleteAll();
        this.metaRepository.deleteAll();
        this.websiteOptionsRepository.deleteAll();
        
    }

    private void setupDictionaryEN() {
        String fileLocalization = System.getProperty("dictionary.words");
        File file = new File(fileLocalization);
        LinkedList<DictionaryWord> words = new LinkedList<DictionaryWord>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                DictionaryWord word = new DictionaryWord();
                word.setValue(line);
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dictionaryWordRepository.save(words);
        System.out.println("dictionary word list: " + fileLocalization + "; added " + words.size() + " words in EN");

    }

    private void prepareTestUserWithData() {
        Gson gson = new Gson();
        Collection<String> roles = new ArrayList<String>();
        roles.add("USER");
        errorRepository.deleteAll();
        mailRepository.deleteAll();
        dayRepository.deleteAll();
        userRepository.deleteAll();
        dictionaryWordRepository.deleteAll();

        User user = new User();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setUsername("words");
        long milis = TimeUnit.DAYS.toMillis(751L);
        long mili2 = DateTimeUtils.getCurrentUTCTime().getTime();
        user.setCreated(new Date(mili2 - milis));
        user.setEmail("word@dayinsix.com");
        user.setPasswordHash(encoder.encode("wordit"));
        user.setLastLogIn(DateTimeUtils.getCurrentUTCTime());
        user.setRoles(roles);

        user.setOptions(new HashMap<String, String>());
        user.setOptionsLastUpdate(new HashMap<String, Date>());

        user.getOptions().put(UserOptions.INPUT_TYPE, InputTypeEnum.WORDS.toString());
        user.getOptionsLastUpdate().put(UserOptions.INPUT_TYPE, DateTimeUtils.getCurrentUTCTime());

        user.getOptions().put(UserOptions.SHARE_STYLE, ShareStyleEnum.PUBLIC.name());
        user.getOptionsLastUpdate().put(UserOptions.SHARE_STYLE, DateTimeUtils.getCurrentUTCTime());

        user.getOptions().put(UserOptions.PROFILE_VISIBILITY, ShareStyleEnum.PUBLIC.name());
        user.getOptionsLastUpdate().put(UserOptions.PROFILE_VISIBILITY, DateTimeUtils.getCurrentUTCTime());

        user.setEnabled(true);

        // user.getOptions().put(UserOptions.TIMEZONE,
        // TimezoneUtils.getTimeZones().get("Europe/Berlin"));
        // user.getOptionsLastUpdate().put(UserOptions.TIMEZONE,
        // DateTimeUtils.getCurrentUTCTime());

        user.getOptions().put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.TWO_WEEKS.name()); // in
                                                                                                           // days
        user.getOptionsLastUpdate().put(UserOptions.NOTIFICATION_FREQUENCY, DateTimeUtils.getCurrentUTCTime());

        user = userRepository.save(user);
        user.setFollowingBy(new ArrayList<String>());
        user.setSharingWith(new ArrayList<String>());
        // System.out.println("User used:" + gson.toJson(user));

        ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
        words.add(new DictionaryWord(user, "Dodbre"));
        words.add(new DictionaryWord(user, "Dobtre"));
        words.add(new DictionaryWord(user, "Dobre"));
        words.add(new DictionaryWord(user, "Dwobre"));
        words.add(new DictionaryWord(user, "Dobre"));
        words.add(new DictionaryWord(user, "Dobrre"));
        words = (ArrayList<DictionaryWord>) dictionaryWordRepository.save(words);
        int los = 0;
        for (int i = 0; i < 751; i++) {
            Day day = new Day();
            los++;
            day.setShareStyle((ShareStyleEnum.values()[los % ShareStyleEnum.values().length].name()));
            day.setStoreDate(new Date(DateTimeUtils.getCurrentUTCTime().getTime() - TimeUnit.DAYS.toMillis(i)));
            day.setUserProfileVisibility(user.getOptions().get(UserOptions.PROFILE_VISIBILITY));
            day.setCreationDate(new Date(DateTimeUtils.getCurrentUTCTime().getTime() - TimeUnit.DAYS.toMillis(i)));
            if (Math.random() > 0.5) {
                ArrayList<Word> w = new ArrayList<Word>();
                for (int j = 0; j < 7; j++) {
                    Word wo = new Word();
                    wo.setStatus(StatusEnum.values()[(los + i + j) % StatusEnum.values().length].name());
                    wo.setValue(words.get(i % words.size()));
                    w.add(wo);
                }
                day.setWords(w);
            } else {
                Sentence sentence = new Sentence();
                sentence.setStatus("POSITIVE");
                sentence.setValue("Day of my life is " + i);
                day.setSentence(sentence);
            }
            day.setUser(user);
            day = dayRepository.save(day);
            // System.out.println("Day added: " + gson.toJson(day));
            i++;
        }

    }
    private void preloadOptions() {
        websiteOptionsRepository.deleteAll();
        WebsiteOptions w = new WebsiteOptions(OptionMap.TITLE_SUFFIX, " / Day in six");
        websiteOptionsRepository.save(w);
    }
    private void preloadMeta() {
        
        
        metaRepository.deleteAll();
        metaRepository.save(new Meta("/","Welcome","Here you can easily and quickly save every day of your life. You write six words per day, which allows you to simply reconstruct any day you've experienced.",""));
        metaRepository.save(new Meta("/faq","How it works","Scout out how to write in your diary, learn what you can share and who with so you can make the most of dayinsix.com. Register and enjoy using our website.",""));
        metaRepository.save(new Meta("/feedback","Feedback","You are welcome to give us feedback. We will be grateful for any piece of advice, report, or simply your opinion about our website so we can make it better.",""));

        metaRepository.save(new Meta("/remind","Reset your password","Forgot password to your diary at dayinsix.com? You can easily reset it on this page and we will send you a new one.",""));
        metaRepository.save(new Meta("/remindSuccess","Reset successful","Forgot password to your diary at dayinsix.com? You can easily reset it on this page and we will send you a new one.",""));
        metaRepository.save(new Meta("/explore","Explore this world","Search for interesting users at dayinsix.com. Find out what they've shared, get to know their life and start following them if you like their diaries.",""));
        metaRepository.save(new Meta("/stats","DayInSix in numbers","Comming soon! :)",""));
        metaRepository.save(new Meta("/s","{user}'s diary","",""));
        metaRepository.save(new Meta("/login","Login","Log in to write in your online diary at dayinsix.com. Forgotten password? Reset it! Don't have an account yet? Go to register page and create it.",""));
        metaRepository.save(new Meta("/logout","Logout","You've just been logged out. Remember to come back tomorrow in order to save every singular day of your life.",""));
        metaRepository.save(new Meta("/register","Register","Register at dayinsix.com - website which allows you to save your memories for future in very easy way so that you can make your own unique online diary.",""));
        metaRepository.save(new Meta("/archive","Archive - {dateString}","Your archive - {dateString}",""));
        
        
        metaRepository.save(new Meta("/followed","Your followers","Your followers",""));
        metaRepository.save(new Meta("/following","Followed by you","Followed by you",""));
        metaRepository.save(new Meta("/share","Users you share days with","Users you share days with",""));
        metaRepository.save(new Meta("/profile","Your profile","Your profile",""));
        
        metaRepository.save(new Meta("/day/add","Save the day","Save the day",""));
        metaRepository.save(new Meta("/day/edit","Change your day","Change your day",""));
        metaRepository.save(new Meta("/day/list","Your diary","Your diary",""));
        
        metaRepository.save(new Meta("/day/list{page}","Your diary / page {page}","Your diary / page {page}",""));
        metaRepository.save(new Meta("/s{page}","{user}'s diary / page {page}","{user}'s diary / page {page}",""));
    }

    private void prepareNotyficationTest() {

        User u = new User();
        long nowMilis = DateTimeUtils.getCurrentUTCTime().getTime();
        long dayMilis = TimeUnit.DAYS.toMillis(1L);
        HashMap<String, String> opt = new HashMap<String, String>();
        opt.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.DAY.name());
        u.setOptions(opt);
        u.setLastLogIn(new Date(nowMilis - dayMilis * 1 - 3000));
        u.setLastNotification(new Date(nowMilis - dayMilis * 2000));
        u.setUsername("Julia_true_true");
        u.setEmail("dayinsixdiary@gmail.com");
        u.setEnabled(true);
        userRepository.save(u);
        u = null;

        User u2 = new User();
        HashMap<String, String> opt2 = new HashMap<String, String>();
        opt2.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.THREE_MONTHS.name());
        u2.setOptions(opt2);
        u2.setLastLogIn(new Date(nowMilis - dayMilis * 94 - 3000));
        u2.setLastNotification(new Date(nowMilis - dayMilis * 2000));
        u2.setUsername("Julia2_true_true");
        u2.setEmail("dayinsixdiary@gmail.com");
        u2.setEnabled(true);
        userRepository.save(u2);
        u2 = null;

        User u4 = new User();
        HashMap<String, String> opt4 = new HashMap<String, String>();
        opt4.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.DAY.name());
        u4.setOptions(opt4);
        u4.setLastLogIn(new Date(nowMilis - dayMilis * 2));
        u4.setLastNotification(new Date(nowMilis - dayMilis * 2000));
        u4.setUsername("Julia4_true_false");
        u4.setEmail("dayinsixdiary@gmail.com");
        u4.setEnabled(false);
        userRepository.save(u4);
        u4 = null;

        User u3 = new User();
        HashMap<String, String> opt3 = new HashMap<String, String>();
        opt3.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.THREE_MONTHS.name());
        u3.setOptions(opt3);
        u3.setLastLogIn(new Date(nowMilis - dayMilis * 89 - 3000));
        u3.setLastNotification(new Date(nowMilis - dayMilis * 2000));
        u3.setUsername("Julia3_false_true");
        u3.setEmail("dayinsixdiary@gmail.com");
        u3.setEnabled(true);
        userRepository.save(u3);
        u3 = null;

        User u5 = new User();
        HashMap<String, String> opt5 = new HashMap<String, String>();
        opt5.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.NONE.name());
        u5.setOptions(opt5);
        u5.setLastLogIn(new Date(nowMilis - dayMilis * 94 - 3000));
        u5.setLastNotification(new Date(nowMilis - dayMilis * 2000));
        u5.setUsername("Julia5_true_true");
        u5.setEmail("dayinsixdiary@gmail.com");
        u5.setEnabled(true);
        userRepository.save(u5);
        u5 = null;
    }
}
