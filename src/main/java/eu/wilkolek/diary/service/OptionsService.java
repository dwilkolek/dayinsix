package eu.wilkolek.diary.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.wilkolek.diary.model.WebsiteOptions;
import eu.wilkolek.diary.repository.WebsiteOptionsRepository;

@Service
public class OptionsService {

    @Autowired
    private WebsiteOptionsRepository websiteOptionsRepository;
    
    private HashMap<String, String> opts = new HashMap<String, String>();
    
    public void updateOption(String key, String value){
        
        if (opts.containsKey(key)){
            opts.remove(key);
            opts.put(key, value);
        }
        
        WebsiteOptions o = websiteOptionsRepository.findByOpt(key);
        if (o == null){
            o = new WebsiteOptions(key, value);
        } else {
            o.setVal(value);
        }
        websiteOptionsRepository.save(o);
        
    }
    
    public String getOption(String key){
        if (opts.containsKey(key)){
            return opts.get(key);
        }
        WebsiteOptions o = websiteOptionsRepository.findByOpt(key);
        return o != null ? o.getVal() : null;
    }

   
}
