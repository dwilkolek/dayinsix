package eu.wilkolek.diary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.model.Sitemap;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.SitemapRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;

@Controller
public class SitemapController {

    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DayRepository dayRepository;
    
    @Autowired
    private SitemapRepository sitemapRepository;
    
    
    
    @RequestMapping("/sitemap.xml")
    public ModelAndView  sitemap(){
       
        ModelAndView model = new ModelAndView("sitemap");
       
//       generateSitemap();
       
       model.getModelMap().put("sitemap",sitemapRepository.getLatest());
       
        
       return model;
    }

    
    
}
