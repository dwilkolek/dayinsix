package eu.wilkolek.diary.service;

import eu.wilkolek.diary.model.WebsiteOptions;
import eu.wilkolek.diary.repository.WebsiteOptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.logging.Logger;

@Service
public class OptionsService {

    private static final Logger logger = Logger.getLogger(OptionsService.class.getName());

    @Autowired
    private WebsiteOptionsRepository websiteOptionsRepository;

    private HashMap<String, String> opts = new HashMap<String, String>();

    public void updateOption(String key, String value) {

        if (opts.containsKey(key)) {
            opts.remove(key);
            opts.put(key, value);
        }

        WebsiteOptions o = websiteOptionsRepository.findByOpt(key);
        if (o == null) {
            o = new WebsiteOptions(key, value);
        } else {
            o.setVal(value);
        }
        websiteOptionsRepository.save(o);

    }

    public String getOption(String key) {
        if (opts.containsKey(key)) {
            return opts.get(key);
        }
        WebsiteOptions o = websiteOptionsRepository.findByOpt(key);
        return o != null ? o.getVal() : null;
    }

    @Scheduled(cron = "0 0 */1 * * ?")
    private void clean() {
        this.opts = new HashMap<String, String>();
        logger.info("OptionsService cleaned");
    }


}
