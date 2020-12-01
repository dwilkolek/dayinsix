package eu.wilkolek.diary;

import eu.wilkolek.diary.model.*;
import eu.wilkolek.diary.repository.*;
import eu.wilkolek.diary.service.SitemapService;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.OptionMap;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(ApplicationStartup.class.getName());

    private DictionaryWordRepository dictionaryWordRepository;
    private UserRepository userRepository;
    private DayRepository dayRepository;
    private MailRepository mailRepository;
    private ErrorRepository errorRepository;
    private MetaRepository metaRepository;
    private WebsiteOptionsRepository websiteOptionsRepository;
    private SitemapRepository sitemapRepository;

    @Autowired
    private SitemapService sitemapService;

    @Autowired
    public ApplicationStartup(DictionaryWordRepository dictionaryWordRepository, UserRepository userRepository, DayRepository dayRepository,
                              ErrorRepository errorRepository, MailRepository mailRepository, MetaRepository metaRepository, WebsiteOptionsRepository websiteOptionsRepository, SitemapRepository sitemapRepository) {
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.dayRepository = dayRepository;
        this.userRepository = userRepository;
        this.mailRepository = mailRepository;
        this.errorRepository = errorRepository;
        this.metaRepository = metaRepository;
        this.websiteOptionsRepository = websiteOptionsRepository;
        this.sitemapRepository = sitemapRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!StringUtils.isEmpty(System.getProperty("dayinsix.clean"))) {
            this.cleanUp();
        }
        if (!StringUtils.isEmpty(System.getProperty("dayinsix.dictionary.words"))) {
            this.setupDictionaryEN();
        }
        if (!StringUtils.isEmpty(System.getProperty("dayinsix.init"))) {
            this.preloadOptions();
            this.preloadMeta();
        }

        sitemapService.generateSitemap();

    }

    private void cleanUp() {
        this.dictionaryWordRepository.deleteAll();
        this.dayRepository.deleteAll();
        this.userRepository.deleteAll();
        this.mailRepository.deleteAll();
        this.errorRepository.deleteAll();
        this.metaRepository.deleteAll();
        this.websiteOptionsRepository.deleteAll();
        this.sitemapRepository.deleteAll();

    }

    private void setupDictionaryEN() {
        String fileLocalization = System.getProperty("dictionary.words");
        InputStream resourceAsStream = this.getClass().getResourceAsStream("nouns.txt");
        LinkedList<DictionaryWord> words = new LinkedList<DictionaryWord>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                DictionaryWord word = new DictionaryWord();
                word.setValue(line);
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dictionaryWordRepository.saveAll(words);
        logger.info("dictionary word list: " + fileLocalization + "; added " + words.size() + " words in EN");

    }

    private void preloadOptions() {
        logger.info("preloadOptions - start");
        WebsiteOptions w1 = new WebsiteOptions(OptionMap.TITLE_SUFFIX, " / Day in six");
        websiteOptionsRepository.save(w1);
        websiteOptionsRepository.deleteAll();
        WebsiteOptions w = new WebsiteOptions(OptionMap.TITLE_SUFFIX, " / Day in six");
        websiteOptionsRepository.save(w);
        logger.info("preloadOptions - end");
    }

    private void preloadMeta() {
        logger.info("preloadMeta - start");
        metaRepository.save(new Meta("/", "Welcome", "Here you can easily and quickly save every day of your life. You write six words per day, which allows you to simply reconstruct any day you've experienced.", ""));


        metaRepository.deleteAll();
        metaRepository.save(new Meta("/", "Welcome", "Here you can easily and quickly save every day of your life. You write six words per day, which allows you to simply reconstruct any day you've experienced.", ""));
        metaRepository.save(new Meta("/faq", "How it works", "Scout out how to write in your diary, learn what you can share and who with so you can make the most of dayinsix.com. Register and enjoy using our website.", ""));
        metaRepository.save(new Meta("/feedback", "Feedback", "You are welcome to give us feedback. We will be grateful for any piece of advice, report, or simply your opinion about our website so we can make it better.", ""));

        metaRepository.save(new Meta("/remind", "Reset your password", "Forgot password to your diary at dayinsix.com? You can easily reset it on this page and we will send you a new one.", ""));
        metaRepository.save(new Meta("/remindSuccess", "Reset successful", "Forgot password to your diary at dayinsix.com? You can easily reset it on this page and we will send you a new one.", ""));
        metaRepository.save(new Meta("/explore", "Explore this world", "Search for interesting users at dayinsix.com. Find out what they've shared, get to know their life and start following them if you like their diaries.", ""));
        metaRepository.save(new Meta("/stats", "DayInSix in numbers", "Comming soon! :)", ""));
        metaRepository.save(new Meta("/s", "{user}'s diary", "", ""));
        metaRepository.save(new Meta("/login", "Login", "Log in to write in your online diary at dayinsix.com. Forgotten password? Reset it! Don't have an account yet? Go to register page and create it.", ""));
        metaRepository.save(new Meta("/logout", "Logout", "You've just been logged out. Remember to come back tomorrow in order to save every singular day of your life.", ""));
        metaRepository.save(new Meta("/register", "Register", "Register at dayinsix.com - website which allows you to save your memories for future in very easy way so that you can make your own unique online diary.", ""));
        metaRepository.save(new Meta("/archive", "Archive - {dateString}", "Your archive - {dateString}", ""));


        metaRepository.save(new Meta("/followed", "Your followers", "Your followers", ""));
        metaRepository.save(new Meta("/following", "Followed by you", "Followed by you", ""));
        //   metaRepository.save(new Meta("/follow","Followed by you","Followed by you",""));
        metaRepository.save(new Meta("/share", "Users you share days with", "Users you share days with", ""));
        metaRepository.save(new Meta("/profile", "Your profile", "Your profile", ""));

        metaRepository.save(new Meta("/day/add", "Save the day", "Save the day", ""));
        metaRepository.save(new Meta("/day/edit", "Change your day", "Change your day", ""));
        metaRepository.save(new Meta("/day/list", "Your diary", "Your diary", ""));

        metaRepository.save(new Meta("/day/list{page}", "Your diary / page {page}", "Your diary / page {page}", ""));
        metaRepository.save(new Meta("/s{page}", "{user}'s diary / page {page}", "{user}'s diary / page {page}", ""));

        metaRepository.save(new Meta("/error", "Error", "Error", ""));
        metaRepository.save(new Meta("/thankyou", "Thank you", "Thank you for registering", ""));
        metaRepository.save(new Meta("/activate", "Activate your account", "Activate your account at DayInSIx.com and start your journey", ""));


        logger.info("preloadMeta - end");
    }

}
