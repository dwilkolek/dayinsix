package eu.wilkolek.diary.service;

import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.MetaRepository;
import eu.wilkolek.diary.util.OptionMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.logging.Logger;

@Service
public class MetaService {


    private static final Logger logger = Logger.getLogger(MetaService.class.getName());

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private ErrorRepository errorRepository;

    HashMap<String, Meta> map = new HashMap<String, Meta>();

    public Meta getMeta(String url, Meta defaultMeta, HashMap<String, String> replacement, String customDescription) {

        Meta m = new Meta();
        if (map.containsKey(url)) {
            m = map.get(url);
        } else {
            m = metaRepository.findByUrl(url);
            map.put(url, m);
        }
        if (m != null) {
            m = m.clone();
            if (m != null) {
                return replaceParts(m, replacement, customDescription);
            } else {
                return defaultMeta;
            }
        } else {
            eu.wilkolek.diary.model.Error err = new eu.wilkolek.diary.model.Error(new Exception("Meta '" + url + "' not found"));
            errorRepository.save(err);
            return new Meta();
        }
    }

    private Meta replaceParts(Meta m, HashMap<String, String> replacement, String customDescription) {

        String suffix = optionsService.getOption(OptionMap.TITLE_SUFFIX);

        String title = m.getTitle();
        title = title + suffix;
        String description = m.getDescription();
        if (replacement == null) {
            m.setTitle(title);
            return m;
        }
        for (String k : replacement.keySet()) {
            title = title.replace(k, replacement.get(k));
            description = description.replace(k, replacement.get(k));
        }
        if (!StringUtils.isEmpty(customDescription)) {
            m.setDescription(customDescription);
        } else {
            m.setDescription(description);
        }
        m.setTitle(title);
        return m;

    }

    public Model updateModel(Model model, String url, Meta defaultMeta, HashMap<String, String> replacement, String customDescription) {
        Meta meta = this.getMeta(url, defaultMeta, replacement, customDescription);
        model.asMap().put("title", meta.getTitle());
        model.asMap().put("description", meta.getDescription());
        model.asMap().put("keywords", meta.getKeywords());
        return model;
    }

    public ModelAndView updateModel(ModelAndView model, String url, Meta defaultMeta, HashMap<String, String> replacement, String customDescription) {
        Meta meta = this.getMeta(url, defaultMeta, replacement, customDescription);
        model.getModelMap().put("title", meta.getTitle());
        model.getModelMap().put("description", meta.getDescription());
        model.getModelMap().put("keywords", meta.getKeywords());
        return model;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    private void clean() {
        this.map = new HashMap<String, Meta>();
        logger.info("OptionsService cleaned");
    }

    public void updateMeta(Meta meta) {
        meta = metaRepository.save(meta);
        if (map.containsKey(meta.getUrl())) {
            map.replace(meta.getUrl(), meta);
        } else {
            map.put(meta.getUrl(), meta);
        }
    }
}
