package eu.wilkolek.diary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.util.MetadataHelper;

@Controller
public class StaticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticController.class);

    @RequestMapping("/")
    public ModelAndView home() {
    	ModelAndView model = new ModelAndView("static/home");
    	model.getModelMap().addAttribute("title", MetadataHelper.title("Welcome"));
        LOGGER.debug("Getting home page");
        return model;
    }
    
    @RequestMapping("/faq")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView("static/faq");
        model.getModelMap().addAttribute("title", MetadataHelper.title("FAQ"));
        LOGGER.debug("Getting about page");
        return model;
    }
    
    @RequestMapping("/feedback")
    public ModelAndView contact() {
        ModelAndView model = new ModelAndView("static/feedback");
        model.getModelMap().addAttribute("title", MetadataHelper.title("Feedback"));
        LOGGER.debug("Getting feedback page");
        return model;
    }

}
