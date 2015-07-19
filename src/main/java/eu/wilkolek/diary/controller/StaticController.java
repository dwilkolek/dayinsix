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
    	model.addObject("title", "Welcome on Dayinsix");
        LOGGER.debug("Getting home page");
        return model;
    }
    
    @RequestMapping("/about")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView("static/about");
        model.addObject("title", "It's all about us & project");
        LOGGER.debug("Getting about page");
        return model;
    }
    
    @RequestMapping("/feedback")
    public ModelAndView contact() {
        ModelAndView model = new ModelAndView("static/feedback");
        model.addObject("title", "Send us feedback!");
        LOGGER.debug("Getting feedback page");
        return model;
    }

}
