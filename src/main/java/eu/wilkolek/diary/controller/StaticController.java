package eu.wilkolek.diary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StaticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticController.class);

    @RequestMapping("/")
    public ModelAndView home() {
    	ModelAndView model = new ModelAndView("static/home");
        LOGGER.debug("Getting home page");
        return model;
    }
    
    @RequestMapping("/about")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView("static/about");
        LOGGER.debug("Getting home page");
        return model;
    }
    
    @RequestMapping("/contact")
    public ModelAndView contact() {
        ModelAndView model = new ModelAndView("static/contact");
        LOGGER.debug("Getting home page");
        return model;
    }

}
